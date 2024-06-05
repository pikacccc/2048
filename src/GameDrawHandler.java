import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.util.Hashtable;

public class GameDrawHandler {
    private Image bg;
    private Image gameBg;
    private Image scoreTitle;
    private Image[] numbers;
    private DrawNumberHandler numHandler;
    private Game2048Canvas canvas;
    private int width;
    private int height;

    private int center_x;
    private int center_y;
    private int bg_x;
    private int bg_y;
    private int gameBg_x;
    private int gameBg_y;
    private int gameStart_x;
    private int gameStart_y;
    private int scoreTitle_x;
    private int scoreTitle_y;
    private int Score_x;
    private int Score_y;

    public GameDrawHandler(Game2048Canvas canvas) {
        this.canvas = canvas;
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        InitImages();
        InitCoordinates();
    }

    public void InitCoordinates() {
        center_x = width / 2;
        center_y = height / 2;
        bg_x = center_x - bg.getWidth() / 2;
        bg_y = center_y - bg.getHeight() / 2;
        gameBg_x = center_x - gameBg.getWidth() / 2;
        gameBg_y = center_y - gameBg.getHeight() / 2;
        Score_x = center_x + 350;
        Score_y = center_y + 50;
        scoreTitle_x = center_x - scoreTitle.getHeight() / 2 + 350;
        scoreTitle_y = center_y - scoreTitle.getHeight() / 2 - 50;
        gameStart_x = center_x - 300;
        gameStart_y = center_y - 300;
    }

    public void InitImages() {
        bg = Util.LoadImg("/background.png");
        gameBg = Util.LoadImg("/bg_play.png");
        scoreTitle = Util.LoadImg("/title_pt.png");
        Image tempNumber = Util.LoadImg("/2048.png");
        numHandler = new DrawNumberHandler("/number.png", 32, 48);
        numbers = new Image[12];
        for (int i = 0; i < 12; ++i) {
            numbers[i] = Image.createImage(tempNumber, i * 150, 0, 150, 150, Sprite.TRANS_NONE);
        }

    }

    public void Draw(Graphics g) {
        g.drawImage(bg, bg_x, bg_y, 0);
        g.drawImage(gameBg, gameBg_x, gameBg_y, 0);
        short[][] state = canvas.state;
        for (int i = 0; i < state.length; i++) {
            short[] ses = state[i];
            for (int j = 0; j < ses.length; j++) {
                int val = (int) ses[j];
                if (val != 0) {
                    int a = findN(val);
                    g.drawImage(numbers[a], gameStart_x + i * 150, gameStart_y + j * 150, 0);
                }
            }
        }
        g.drawImage(scoreTitle, scoreTitle_x, scoreTitle_y, 0);
        numHandler.ShowNumber(g, canvas.score, Score_x, Score_y, AlignmentType.Left);
    }

    public int findN(int num) {
        if (num % 2 != 0 || num < 2) {
            return -1; // 输入的数不是2的n次方等差数列中的项
        }

        int n = 0;
        while (num > 2) {
            num = num / 2;
            n++;
        }

        return n;
    }
}
