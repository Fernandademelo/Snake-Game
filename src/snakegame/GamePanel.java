package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {


    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    private Image head;
    private Image fundo;
    private Image currentEmoji;
    AffineTransform at = new AffineTransform();


    public void LoadImages() {
        ImageIcon cabecinha = new ImageIcon("src/images/snakehead.png");
        head = cabecinha.getImage();
        head = head.getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
        ImageIcon backgroundImg = new ImageIcon("src/images/background.png");
        fundo = backgroundImg.getImage();


    }


    GamePanel() {

        random = new Random();
        LoadImages();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {

        g.drawImage(fundo, 0, 0, this);
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g) {

        if (running) {
            Graphics2D g2d = (Graphics2D) g;
//          /*grid*/
//            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
//                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
//            }
            /*APPLE*/
            g.setColor(Color.red);
            g.drawImage(currentEmoji, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);

            /*corpinho*/
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g2d.setTransform(at);
                    g2d.drawImage(head, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    g2d.setTransform(new AffineTransform());
                    // g.drawImage(head, x[i], y[i], this);
                    //g.setColor(Color.green);

                } else {
                    g.setColor(new Color(0, 255, 255));
                    g.setColor(new Color(random.nextInt(100), random.nextInt(200), random.nextInt(255))); //random body color
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            /*Display the Score!*/
            g.setColor(Color.pink);
            g.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 30));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }


    public void newApple() {
        /*Array of fruit emojis*/
        ImageIcon[] emojis = new ImageIcon[5];
        emojis[0] = new ImageIcon("src/images/cherry.png");
        emojis[1] = new ImageIcon("src/images/peach.png");
        emojis[2] = new ImageIcon("src/images/tangerine.png");
        emojis[3] = new ImageIcon("src/images/uva.png");
        emojis[4] = new ImageIcon("src/images/watermelon.png");
        /*Appears random emojis on screen*/
        int randomEmoji = random.nextInt(emojis.length);
        currentEmoji = emojis[randomEmoji].getImage();

        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

    }


    public void move() {
        switch (direction) {
            case 'U':
                at.rotate(Math.toRadians(-90), head.getWidth(this) / 2, head.getHeight(this) / 2);
                break;
            case 'D':
                at.rotate(Math.toRadians(90), head.getWidth(this) / 2, head.getHeight(this) / 2);
                break;
            case 'L':
                at.rotate(Math.toRadians(180), head.getWidth(this) / 2, head.getHeight(this) / 2);
                break;
        }
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
        }

    }


    public void checkApple() {
        /*Eat Apple and grow body*/
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }

    }


    public void checkCollisons() {
        /*checks if head colLides with body*/
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        /*check if head touches left border*/
        if (x[0] < 0) {
            running = false;
        }
        /*check if head touches right border*/
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        /*check if head touches top border*/
        if (y[0] < 0) {
            running = false;
        }
        /*check if head touches bottom border*/
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void restartGame() {
        new GameFrame();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.pink);
        g.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 30));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        /*Game Over text*/
        g.setColor(Color.pink);
        g.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        /*GAME OVER IN THE CENTER OF THE SCREEN*/
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 25));
        //  FontMetrics metrics3 = getFontMetrics(g.getFont());
        // g.drawString("Press ENTER to restart" + applesEaten,(SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to restart"))/5,g.getFont().getSize());

        /* Hit Enter to Restart the game! :) */
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisons();

        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        /*Actual moving with keyboard arrows!*/
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
