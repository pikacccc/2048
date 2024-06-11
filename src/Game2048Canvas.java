import com.sun.j2me.global.NumberFormat;

import java.util.Random;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

public class Game2048Canvas extends GameCanvas implements CommandListener {
    public int HISCORE = 0;
    int freeCount;
    private final Random random = new Random();
    public short[][] state = new short[4][4];
    public int score;
    private GameDrawHandler drawHandler;

    public boolean isPlay = false;
    public Midlet midlet;


    public Game2048Canvas(Midlet midlet) {
        super(false);
        this.setFullScreenMode(true);
        this.midlet = midlet;
        drawHandler = new GameDrawHandler(this);
    }

    public void Start() {
        this.isPlay = true;
        state = new short[4][4];
        freeCount = 16;
        score = 0;
        insertNew();
        insertNew();
    }

    public void Stop() {
        this.isPlay = false;
    }

    protected void keyPressed(int keyCode) {
        if (!isPlay) return;
        boolean isGameOver = isGameOver();
        if (isGameOver) {
            if (score > HISCORE) {
                HISCORE = score;
            }
            midlet.OpenGameOver();
            midlet.CloseGame();
            return;
        }
        int action = getGameAction(keyCode);
        if (keyCode == -6 || keyCode == 8 || keyCode == 96 || keyCode == -8 || keyCode == -7) {
            midlet.OpenMenu();
            midlet.CloseGame();
            return;
        }
        boolean isModified = false;
        switch (action) {
            case Canvas.LEFT:
            case Canvas.KEY_NUM4:
                isModified = moveUp();
                break;

            case Canvas.UP:
            case Canvas.KEY_NUM2:
                isModified = moveLeft();
                break;

            case Canvas.RIGHT:
            case Canvas.KEY_NUM6:
                isModified = moveDown();
                break;

            case Canvas.DOWN:
            case Canvas.KEY_NUM8:
                isModified = moveRight();
                break;
        }

        if (!isModified) {
            return;
        }

        insertNew();
        repaint();
    }

    private boolean moveDown() {
        boolean result = false;
        int[] maxCheck = {3, 3, 3, 3};
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 0) {
                    continue;
                }

                int nextFilled = -1;
                for (int k = i + 1; k <= maxCheck[j]; k++) {
                    if (state[k][j] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    if (maxCheck[j] != i) {
                        state[maxCheck[j]][j] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[nextFilled][j]) {
                    state[nextFilled][j] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    maxCheck[j] = nextFilled - 1;
                    freeCount++;
                    result = true;
                } else {
                    if (nextFilled - 1 != i) {
                        state[nextFilled - 1][j] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean moveRight() {
        boolean result = false;
        int[] maxCheck = {3, 3, 3, 3};
        for (int j = 2; j >= 0; j--) {
            for (int i = 0; i < 4; i++) {

                if (state[i][j] == 0) {
                    continue;
                }

                //Найдем индекс следующего "занятого" элемента
                int nextFilled = -1;
                for (int k = j + 1; k <= maxCheck[i]; k++) {
                    if (state[i][k] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //в конце 0 - двигаем в конец (если это возможно)
                    if (maxCheck[i] != j) {
                        state[i][maxCheck[i]] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[i][nextFilled]) {
                    //в конце точно такой же tile - объединяем их
                    state[i][nextFilled] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    maxCheck[i] = nextFilled - 1;
                    freeCount++;
                    result = true;
                } else {
                    //в конце какой-то обычный tile. Просто двигаем в плотную к нему
                    if (nextFilled - 1 != j) {
                        state[i][nextFilled - 1] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean moveLeft() {
        boolean result = false;
        int[] minCheck = {0, 0, 0, 0};
        for (int j = 1; j < 4; j++) {
            for (int i = 0; i < 4; i++) {

                if (state[i][j] == 0) {
                    continue;
                }

                //Найдем индекс следующего "занятого" элемента
                int nextFilled = -1;
                for (int k = j - 1; k >= minCheck[i]; k--) {
                    if (state[i][k] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //в конце 0 - двигаем в конец (если это возможно)
                    if (minCheck[i] != j) {
                        state[i][minCheck[i]] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[i][nextFilled]) {
                    //в конец не 0, а точно такой же tile - объединяем их
                    state[i][nextFilled] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    minCheck[i] = nextFilled + 1;
                    freeCount++;
                    result = true;
                } else {
                    //в конце какой-то обычный tile. Просто двигаем в плотную к нему
                    if (nextFilled + 1 != j) {
                        state[i][nextFilled + 1] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean moveUp() {
        boolean result = false;
        int[] minCheck = {0, 0, 0, 0};
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 0) {
                    continue;
                }

                //Найдем индекс следующего "занятого" элемента
                int nextFilled = -1;
                for (int k = i - 1; k >= minCheck[j]; k--) {
                    if (state[k][j] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //в конце 0 - двигаем в конец (если это возможно)
                    if (minCheck[j] != i) {
                        state[minCheck[j]][j] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[nextFilled][j]) {
                    //в конце точно такой же tile - объединяем их
                    state[nextFilled][j] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    minCheck[j] = nextFilled + 1;
                    freeCount++;
                    result = true;
                } else {
                    //в конце какой-то обычный tile. Просто двигаем в плотную к нему
                    if (nextFilled + 1 != i) {
                        state[nextFilled + 1][j] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public boolean isGameOver() {
        // 检查是否还有空格
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 0) {
                    return false; // 只要有一个空格，游戏就还没有结束
                }
            }
        }

        // 检查水平方向是否还有相邻可合并的数字
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == state[i][j + 1]) {
                    return false; // 存在相邻可合并的数字，游戏还没有结束
                }
            }
        }

        // 检查垂直方向是否还有相邻可合并的数字
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                if (state[i][j] == state[i + 1][j]) {
                    return false; // 存在相邻可合并的数字，游戏还没有结束
                }
            }
        }

        // 如果以上条件都不满足，则游戏结束
        return true;
    }

    private void insertNew() {
        int cond = random.nextInt(10);
        short next = (short) (cond < 9 ? 2 : 4);

        int pos = random.nextInt(freeCount);

        freeCount--;
        int currentFreePos = 0;
        for (int i = 0; i < state.length; i++) {
            short[] ses = state[i];
            for (int j = 0; j < ses.length; j++) {
                short s = ses[j];
                if (s == 0) {
                    if (currentFreePos == pos) {
                        state[i][j] = next;
                        return;
                    } else {
                        currentFreePos++;
                    }
                }
            }
        }
    }

    public void paint(Graphics g) {
        if (!isPlay) return;
        drawHandler.Draw(g);
    }

    public void commandAction(Command c, Displayable displayable) {
    }
}