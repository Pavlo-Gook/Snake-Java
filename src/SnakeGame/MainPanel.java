package SnakeGame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

//Клас, в якому описується вся робота гри, в тому числі меню і налаштування
public class MainPanel extends JPanel {
    private Textures textures = new Textures();
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private GameSettings settings = new GameSettings();
    private JLabel soundButton, exitTitleButton, playButton, settingsButton, exitButton;
    private Clip clickSound;     //Звук натискання кнопок
    private boolean isSound = true;     //Наявність звуку в грі
    private boolean inGame = false;     //Відкрита GamePanel
    private boolean inSettings = false;     //Відкрита SettingsPanel
    private int score = 0;     //Рахунок
    private int record;     //Рекорд

    public MainPanel() {
        setLayout(null);
        setCursor(createCursor());
        createRecordFile();     //Створення файлу рекорду (у разі його відсутності)
        if (getRecord() != "")     //Запис рекорду в змінну
            record = Integer.parseInt(getRecord());
        else record = 0;
        addComponent();     //Додаю всі кнопки меню та Title bar
        try {     //Ініціалізація змінної для відтворення звуку натискання кнопки
            clickSound = AudioSystem.getClip();
            clickSound.open(AudioSystem.getAudioInputStream(getClass().getResource("resources/sounds/click.wav")));
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, new Color(100, 215, 20),
                Window.SCREEN_WIDTH, Window.SCREEN_HEIGHT, new Color(160, 255, 60)));
        g2.fillRect(0, 0, Window.SCREEN_WIDTH, Window.SCREEN_HEIGHT);     //Заливка панелі градієнтом
        g2.drawImage(textures.cup, 0, 0, this);     //Значок рекорду
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        g2.setColor(new Color(60, 100, 0));
        g2.drawString(String.valueOf(record), 50, 36);     //Значення рекорду

        if (inGame) {
            FontMetrics fm = g2.getFontMetrics(new Font("Arial", Font.BOLD, 26));     //Для визначення ширини рядка
            g2.drawImage(textures.titleScore, 50 + fm.stringWidth(String.valueOf(record)), 0, this);     //Значок рахунку
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.setColor(new Color(60, 100, 0));
            g2.drawString(String.valueOf(score), 100 + fm.stringWidth(String.valueOf(record)), 36);     //Значення рахунку
        }

