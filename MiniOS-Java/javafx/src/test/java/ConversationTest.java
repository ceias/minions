import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/** Launch the real JavaFX scene using software rendering, then exercise the UI path. */
public class ConversationTest {
    static MiniOSFX app;
    static void fx(Runnable action) throws Exception {
        CountDownLatch done=new CountDownLatch(1);
        AtomicReference<Throwable> error=new AtomicReference<>();
        Platform.runLater(()->{try { action.run(); } catch(Throwable e) { error.set(e); } finally { done.countDown(); }});
        assertTrue(done.await(15,TimeUnit.SECONDS),"FX action timed out");
        if(error.get()!=null) throw new AssertionError(error.get());
    }
    @BeforeAll static void launch() throws Exception {
        CountDownLatch ready=new CountDownLatch(1);
        Platform.startup(()->{Platform.setImplicitExit(false);ready.countDown();});
        assertTrue(ready.await(15,TimeUnit.SECONDS));
        fx(()->{app=new MiniOSFX();app.start(new Stage());});
        if (System.getProperty("minios.snapshot") != null) fx(() -> {
            javafx.scene.image.WritableImage image=app.stage.getScene().snapshot(null);
            int w=(int)image.getWidth(), h=(int)image.getHeight();
            int[] pixels=new int[w*h];
            image.getPixelReader().getPixels(0,0,w,h,javafx.scene.image.PixelFormat.getIntArgbInstance(),pixels,0,w);
            java.awt.image.BufferedImage png=new java.awt.image.BufferedImage(w,h,java.awt.image.BufferedImage.TYPE_INT_ARGB);
            png.setRGB(0,0,w,h,pixels,0,w);
            try { javax.imageio.ImageIO.write(png,"png",new java.io.File(System.getProperty("minios.snapshot"))); }
            catch(java.io.IOException e) { throw new RuntimeException(e); }
        });
    }
    @AfterAll static void close() throws Exception { if(app!=null) fx(()->{app.stage.close();app.stop();}); }
    static void waitForRun() throws Exception {
        long until=System.nanoTime()+TimeUnit.SECONDS.toNanos(15);
        boolean[] running={true};
        do { fx(()->running[0]=app.busy); if(!running[0]) return; Thread.sleep(20); } while(System.nanoTime()<until);
        fail("Simulation did not finish");
    }
    @Test void auditedAlgorithmRegressions() { AuditRegressionTests.main(new String[0]); }
    @Test void everyModuleProducesRealResults() throws Exception {
        for(Modules.Module module:app.modules) {
            fx(()->{app.lastResult="";app.open(module,null).run();});
            waitForRun();
            fx(()->assertTrue(app.lastResult.length()>20,module.name));
        }
    }
    @Test void commandsAndDraftPreservation() throws Exception {
        fx(()->{
            app.route("srtf");
            assertEquals("CPU Scheduling",app.active.module.name);
            assertEquals(Cpu.NAMES[2],app.active.options().get("Algorithm"));
            app.active.input.setText("CUSTOM 0 3");
            app.route("lru"); assertEquals("Page Replacement",app.active.module.name);
            assertEquals(Pages.NAMES[6],app.active.options().get("Algorithm"));
            app.route("srtf"); assertEquals("CUSTOM 0 3",app.active.input.getText());
            app.clear(); app.route("srtf"); assertEquals("CUSTOM 0 3",app.active.input.getText());
            int count=app.history.getChildren().size();app.route("unknown command");
            assertTrue(app.history.getChildren().size()>count);
            app.route("help");
            javafx.scene.layout.HBox help=(javafx.scene.layout.HBox)app.history.getChildren().get(app.history.getChildren().size()-1);
            String message=((javafx.scene.control.Label)help.getChildren().get(1)).getText();
            assertTrue(message.contains("HOW TO USE"));
            assertTrue(message.contains("Commands:"));
            assertFalse(message.toLowerCase().contains("rubric"));
            app.home(); assertNull(app.active);
        });
    }
    @Test void validationRecoversAndRunCommandExecutes() throws Exception {
        fx(()->{app.route("srtf");app.active.input.clear();app.active.run();});
        waitForRun();
        fx(()->{
            assertTrue(app.status.getText().contains("check your input"));
            assertFalse(app.active.run.isDisabled());
            app.active.input.setText("P1 0 3");app.route("run srtf");
        });
        waitForRun();
        fx(()->assertTrue(app.lastResult.contains("P1")));
    }
}
