import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.*;

public class Midlet extends MIDlet {
    private final Display display;
    private final Game2048Canvas canvas;
    public Menu gameMenu;

    public Midlet() {
        this.display = Display.getDisplay(this);
        this.canvas = new Game2048Canvas(this);
        gameMenu = new Menu();
        gameMenu.midlet = this;
    }

    public void startApp() {
        OpenMenu();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        canvas.Stop();
        display.setCurrent(null);
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
        notifyDestroyed();
    }
}