        if (!inGame && !inSettings) {
            g2.drawImage(textures.titleText, 300, 50, this);     //Великий надпис в меню "SNAKE"
            g2.drawImage(textures.bigSnake, 450, 250, this);     //Картинка змійки для меню
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(Color.WHITE);
            g2.drawString("Pavlo Guk", Window.SCREEN_WIDTH - 85, Window.SCREEN_HEIGHT - 5);     //Автор гри
        }
    }

    private void addComponent() {
        soundButton = new JLabel();     //Кнопка звуку на Title bar
        soundButton.setIcon(textures.soundOn);
        soundButton.setBounds(Window.SCREEN_WIDTH - 100, 0,
                textures.soundOn.getIconWidth(), textures.soundOn.getIconHeight());
        soundButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {     //Натиснута кнопка soundButton
                super.mouseReleased(e);
                isSound = !isSound;     //Зміна настройок звуку
                if (isSound) soundButton.setIcon(textures.soundOnMouse);
                else soundButton.setIcon(textures.soundOffMouse);
                playSound(clickSound);
            }

            @Override
            public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                super.mouseEntered(e);
                if (isSound) soundButton.setIcon(textures.soundOnMouse);
                else soundButton.setIcon(textures.soundOffMouse);
            }

            @Override
            public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                super.mouseExited(e);
                if (isSound) soundButton.setIcon(textures.soundOn);
                else soundButton.setIcon(textures.soundOff);
            }
        });

        exitTitleButton = new JLabel();     //Кнопка виходу з гри на Title bar
        exitTitleButton.setIcon(textures.exitTitle);
        exitTitleButton.setBounds(Window.SCREEN_WIDTH - 50, 0,
                textures.exitTitle.getIconWidth(), textures.exitTitle.getIconHeight());
        exitTitleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {     //Натиснута кнопка exitTitleButton
                super.mouseReleased(e);
                newRecord(String.valueOf(record));
                System.exit(1);     //Закриття програми
            }

            @Override
            public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                super.mouseEntered(e);
                exitTitleButton.setIcon(textures.exitTitleMouse);
            }

            @Override
            public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                super.mouseExited(e);
                exitTitleButton.setIcon(textures.exitTitle);
            }
        });

        final int startingPositionX = 150;     //Стартова позиція кнопок меню по осі Х
        playButton = new JLabel();     //Кнопка запуску гри
        playButton.setIcon(textures.play);
        playButton.setBounds(150, 250, textures.play.getIconWidth(), textures.play.getIconHeight());
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {     //Натиснута кнопка playButton
                super.mouseReleased(e);
                playButton.setLocation(startingPositionX, playButton.getY());
                playSound(clickSound);
                remove(playButton);     //Видалення кнопок меню
                remove(settingsButton);
                remove(exitButton);
                gamePanel = new GamePanel(settings);     //Створення ігрового поля
                add(gamePanel);
                gamePanel.requestFocus();     //Зміна фокусу на панель гри
            }

            @Override
            public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                super.mouseEntered(e);
                playButton.setLocation(playButton.getX() + 20, playButton.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                super.mouseExited(e);
                playButton.setLocation(startingPositionX, playButton.getY());
            }
        });

        settingsButton = new JLabel();     //Кнопка відкриття настройок
        settingsButton.setIcon(textures.settings);
        settingsButton.setBounds(150, 370, textures.settings.getIconWidth(), textures.settings.getIconHeight());
        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {     //Натиснута кнопка settingsButton
                super.mouseReleased(e);
                settingsButton.setLocation(startingPositionX, settingsButton.getY());
                playSound(clickSound);
                remove(playButton);     //Видалення кнопок меню
                remove(settingsButton);
                remove(exitButton);
                settingsPanel = new SettingsPanel();     //Створення панелі налаштувань
                add(settingsPanel);
            }

            @Override
            public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                super.mouseEntered(e);
                settingsButton.setLocation(settingsButton.getX() + 20, settingsButton.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                super.mouseExited(e);
                settingsButton.setLocation(startingPositionX, settingsButton.getY());
            }
        });

        exitButton = new JLabel();     //Кнопка виходу з гри
        exitButton.setIcon(textures.exit);
        exitButton.setBounds(150, 490, textures.exit.getIconWidth(), textures.exit.getIconHeight());
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {     //Натиснута кнопка exitButton
                super.mouseReleased(e);
                newRecord(String.valueOf(record));
                System.exit(1);     //Закриття програми
            }

            @Override
            public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                super.mouseEntered(e);
                exitButton.setLocation(exitButton.getX() + 20, exitButton.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                super.mouseExited(e);
                exitButton.setLocation(startingPositionX, exitButton.getY());
            }
        });

        add(soundButton);
        add(exitTitleButton);
        add(playButton);
        add(settingsButton);
        add(exitButton);
    }

    private void createRecordFile() {
        //Створення файлу для запису рекорду
        File dir = new File(System.getProperty("user.home") + "/SnakeGame");
        dir.mkdirs();
        File file = new File(dir + "/record.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newRecord(String record) {
        //Запис рекорду при закритті програми
        String file = System.getProperty("user.home") + "/SnakeGame/record.txt";
        try(FileWriter writer = new FileWriter(file, false)) {
            writer.write(record);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRecord() {
        //Отримання рекорду з файлу
        String file = System.getProperty("user.home") + "/SnakeGame/record.txt";
        StringBuilder result = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private Cursor createCursor() {
        //Створення свого власного курсора
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        BufferedImage cursor = new BufferedImage(32,32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = cursor.createGraphics();
        g2.setStroke(new BasicStroke(5.0f));
        g2.setColor(Color.ORANGE);
        g2.drawLine(3, 3, 3, 32);
        g2.drawLine(4, 2, 20, 24);
        return toolkit.createCustomCursor(cursor, new Point(0,0), "MyCursor");
    }

    private void playSound(Clip clip) {
        if (isSound) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void updateMainPanel() {
        //Оновлення головної панелі під час гри або в настройках
        repaint();
    }

    private void removePanel(JPanel panel) {
        //Видалення панелі (гри або налаштувань) та повернення в меню
        remove(panel);
        add(playButton);
        add(settingsButton);
        add(exitButton);
        repaint();
    }

    //Напрямки руху змійки
    private enum direction {LEFT, RIGHT, UP, DOWN}

    private class GamePanel extends JPanel implements ActionListener    {
        private final int WIDTH = 1000, HEIGHT = 600;
        private final int UNIT_SIZE = 50;
        private final int GAME_UNITS = (WIDTH / UNIT_SIZE) * (HEIGHT / UNIT_SIZE);
        private int delay;     //Затримка таймера
        private int[] x = new int[GAME_UNITS + 1];
        private int[] y = new int[GAME_UNITS + 1];
        private int[] grassX = new int[30];
        private int[] grassY = new int[30];
        private int[] barrierX, barrierY;
        private direction currentDirection;     //Поточний напрямок руху
        private LinkedList<direction> directions = new LinkedList<>();     //Буфер для зберігання нових напрямків
        private direction[] dirSnakeParts = new direction[GAME_UNITS + 1];     //Напрямки руху частин змійки (для текстур)
        private int bodyParts = 3;     //Початкова кіькість частин змійки
        private int appleX, appleY;
        private Timer timer;
        private Clip eatSound, gameOverSound;     //Зкуки в грі
        private Random random = new Random();
        private GameSettings settings;     //Поточні налаштування гри

        public GamePanel(GameSettings settings) {
            this.settings = settings;
            setBounds(3, 50, WIDTH, HEIGHT);
            setBackground(new Color(185, 255, 90));
            addKeyListener(new MyKeyAdapter());
            updateMainPanel();
            textures.updateSnakeColor(settings);     //Оновлення текстур змійки відносно налаштувань

            x[0] = 0; y[0] = HEIGHT / 2 - UNIT_SIZE;     //Початкове розміщення змійки
            for (int i = 1; i < bodyParts; i++) {
                x[i] = x[i - 1] - UNIT_SIZE;
                y[i] = y[0];
            }
            currentDirection = direction.RIGHT;     //Стартовий напрямок змійки
            for (int i = 0; i < bodyParts; i++) {     //Початкові напрямки частин змійки
                dirSnakeParts[i] = currentDirection;
            }

            for (int i = 0; i < grassX.length; i++) {     //Генерація координат трави
                grassX[i] = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
                grassY[i] = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            }

            if (settings.getTypeOfGame() == type.BARRIER) {
                generateBarrier();
            }

            try {     //Ініціалізація змінних для відтворення звуку у грі
                eatSound = AudioSystem.getClip();
                eatSound.open(AudioSystem.getAudioInputStream(getClass().getResource("resources/sounds/eat.wav")));
                gameOverSound = AudioSystem.getClip();
                gameOverSound.open(AudioSystem.getAudioInputStream(getClass().getResource("resources/sounds/gameOver.wav")));
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }

            inGame = true;
            newApple();
            switch (settings.getSnakeSpeed()) {     //Ініціалізація затримки таймера
                case SMALL -> delay = 120;
                case MEDIUM -> delay = 105;
                case LARGE -> delay = 90;
            }
            timer = new Timer(delay,this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (inGame) {     //Якщо гра триває, то малюємо елементи гри
                for (int i = 0; i < grassX.length; i++) {     //Малювання трави
                    g2.drawImage(textures.grass, grassX[i], grassY[i], UNIT_SIZE, UNIT_SIZE, this);
                }

                if (settings.getTypeOfGame() == type.BARRIER) {
                    for (int i = 0; i < barrierX.length; i++) {     //Малювання перешкод
                        g2.drawImage(textures.barrier, barrierX[i], barrierY[i], UNIT_SIZE, UNIT_SIZE, this);
                    }
                }

                g2.drawImage(textures.apple, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);     //Малювання яблука
                drawSnake(g2);     //Малювання змійки
            }
            else gameOver();     //В іншому випадку - кінець гри
        }

        private void drawSnake(Graphics2D g2) {
            for (int i = 0; i < bodyParts; i++) {
                AffineTransform old = g2.getTransform();     //Початковий кут повороту текстури
                switch (dirSnakeParts[i]) {
                    case UP -> g2.rotate(Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                    case DOWN -> g2.rotate(3 * Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                    case RIGHT -> g2.rotate(Math.PI, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                }
                if (i == 0) {     //Малювання голови змійки
                    g2.drawImage(textures.getHead(), x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                } else if (i == bodyParts - 1) {     //Малювання хвоста змійки
                    if (dirSnakeParts[i] != dirSnakeParts[i - 1]) {
                        g2.setTransform(old);     //Скидаємо кут повороту
                        switch (dirSnakeParts[i - 1]) {     //Беремо напрямок руху попередньої частини тіла
                            case UP -> g2.rotate(Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            case DOWN -> g2.rotate(3 * Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            case RIGHT -> g2.rotate(Math.PI, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                        }
                    }
                    g2.drawImage(textures.getTail(), x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                } else if (dirSnakeParts[i] != dirSnakeParts[i - 1]) {     //Малювання частин повороту змійки
                    g2.setTransform(old);     //Скидаємо кут повороту
                    switch (dirSnakeParts[i - 1]) {
                        case UP:     //Якщо попередня частина тіла рухається вгору
                            if (dirSnakeParts[i] == direction.LEFT)
                                g2.rotate(3 * Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            else g2.rotate(Math.PI, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            break;
                        case DOWN:     //Якщо попередня частина тіла рухається вниз
                            if (dirSnakeParts[i] == direction.LEFT)
                                break;
                            else g2.rotate(3 * Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                        case LEFT:     //Якщо попередня частина тіла рухається вліво
                            if (dirSnakeParts[i] == direction.UP)
                                g2.rotate(Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            else g2.rotate(Math.PI, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                            break;
                        case RIGHT:     //Якщо попередня частина тіла рухається вправо
                            if (dirSnakeParts[i] == direction.UP)
                                break;
                            else g2.rotate(3 * Math.PI / 2, x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 2);
                    }
                    g2.drawImage(textures.getBodyAngle(), x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                } else {     //Малювання тулуба змійки (без частин повороту)
                    g2.drawImage(textures.getBody(), x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                }
                g2.setTransform(old);     //Скидаємо кут повороту
            }
        }

        private void generateBarrier() {
            //Генерація координат перешкод
            switch (random.nextInt(5)) {
                case 0:     //Перша карта перешкод
                    barrierX = new int[] {100, 150, 800, 850, 100, 850, 100, 850, 100, 150, 800, 850};
                    barrierY = new int[] {100, 100, 100, 100, 150, 150, 400, 400, 450, 450, 450, 450};
                    break;
                case 1:     //Друга карта перешкод
                    barrierX = new int[] {150, 200, 250, 300, 500, 500, 500, 500, 700, 750, 800};
                    barrierY = new int[] {150, 150, 150, 150, 200, 250, 300, 350, 400, 400, 400};
                    break;
                case 2:     //Третя карта перешкод
                    barrierX = new int[] {150, 200, 250, 700, 750, 800, 150, 800, 450, 500, 400, 450, 500, 550};
                    barrierY = new int[] {100, 100, 100, 100, 100, 100, 150, 150, 400, 400, 450, 450, 450, 450};
                    break;
                case 3:     //Четверта карта перешкод
                    barrierX = new int[] {700, 100, 600, 650, 700, 100, 150, 750, 800, 300, 350, 800};
                    barrierY = new int[] {100, 150, 150, 150, 150, 200, 200, 450, 450, 500, 500, 500};
                    break;
                case 4:     //П'ята карта перешкод
                    barrierX = new int[] {250, 750, 200, 250, 750, 800, 850, 200, 550, 450, 500, 550, 600};
                    barrierY = new int[] {100, 100, 150, 150, 150, 150, 150, 200, 400, 450, 450, 450, 450};
            }
        }

        private void newApple() {
            //Генерація координат яблука
            appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            //Перевірка координат яблука на конфлікт з координатами змійки
            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i])
                    newApple();
            }
            //Перевірка координат яблуа на конфлікт з координатами перешкод
            if (settings.getTypeOfGame() == type.BARRIER) {
                for (int i = 0; i < barrierX.length; i++) {
                    if (appleX == barrierX[i] && appleY == barrierY[i])
                        newApple();
                }
            }
        }

        private void move() {
            if (!directions.isEmpty()) {     //Перевірка буферу напрямків змійки
                direction tempDir = directions.pop();     //Беремо перший напрямок у списку
                if (!((currentDirection == direction.LEFT && tempDir == direction.RIGHT) ||
                        (currentDirection == direction.RIGHT && tempDir == direction.LEFT) ||
                        (currentDirection == direction.UP && tempDir == direction.DOWN) ||
                        (currentDirection == direction.DOWN && tempDir == direction.UP))) {
                    currentDirection = tempDir;     //Якщо немає конфлікту напрямків, то записуємо в поточний
                }
            }

            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];     //Переміщення тіла змійки на одну позицію
                y[i] = y[i - 1];
                dirSnakeParts[i] = dirSnakeParts[i - 1];
            }
            dirSnakeParts[0] = currentDirection;     //Напрямок голови змійки

            switch (currentDirection) {     //Нові координати голови змійки
                case UP -> y[0] -= UNIT_SIZE;
                case DOWN -> y[0] += UNIT_SIZE;
                case LEFT -> x[0] -= UNIT_SIZE;
                case RIGHT -> x[0] += UNIT_SIZE;
            }

            if (settings.getTypeOfGame() == type.TELEPORT) {     //Вихід за межі карти (телепорт)
                if (x[0] < 0)
                    x[0] = WIDTH - UNIT_SIZE;
                if (x[0] >= WIDTH)
                    x[0] = 0;
                if (y[0] < 0)
                    y[0] = HEIGHT - UNIT_SIZE;
                if (y[0] >= HEIGHT)
                    y[0] = 0;
            }
        }

        private void checkApple() {
            if ((x[0] == appleX) && (y[0] == appleY)) {     //Змійка зловила яблуко
                bodyParts++;     //Збільшення розміру тіла
                score++;     //Збільшення рахунку
                if (score > record)     //Запис рекорду
                    record = score;
                playSound(eatSound);     //Відтворення звуку
                updateMainPanel();     //Оновлення рахунку та рекорду на Title bar
                newApple();     //Генерація нового яблука
            }
        }

        private void checkCollisions() {
            if (bodyParts == GAME_UNITS) {     //Якщо заповнено все поле
                inGame = false;
                gameOver();     //Кінець гри
            }

            for (int i = bodyParts; i > 0; i--) {     //Змійка зловила свій хвіст
                if ((x[0] == x[i]) && (y[0] == y[i]))
                    inGame = false;
            }

            if (settings.getTypeOfGame() != type.TELEPORT) {     //Вихід за межі карти (проіграш)
                if (x[0] < 0)
                    inGame = false;
                if (x[0] >= WIDTH)
                    inGame = false;
                if (y[0] < 0)
                    inGame = false;
                if (y[0] >= HEIGHT)
                    inGame = false;
            }

            if (settings.getTypeOfGame() == type.BARRIER) {     //Змійка врізалася в перешкоду
                for (int i = 0; i < barrierX.length; i++) {
                    if (x[0] == barrierX[i] && y[0] == barrierY[i])
                        inGame = false;
                }
            }
        }

        private void gameOver() {
            score = 0;     //Занульовую рахунок
            playSound(gameOverSound);     //Відтворення звуку
            try {     //Затримка на 1 секунду перед закриттям панелі
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            removePanel(this);     //Видалення поля гри з головної панелі
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Функція переривання таймера
            if (inGame) {
                move();
                checkCollisions();
                checkApple();
            }
            else timer.stop();
            repaint();
        }

        private class MyKeyAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {     //Якщо натиснута кнопка на клавіатурі
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                        directions.add(direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                        directions.add(direction.RIGHT);
                        break;
                    case KeyEvent.VK_UP, KeyEvent.VK_W:
                        directions.add(direction.UP);
                        break;
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                        directions.add(direction.DOWN);
                }
            }
        }
    }

    private class SettingsPanel extends JPanel {
        private final int WIDTH = 1000, HEIGHT = 600;
        private JLabel next1, next2, next3, back;

        public SettingsPanel() {
            setBounds(3, 50, WIDTH, HEIGHT);
            inSettings = true;
            updateMainPanel();     //Оновлення головної панелі
            addComponent();     //Додавання кнопок на панель
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, new Color(100, 215, 20),
                    WIDTH, HEIGHT, new Color(160, 255, 60)));
            g2.fillRect(0, 0, WIDTH, HEIGHT);     //Заливка панелі градієнтом

            g2.drawImage(textures.titleSettings, 340, 0, this);     //Надпис "Settings" на панелі
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(Color.BLACK);
            g2.drawString("Type of Game", 148, 140);     //Назви налаштувань
            g2.drawString("Snake color", 453, 140);
            g2.drawString("Speed", 775, 140);

            switch (settings.getTypeOfGame()) {     //Картинка типу гри
                case CLASSICAL -> g2.drawImage(textures.classicalType, 100, 150, this);
                case TELEPORT -> g2.drawImage(textures.teleportType, 100, 150, this);
                case BARRIER -> g2.drawImage(textures.barrierType, 100, 150, this);
            }

            switch (settings.getSnakeColor()) {     //Картинка кольору змійки
                case BLUE -> g2.drawImage(textures.blueColor, 400, 150, this);
                case GREEN -> g2.drawImage(textures.greenColor, 400, 150, this);
                case ORANGE -> g2.drawImage(textures.orangeColor, 400, 150, this);
                case PINK -> g2.drawImage(textures.pinkColor, 400, 150, this);
                case VIOLET -> g2.drawImage(textures.violetColor, 400, 150, this);
            }

            switch (settings.getSnakeSpeed()) {     //Картинка швидкості руху змійки
                case SMALL -> g2.drawImage(textures.speedSmall, 700, 150, this);
                case MEDIUM -> g2.drawImage(textures.speedMedium, 700, 150, this);
                case LARGE -> g2.drawImage(textures.speedLarge, 700, 150, this);
            }
        }

        private void removeSettingsPanel() {
            remove(next1);     //Видалення кнопок
            remove(next2);
            remove(next3);
            remove(back);
            inSettings = false;
            removePanel(this);     //Видалення панелі налаштувань з головної панелі
        }

        private void addComponent() {
            next1 = new JLabel();     //Перша стрілочка зміни налаштувань
            next1.setIcon(textures.next);
            next1.setBounds(170, 365, textures.next.getIconWidth(), textures.next.getIconHeight());
            next1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {     //Натиснута кнопка next1
                    super.mouseReleased(e);
                    switch (settings.getTypeOfGame()) {     //Зміна типу гри
                        case CLASSICAL -> settings.setTypeOfGame(type.TELEPORT);
                        case TELEPORT -> settings.setTypeOfGame(type.BARRIER);
                        case BARRIER -> settings.setTypeOfGame(type.CLASSICAL);
                    }
                    playSound(clickSound);
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                    super.mouseEntered(e);
                    next1.setLocation(next1.getX(), next1.getY() + 10);
                }

                @Override
                public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                    super.mouseExited(e);
                    next1.setLocation(next1.getX(), next1.getY() - 10);
                }
            });

            next2 = new JLabel();     //Друга стрілочка зміни налаштувань
            next2.setIcon(textures.next);
            next2.setBounds(470, 365, textures.next.getIconWidth(), textures.next.getIconHeight());
            next2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {     //Натиснута кнопка next2
                    super.mouseReleased(e);
                    switch (settings.getSnakeColor()) {     //Зміна типу гри
                        case BLUE -> settings.setSnakeColor(color.GREEN);
                        case GREEN -> settings.setSnakeColor(color.ORANGE);
                        case ORANGE -> settings.setSnakeColor(color.PINK);
                        case PINK -> settings.setSnakeColor(color.VIOLET);
                        case VIOLET -> settings.setSnakeColor(color.BLUE);
                    }
                    playSound(clickSound);
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                    super.mouseEntered(e);
                    next2.setLocation(next2.getX(), next2.getY() + 10);
                }

                @Override
                public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                    super.mouseExited(e);
                    next2.setLocation(next2.getX(), next2.getY() - 10);
                }
            });

            next3 = new JLabel();     //Третя стрілочка зміни налаштувань
            next3.setIcon(textures.next);
            next3.setBounds(770, 365, textures.next.getIconWidth(), textures.next.getIconHeight());
            next3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {     //Натиснута кнопка next3
                    super.mouseReleased(e);
                    switch (settings.getSnakeSpeed()) {     //Зміна типу гри
                        case SMALL -> settings.setSnakeSpeed(speed.MEDIUM);
                        case MEDIUM -> settings.setSnakeSpeed(speed.LARGE);
                        case LARGE -> settings.setSnakeSpeed(speed.SMALL);
                    }
                    playSound(clickSound);
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                    super.mouseEntered(e);
                    next3.setLocation(next3.getX(), next3.getY() + 10);
                }

                @Override
                public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                    super.mouseExited(e);
                    next3.setLocation(next3.getX(), next3.getY() - 10);
                }
            });

            back = new JLabel();     //Кнопка виходу з настройок
            back.setIcon(textures.back);
            back.setBounds(380, 470, textures.back.getIconWidth(), textures.back.getIconHeight());
            back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {     //Натиснута кнопка back
                    super.mouseReleased(e);
                    playSound(clickSound);
                    removeSettingsPanel();     //Видалення панелі налаштувань
                }

                @Override
                public void mouseEntered(MouseEvent e) {     //Анімація при наведенні курсором
                    super.mouseEntered(e);
                    back.setLocation(back.getX(), back.getY() - 10);
                }

                @Override
                public void mouseExited(MouseEvent e) {     //Анімація при забиранні курсора
                    super.mouseExited(e);
                    back.setLocation(back.getX(), back.getY() + 10);
                }
            });

            add(next1);
            add(next2);
            add(next3);
            add(back);
        }
    }
}
