import com.sun.j2me.global.NumberFormat;

import java.util.Random;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

public class Game2048Canvas extends Canvas implements Runnable,IRestartGame {
    public int HISCORE = 0;
    int freeCount;
    private final Random random = new Random();
    public short[][] state = new short[4][4];
    public int score;
    private GameDrawHandler drawHandler;

    public boolean isPlay = false;
    public Midlet midlet;

    public boolean pause = false;
    private PausePannel pp;

    public Game2048Canvas(Midlet midlet) {
        this.setFullScreenMode(true);
        this.midlet = midlet;
        drawHandler = new GameDrawHandler(this);
        pp = new PausePannel(midlet, this, this.getWidth(), this.getHeight());
    }

    public void Start() {
        this.isPlay = true;
        state = new short[4][4];
        freeCount = 16;
        score = 0;
        insertNew();
        insertNew();
        Thread t = new Thread(this);
        t.start();
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
        if (keyCode == 8 || keyCode == 96 || keyCode == -6 || keyCode == 48 || keyCode == -31 || keyCode == -8 || keyCode == -9 || keyCode == -5) {
            if (action != FIRE && action != UP && action != LEFT && action != RIGHT && action != DOWN) {
                pause = true;
            }
        }
        if (!pause) {
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
        } else {
            pp.keyPressed(action);
        }
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

                //���ѧۧէ֧� �ڧߧէ֧ܧ� ��ݧ֧է���֧ԧ� "�٧ѧߧ���ԧ�" ��ݧ֧ާ֧ߧ��
                int nextFilled = -1;
                for (int k = j + 1; k <= maxCheck[i]; k++) {
                    if (state[i][k] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //�� �ܧ�ߧ�� 0 - �էӧڧԧѧ֧� �� �ܧ�ߧ֧� (�֧�ݧ� ���� �ӧ�٧ާ�اߧ�)
                    if (maxCheck[i] != j) {
                        state[i][maxCheck[i]] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[i][nextFilled]) {
                    //�� �ܧ�ߧ�� ����ߧ� ��ѧܧ�� �ا� tile - ��ҧ�֧էڧߧ�֧� �ڧ�
                    state[i][nextFilled] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    maxCheck[i] = nextFilled - 1;
                    freeCount++;
                    result = true;
                } else {
                    //�� �ܧ�ߧ�� �ܧѧܧ��-��� ��ҧ��ߧ�� tile. �������� �էӧڧԧѧ֧� �� ��ݧ��ߧ�� �� �ߧ֧ާ�
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

                //���ѧۧէ֧� �ڧߧէ֧ܧ� ��ݧ֧է���֧ԧ� "�٧ѧߧ���ԧ�" ��ݧ֧ާ֧ߧ��
                int nextFilled = -1;
                for (int k = j - 1; k >= minCheck[i]; k--) {
                    if (state[i][k] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //�� �ܧ�ߧ�� 0 - �էӧڧԧѧ֧� �� �ܧ�ߧ֧� (�֧�ݧ� ���� �ӧ�٧ާ�اߧ�)
                    if (minCheck[i] != j) {
                        state[i][minCheck[i]] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[i][nextFilled]) {
                    //�� �ܧ�ߧ֧� �ߧ� 0, �� ����ߧ� ��ѧܧ�� �ا� tile - ��ҧ�֧էڧߧ�֧� �ڧ�
                    state[i][nextFilled] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    minCheck[i] = nextFilled + 1;
                    freeCount++;
                    result = true;
                } else {
                    //�� �ܧ�ߧ�� �ܧѧܧ��-��� ��ҧ��ߧ�� tile. �������� �էӧڧԧѧ֧� �� ��ݧ��ߧ�� �� �ߧ֧ާ�
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

                //���ѧۧէ֧� �ڧߧէ֧ܧ� ��ݧ֧է���֧ԧ� "�٧ѧߧ���ԧ�" ��ݧ֧ާ֧ߧ��
                int nextFilled = -1;
                for (int k = i - 1; k >= minCheck[j]; k--) {
                    if (state[k][j] != 0) {
                        nextFilled = k;
                        break;
                    }
                }

                if (nextFilled == -1) {
                    //�� �ܧ�ߧ�� 0 - �էӧڧԧѧ֧� �� �ܧ�ߧ֧� (�֧�ݧ� ���� �ӧ�٧ާ�اߧ�)
                    if (minCheck[j] != i) {
                        state[minCheck[j]][j] = state[i][j];
                        state[i][j] = 0;
                        result = true;
                    }
                } else if (state[i][j] == state[nextFilled][j]) {
                    //�� �ܧ�ߧ�� ����ߧ� ��ѧܧ�� �ا� tile - ��ҧ�֧էڧߧ�֧� �ڧ�
                    state[nextFilled][j] *= 2;
                    score += state[nextFilled][j];
                    state[i][j] = 0;
                    minCheck[j] = nextFilled + 1;
                    freeCount++;
                    result = true;
                } else {
                    //�� �ܧ�ߧ�� �ܧѧܧ��-��� ��ҧ��ߧ�� tile. �������� �էӧڧԧѧ֧� �� ��ݧ��ߧ�� �� �ߧ֧ާ�
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

    protected void hideNotify() {
        super.hideNotify();
        pause = true;
        repaint();
        System.out.println("Out");
    }

    public boolean isGameOver() {
        // ����Ƿ��пո�
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 0) {
                    return false; // ֻҪ��һ���ո���Ϸ�ͻ�û�н���
                }
            }
        }

        // ���ˮƽ�����Ƿ������ڿɺϲ�������
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == state[i][j + 1]) {
                    return false; // �������ڿɺϲ������֣���Ϸ��û�н���
                }
            }
        }

        // ��鴹ֱ�����Ƿ������ڿɺϲ�������
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                if (state[i][j] == state[i + 1][j]) {
                    return false; // �������ڿɺϲ������֣���Ϸ��û�н���
                }
            }
        }

        // ������������������㣬����Ϸ����
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
        if (pause) pp.Draw(g);
    }

    public void RestartGame() {
        pause = false;
        repaint();
    }

    private void drawString(Graphics g, String str, int x, int y, int anchor) {
        g.setColor(0, 0, 0);
        g.drawString(str, x - 2, y, anchor);
        g.drawString(str, x + 2, y, anchor);
        g.drawString(str, x, y - 2, anchor);
        g.drawString(str, x, y + 2, anchor);
        g.setColor(0, 0, 129);
        g.drawString(str, x - 1, y, anchor);
        g.drawString(str, x + 1, y, anchor);
        g.drawString(str, x, y - 1, anchor);
        g.drawString(str, x, y + 1, anchor);
        g.setColor(199, 218, 243);
        g.drawString(str, x, y, anchor);
    }

    public void run() {
        while (isPlay) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignored
            }
            repaint();
        }
    }
}