import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

/** Conversation UI. All calculations are delegated to the existing Modules runners. */
public class MiniOSFX extends Application {
    final VBox history = new VBox(12);
    final ScrollPane chat = new ScrollPane(history);
    final TextField command = new TextField();
    final Label status = new Label("Ready · Choose a module or type help");
    final List<Modules.Module> modules = Modules.all();
    final Map<String, Editor> editors = new LinkedHashMap<>();
    final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "mini-os-simulator"); t.setDaemon(true); return t;
    });
    Image sheet;
    Stage stage;
    Editor active;
    boolean busy;
    String lastResult = "";
    double dragX, dragY;

    @Override public void start(Stage window) {
        stage = window;
        sheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/ui-sheet.png")));
        BorderPane frame = new BorderPane();
        frame.getStyleClass().add("window");
        frame.setTop(titleBar());
        history.setPadding(new Insets(18));
        chat.setFitToWidth(true);
        chat.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chat.getStyleClass().add("conversation");
        HBox conversation = new HBox(18, chat, sideBar());
        HBox.setHgrow(chat, Priority.ALWAYS);
        VBox center = new VBox(12, conversation, selectors(), composer(), status);
        VBox.setVgrow(conversation, Priority.ALWAYS);
        center.setPadding(new Insets(18));
        frame.setCenter(center);
        Scene scene = new Scene(new StackPane(frame), 1180, 850);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/mini-os.css")).toExternalForm());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Mini OS · Conversational Simulator");
        stage.setMinWidth(960); stage.setMinHeight(700);
        stage.setScene(scene);
        stage.setOnHidden(e -> worker.shutdownNow());
        home();
        stage.show();
    }

    private HBox titleBar() {
        Label title = new Label("✦  Mini OS chat");
        title.getStyleClass().add("window-title");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button minimize = button("—", () -> stage.setIconified(true));
        Button maximize = button("□", () -> stage.setMaximized(!stage.isMaximized()));
        Button close = button("×", () -> stage.close());
        minimize.setAccessibleText("Minimize window"); maximize.setAccessibleText("Toggle maximize"); close.setAccessibleText("Close window");
        HBox bar = new HBox(8, title, spacer, minimize, maximize, close);
        bar.setAlignment(Pos.CENTER_LEFT); bar.getStyleClass().add("title-bar");
        bar.setOnMousePressed(e -> { dragX = e.getSceneX(); dragY = e.getSceneY(); });
        bar.setOnMouseDragged(e -> { if (!stage.isMaximized()) { stage.setX(e.getScreenX()-dragX); stage.setY(e.getScreenY()-dragY); } });
        return bar;
    }

    private VBox sideBar() {
        ImageView frog = portrait(false, 88);
        Button help = button("?", this::help);
        Button home = button("⌂", this::home);
        Button clear = button("×", this::clear);
        help.setText(""); home.setText(""); clear.setText("");
        help.setGraphic(icon(686, 691)); home.setGraphic(icon(532, 691)); clear.setGraphic(icon(532, 545));
        help.setAccessibleText("Mini OS help"); home.setAccessibleText("Home"); clear.setAccessibleText("Clear conversation");
        help.getStyleClass().add("icon-button"); home.getStyleClass().add("icon-button"); clear.getStyleClass().add("icon-button");
        VBox side = new VBox(14, frog, help, home, clear);
        side.setAlignment(Pos.TOP_CENTER); side.setPrefWidth(95);
        return side;
    }

    /** Viewports reference the original sheet without altering the supplied artwork. */
    private ImageView portrait(boolean bunny, int size) {
        ImageView view = new ImageView(sheet);
        view.setViewport(new Rectangle2D(1328, bunny ? 317 : 111, 144, 144));
        view.setFitWidth(size); view.setFitHeight(size); view.setSmooth(false);
        view.setAccessibleText(bunny ? "Bunny user" : "Frog assistant");
        return view;
    }

    private ImageView icon(int x, int y) {
        ImageView view = new ImageView(sheet);
        view.setViewport(new Rectangle2D(x,y,90,80));
        view.setFitWidth(32); view.setFitHeight(30); view.setSmooth(false);
        return view;
    }

    private VBox selectors() {
        VBox groups = new VBox(6);
        String[][] labels = {{"FCFS (FIFO)", "SJF", "SRTF", "Priority", "Preemptive priority", "Round robin", "MLQ", "MLFQ"},
            {"FIFO", "Optimal", "NRU", "Second chance", "CLOCK", "NFU", "LRU", "Random", "Working set"}};
        for (int g=0; g<2; g++) {
            FlowPane row = new FlowPane(6, 6);
            Modules.Module m = modules.get(g);
            for (int i=0; i<labels[g].length; i++) {
                final String algorithm = m.fields[0].choices[i];
                row.getChildren().add(button(labels[g][i], () -> open(m, algorithm)));
            }
            groups.getChildren().addAll(groupLabel(g==0 ? "CPU scheduling" : "Page replacement"), row);
        }
        FlowPane rest = new FlowPane(6, 6);
        for (Modules.Module m : modules) if (!m.name.equals("CPU Scheduling") && !m.name.equals("Page Replacement"))
            rest.getChildren().add(button(m.name, () -> open(m, null)));
        groups.getChildren().addAll(groupLabel("Memory, disk & jobs"), rest);
        return groups;
    }

    private Label groupLabel(String text) { Label l = new Label(text); l.getStyleClass().add("group-label"); return l; }
    private HBox composer() {
        command.setPromptText("Type a command, e.g. run srtf, run lru, banker, help");
        command.setAccessibleText("Chat command");
        command.setOnAction(e -> send());
        Button send = button("Send", this::send); send.getStyleClass().add("primary");
        HBox box = new HBox(12, portrait(true, 48), command, send);
        box.setAlignment(Pos.CENTER); HBox.setHgrow(command, Priority.ALWAYS);
        return box;
    }

    private Button button(String text, Runnable action) {
        Button b = new Button(text); b.setOnAction(e -> action.run()); return b;
    }

    private void bubble(String text, boolean user) {
        Label message = new Label(text); message.setWrapText(true); message.setMaxWidth(Double.MAX_VALUE);
        message.getStyleClass().add(user ? "user-bubble" : "assistant-bubble");
        HBox line = new HBox(10, portrait(user, 36), message);
        HBox.setHgrow(message, Priority.ALWAYS); history.getChildren().add(line); scroll();
    }
    private void scroll() { Platform.runLater(() -> chat.setVvalue(1)); }
    void home() {
        if (active != null) history.getChildren().remove(active.card);
        active = null;
        bubble("Hi, I’m the Mini OS Frog! Choose an algorithm below. Edit the sample data in its input card, then Run. All results come from the Java simulators.", false);
    }
    void clear() {
        history.getChildren().clear(); active = null;
        bubble("Conversation cleared. Your module inputs are kept. Choose a module to continue.", false);
    }
    void help() { bubble(AppHelp.TEXT + "\nCommands: help, home, clear, run, run srtf, run lru, banker, deadlock, paging, segmentation, fragmentation, disk, multiprogramming.\nThis is a local command interface; it does not call an AI service.", false); }

    Editor open(Modules.Module module, String algorithm) {
        if (active != null) history.getChildren().remove(active.card);
        active = editors.computeIfAbsent(module.name, k -> new Editor(module));
        if (algorithm != null) active.set("Algorithm", algorithm);
        history.getChildren().add(active.card);
        status.setText("Editing " + module.name + " · Small sample loaded initially; change any input");
        scroll(); return active;
    }

    void send() {
        String text = command.getText().trim(); if (text.isEmpty()) return;
        command.clear(); bubble(text, true); route(text);
    }
    void route(String text) {
        String key = text.toLowerCase(Locale.ROOT).trim();
        if (key.equals("help")) { help(); return; }
        if (key.equals("home")) { home(); return; }
        if (key.equals("clear")) { clear(); return; }
        if (key.equals("run")) { if (active != null) active.run(); else bubble("Choose an algorithm first, then enter your data.", false); return; }
        boolean run = key.startsWith("run "); if (run) key = key.substring(4).trim();
        String[] cpu = {"fcfs", "sjf", "srtf", "priority", "preemptive priority", "round robin", "mlq", "mlfq"};
        String[] pages = {"fifo", "optimal", "nru", "second chance", "clock", "nfu", "lru", "random", "working set"};
        if (key.equals("rr")) key="round robin";
        if (key.equals("opt")) key="optimal";
        for (int group=0; group<2; group++) {
            String[] keys = group==0 ? cpu : pages;
            for (int i=0; i<keys.length; i++) if (keys[i].equals(key)) {
                Editor e = open(modules.get(group), modules.get(group).fields[0].choices[i]); if (run) e.run(); return;
            }
        }
        String[] aliases = {"cpu", "pages", "deadlock", "banker", "paging", "segmentation", "fragmentation", "disk", "multiprogramming"};
        for (int i=0; i<modules.size(); i++) if (key.equals(aliases[i]) || key.equals(modules.get(i).name.toLowerCase(Locale.ROOT))) {
            Editor e=open(modules.get(i), null); if (run) e.run(); return;
        }
        bubble("I didn’t recognize that command. Try help, run srtf, run lru, banker or choose a button below.", false);
    }

    final class Editor {
        final Modules.Module module;
        final VBox card = new VBox(10);
        final TextArea input = new TextArea();
        final Map<String, Control> controls = new LinkedHashMap<>();
        final Button run = button("Run simulation", this::run);
        Editor(Modules.Module m) {
            module=m; card.getStyleClass().add("input-card");
            Label title=groupLabel(m.name + " · EDITABLE INPUT");
            Label hint=new Label(m.hint + "\n" + m.how); hint.setWrapText(true);
            GridPane fields=new GridPane(); fields.setHgap(12); fields.setVgap(8);
            ColumnConstraints valueColumn=new ColumnConstraints(); valueColumn.setHgrow(Priority.ALWAYS);
            fields.getColumnConstraints().addAll(new ColumnConstraints(), valueColumn);
            int row=0;
            for (Modules.Field f : m.fields) {
                Control c;
                if (f.choices != null) { ComboBox<String> cb=new ComboBox<>(); cb.getItems().addAll(f.choices); cb.setValue(f.def); cb.setMaxWidth(Double.MAX_VALUE); c=cb; }
                else c=new TextField(f.def);
                c.setAccessibleText(f.label);
                controls.put(f.label,c); fields.addRow(row++,new Label(f.label),c);
            }
            input.setPrefRowCount(6); input.setWrapText(false); input.setAccessibleText(m.inputLabel);
            FlowPane buttons=new FlowPane(8,8);
            buttons.getChildren().addAll(button("Small",()->load(m.small)),button("Large",()->load(m.large)),button("Edge",()->load(m.edge)),button("Clear input",()->input.clear()),run);
            run.getStyleClass().add("primary");
            card.getChildren().addAll(title,hint,fields,new Label(m.inputLabel),input,buttons);
            load(m.small);
        }
        @SuppressWarnings("unchecked") void set(String name,String value) {
            Control c=controls.get(name);
            if (c instanceof TextField f) f.setText(value);
            else if (c instanceof ComboBox) ((ComboBox<String>)c).setValue(value);
        }
        void load(Modules.Sample s) { input.setText(s.text); s.opts.forEach(this::set); }
        Map<String,String> options() {
            Map<String,String> opts=new LinkedHashMap<>();
            controls.forEach((k,c)->opts.put(k,c instanceof TextField f ? f.getText().trim() : String.valueOf(((ComboBox<?>)c).getValue())));
            return opts;
        }
        void run() {
            if (busy) { bubble("A simulation is still running. Please wait before starting another.",false); return; }
            List<String> lines=Util.lineList(input.getText()); Map<String,String> opts=options();
            busy=true; editors.values().forEach(e->e.run.setDisable(true));
            status.setText("Frog is calculating · " + module.name);
            Task<String> task=new Task<>() { @Override protected String call() { return module.run.run(lines,opts); } };
            task.setOnSucceeded(e->{ finish(); showResult(module.name,task.getValue()); });
            task.setOnFailed(e->{ finish(); Throwable error=task.getException(); bubble("Input / simulation error: " + error.getMessage(),false); status.setText("Please check your input and try again."); });
            worker.submit(task);
        }
        private void finish() { busy=false; editors.values().forEach(e->e.run.setDisable(false)); }
    }

    private void showResult(String name,String result) {
        lastResult=result;
        TextArea output=new TextArea(result); output.setEditable(false); output.setWrapText(false); output.setPrefRowCount(12); output.getStyleClass().add("results");
        output.setAccessibleText(name + " simulation results");
        Button copy=button("Copy results",()->{ ClipboardContent c=new ClipboardContent(); c.putString(result); Clipboard.getSystemClipboard().setContent(c); status.setText("Results copied."); });
        Button save=button("Save results",()->{
            FileChooser fc=new FileChooser(); fc.setInitialFileName(name.toLowerCase(Locale.ROOT).replace(' ','_')+".txt");
            java.io.File file=fc.showSaveDialog(stage);
            if (file!=null) try { Files.write(file.toPath(),result.getBytes(StandardCharsets.UTF_8)); status.setText("Results saved."); }
            catch (Exception ex) { bubble("Could not save: " + ex.getMessage(),false); }
        });
        VBox card=new VBox(8,groupLabel(name + " · RESULTS"),output,new HBox(8,copy,save)); card.getStyleClass().add("result-card");
        history.getChildren().add(card); status.setText("Completed · " + name); scroll();
    }
    @Override public void stop() { worker.shutdownNow(); }
    public static void main(String[] args) { launch(args); }
}
