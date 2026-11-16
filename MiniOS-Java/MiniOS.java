import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Mini OS: CPU scheduling, page replacement, deadlock, Banker's, paging, segmentation, fragmentation, disk scheduling and multiprogramming. */
public class MiniOS extends JFrame {
    static final Color CREAM = new Color(0xFD, 0xF1, 0xD3), PAPER = new Color(0xFF, 0xFD, 0xF6), ROSE = new Color(0xC8, 0x8A, 0x80),
        INK = new Color(0x4A, 0x1D, 0x2A), CORAL = new Color(0xF4, 0x80, 0x6B), BROWN = new Color(0x8A, 0x5A, 0x4A);
    static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13), BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);

    /** One simulator page: options, input, Run, samples, and the results area. */
    static class ModulePanel extends JPanel {
        final Modules.Module m;
        final Map<String, JComponent> comps = new LinkedHashMap<String, JComponent>();
        final JTextArea input = new JTextArea(12, 36), out = new JTextArea();
        final JLabel status = new JLabel("Ready - load a sample or type your own input, then press Run.");
        String lastOutput = "";

        ModulePanel(Modules.Module module) {
            this.m = module;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
            setBackground(CREAM);

            JLabel title = new JLabel(m.name);
            title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            title.setForeground(INK);
            JTextArea hint = note(m.hint + "\nHow it works: " + m.how);
            JPanel north = new JPanel(new BorderLayout(0, 4));
            north.setOpaque(false);
            north.add(title, BorderLayout.NORTH);
            north.add(hint, BorderLayout.CENTER);
            add(north, BorderLayout.NORTH);

            // left: options + input + buttons
            JPanel left = new JPanel(new BorderLayout(0, 8));
            left.setOpaque(false);
            JPanel opts = new JPanel(new GridBagLayout());
            opts.setOpaque(false);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(2, 0, 2, 8);
            c.anchor = GridBagConstraints.WEST;
            int row = 0;
            for (Modules.Field fld : m.fields) {
                c.gridx = 0; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
                JLabel l = new JLabel(fld.label);
                l.setForeground(INK);
                opts.add(l, c);
                JComponent w;
                if (fld.choices != null) { JComboBox<String> cb = new JComboBox<String>(fld.choices); cb.setSelectedItem(fld.def); w = cb; }
                else { JTextField tf = new JTextField(fld.def, 14); w = tf; }
                c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
                opts.add(w, c);
                comps.put(fld.label, w);
                row++;
            }
            JPanel leftTop = new JPanel(new BorderLayout());
            leftTop.setOpaque(false);
            leftTop.add(opts, BorderLayout.NORTH);
            left.add(leftTop, BorderLayout.NORTH);

            JLabel il = new JLabel(m.inputLabel + "  (one entry per line, # = comment)");
            il.setForeground(BROWN);
            input.setFont(MONO);
            input.setBackground(PAPER);
            input.setForeground(INK);
            input.getInputMap().put(KeyStroke.getKeyStroke("control ENTER"), "run");
            input.getActionMap().put("run", new AbstractAction() { public void actionPerformed(ActionEvent e) { execute(); } });
            JPanel mid = new JPanel(new BorderLayout(0, 4));
            mid.setOpaque(false);
            mid.add(il, BorderLayout.NORTH);
            mid.add(new JScrollPane(input), BorderLayout.CENTER);
            left.add(mid, BorderLayout.CENTER);

            JPanel btns = new JPanel(new java.awt.GridLayout(0, 4, 4, 4));
            btns.setOpaque(false);
            JButton run = button("Run (Ctrl+Enter)", true);
            run.addActionListener(e -> execute());
            JPanel runRow = new JPanel(new BorderLayout());
            runRow.setOpaque(false);
            runRow.add(run, BorderLayout.CENTER);
            addSample(btns, "Small", m.small);
            addSample(btns, "Large", m.large);
            addSample(btns, "Edge", m.edge);
            JButton clear = button("Clear", false);
            clear.addActionListener(e -> { input.setText(""); status.setText("Input cleared."); });
            btns.add(clear);
            JPanel bottom = new JPanel(new BorderLayout(0, 6));
            bottom.setOpaque(false);
            bottom.add(runRow, BorderLayout.NORTH);
            bottom.add(btns, BorderLayout.CENTER);
            left.add(bottom, BorderLayout.SOUTH);

            // right: results
            JPanel right = new JPanel(new BorderLayout(0, 6));
            right.setOpaque(false);
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel rl = new JLabel("RESULTS");
            rl.setForeground(BROWN);
            rl.setFont(BOLD);
            top.add(rl, BorderLayout.WEST);
            JPanel tb = new JPanel();
            tb.setOpaque(false);
            JButton copy = button("Copy", false), save = button("Save", false);
            copy.addActionListener(e -> {
                try { Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(out.getText()), null); status.setText("Results copied to clipboard."); }
                catch (Exception ex) { status.setText("Could not copy: " + ex.getMessage()); }
            });
            save.addActionListener(e -> save());
            tb.add(copy); tb.add(save);
            top.add(tb, BorderLayout.EAST);
            right.add(top, BorderLayout.NORTH);
            out.setEditable(false);
            out.setFont(MONO);
            out.setBackground(PAPER);
            out.setForeground(INK);
            out.setText("Results will appear here.\n\nTip: press Small, then Run.");
            right.add(new JScrollPane(out), BorderLayout.CENTER);

            left.setPreferredSize(new Dimension(430, 100));
            right.setPreferredSize(new Dimension(560, 100));
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
            split.setResizeWeight(0.3);
            split.setBorder(null);
            split.setOpaque(false);
            add(split, BorderLayout.CENTER);
            status.setForeground(BROWN);
            add(status, BorderLayout.SOUTH);
        }

        private JTextArea note(String text) {
            JTextArea t = new JTextArea(text);
            t.setEditable(false); t.setLineWrap(true); t.setWrapStyleWord(true);
            t.setOpaque(false); t.setForeground(BROWN); t.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            return t;
        }

        private JButton button(String text, boolean accent) {
            JButton b = new JButton(text);
            b.setFocusPainted(false);
            b.setBackground(accent ? CORAL : PAPER);
            b.setForeground(INK);
            b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(accent ? INK : ROSE, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            b.setFont(BOLD);
            return b;
        }

        private void addSample(JPanel p, String label, final Modules.Sample s) {
            JButton b = button(label, false);
            b.addActionListener(e -> load(s));
            p.add(b);
        }

        /** Put a sample's text and options into the page. */
        void load(Modules.Sample s) {
            input.setText(s.text);
            for (Map.Entry<String, String> e : s.opts.entrySet()) {
                JComponent w = comps.get(e.getKey());
                if (w instanceof JComboBox) ((JComboBox<?>) w).setSelectedItem(e.getValue());
                else if (w instanceof JTextField) ((JTextField) w).setText(e.getValue());
            }
            input.setCaretPosition(0);
            status.setText("Input loaded - press Run.");
        }

        /** Run the module on the current input and show the results. Never throws. */
        String execute() {
            Map<String, String> o = new LinkedHashMap<String, String>();
            for (Map.Entry<String, JComponent> e : comps.entrySet()) {
                JComponent w = e.getValue();
                o.put(e.getKey(), w instanceof JComboBox ? String.valueOf(((JComboBox<?>) w).getSelectedItem()) : ((JTextField) w).getText().trim());
            }
            String text;
            long t0 = System.nanoTime();
            try {
                text = m.run.run(Util.lineList(input.getText()), o);
                status.setText(String.format("Done in %.1f ms - %d lines of output", (System.nanoTime() - t0) / 1e6, text.split("\n").length));
            } catch (IllegalArgumentException ex) {
                text = "Invalid input: " + ex.getMessage();
                status.setText(text);
            } catch (RuntimeException ex) {
                text = "Unexpected problem: " + ex;
                status.setText(text);
            }
            out.setText(text);
            out.setCaretPosition(0);
            lastOutput = text;
            return text;
        }

        private void save() {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(m.name.toLowerCase().replace(' ', '_').replace("'", "") + ".txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    Files.write(fc.getSelectedFile().toPath(), out.getText().getBytes(StandardCharsets.UTF_8));
                    status.setText("Saved to " + fc.getSelectedFile());
                } catch (Exception ex) {
                    status.setText("Could not save: " + ex.getMessage());
                }
            }
        }
    }

    public MiniOS() {
        super("Mini OS - Operating System Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 780);
        setMinimumSize(new Dimension(980, 620));
        getContentPane().setBackground(CREAM);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("  Mini OS   |   CPU  |  Memory  |  Deadlock  |  Disk");
        header.setOpaque(true);
        header.setBackground(ROSE);
        header.setForeground(CREAM);
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.add(header, BorderLayout.CENTER);
        JButton rubric = new JButton("Rubric / Demo Guide");
        rubric.addActionListener(e -> {
            JTextArea guide = new JTextArea(RubricGuide.TEXT, 28, 85);
            guide.setEditable(false);
            guide.setFont(MONO);
            guide.setCaretPosition(0);
            javax.swing.JOptionPane.showMessageDialog(this, new JScrollPane(guide),
                "Rubric and presentation guide", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });
        headerRow.add(rubric, BorderLayout.EAST);
        add(headerRow, BorderLayout.NORTH);

        final CardLayout cards = new CardLayout();
        final JPanel center = new JPanel(cards);
        final DefaultListModel<String> names = new DefaultListModel<String>();
        for (Modules.Module mod : Modules.all()) {
            names.addElement(mod.name);
            center.add(new ModulePanel(mod), mod.name);
        }
        final JList<String> list = new JList<String>(names);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        list.setBackground(PAPER);
        list.setForeground(INK);
        list.setSelectionBackground(CORAL);
        list.setSelectionForeground(INK);
        list.setFixedCellHeight(38);
        list.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) cards.show(center, list.getSelectedValue()); });
        JScrollPane side = new JScrollPane(list);
        side.setPreferredSize(new Dimension(230, 100));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, ROSE));
        add(side, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        list.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) { /* keep default */ }
                new MiniOS().setVisible(true);
            }
        });
    }
}
