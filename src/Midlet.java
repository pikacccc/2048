import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.*;

public class Midlet extends MIDlet implements IExit {
    private final Display display;
    public final Game2048Canvas canvas;
    public GameOver gameOver;
    public Menu gameMenu;
    public RecordStores rs;

    public Midlet() {
        rs = new RecordStores("ES.2048", 1);
        this.display = Display.getDisplay(this);
        this.canvas = new Game2048Canvas(this);
        gameMenu = new Menu();
        gameMenu.midlet = this;
        gameOver = new GameOver();
        gameOver.midlet = this;
    }

    public void startApp() {
        int nr = rs.getNumRecords();
        if (nr > 0) canvas.HISCORE = rs.getRecord(1);
        OpenMenu();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        System.out.println("========Exit========");
        rs.setRecord(1, canvas.HISCORE);
        rs.closeRecords();
        canvas.Stop();
        display.setCurrent(null);
        notifyDestroyed();
    }

    public void OpenMenu() {
        gameMenu.start();
        display.setCurrent(gameMenu);
    }

    public void CloseMenu() {
        gameMenu.stop();
    }

    public void StartGame() {
        canvas.Start();
        display.setCurrent(canvas);
    }

    public void CloseGame() {
        canvas.Stop();
    }

    public void exitMIDlet() {
        destroyApp(true);
    }

    public void OpenGameOver() {
        gameOver.start();
        display.setCurrent(gameOver);
    }

    public void CloseGameOver() {
        gameOver.stop();
    }

    public void Exit() {
        exitMIDlet();
    }
}
