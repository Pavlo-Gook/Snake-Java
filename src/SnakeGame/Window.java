package SnakeGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class Window extends JFrame {     //Створення та налаштування вікна гри.
    public static final int SCREEN_WIDTH = 1006, SCREEN_HEIGHT = 653;

    public Window() {
        setTitle("Snake");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);     //Вікно без системної рамки
        setShape(new RoundRectangle2D.Double(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 10, 10));
        try {
            setIconImage(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/images/icon.png"))).getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        add(new MainPanel());     //Додаю на вікно головну панель
        setVisible(true);
    }

    public static void main(String[] args) {
        new Window();
    }
}
