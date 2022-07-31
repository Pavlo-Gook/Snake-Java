package SnakeGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

//Клас в якому зберігаютья всі картинки, що використовуються в грі
public class Textures {
    //Title bar
    public final ImageIcon exitTitle, exitTitleMouse, soundOn, soundOnMouse, soundOff, soundOffMouse;
    public final Image titleScore, cup;
    //Menu
    public final Image titleText, bigSnake;
    public final ImageIcon play, settings, exit;
    //Settings
    public final Image titleSettings;
    public final ImageIcon next, back;
    public final Image classicalType, teleportType, barrierType;
    public final Image blueColor, greenColor, orangeColor, pinkColor, violetColor;
    public final Image speedSmall, speedMedium, speedLarge;
    //Game
    public final Image apple, grass, barrier;
    private Image head, body, bodyAngle, tail;

    public Textures() {
        //Title bar
        exitTitle = getImageIcon("resources/images/titleBar/exitTitle.png");
        exitTitleMouse = getImageIcon("resources/images/titleBar/exitTitleMouse.png");
        soundOn = getImageIcon("resources/images/titleBar/soundOn.png");
        soundOnMouse = getImageIcon("resources/images/titleBar/soundOnMouse.png");
        soundOff = getImageIcon("resources/images/titleBar/soundOff.png");
        soundOffMouse = getImageIcon("resources/images/titleBar/soundOffMouse.png");
        titleScore = getImageIcon("resources/images/titleBar/titleScore.png").getImage();
        cup = getImageIcon("resources/images/titleBar/cup.png").getImage();
        //Menu
        titleText = getImageIcon("resources/images/menu/titleText.png").getImage();
        bigSnake = getImageIcon("resources/images/menu/bigSnake.png").getImage();
        play = getImageIcon("resources/images/menu/play.png");
        settings = getImageIcon("resources/images/menu/settings.png");
        exit = getImageIcon("resources/images/menu/exit.png");
        //Settings
        titleSettings = getImageIcon("resources/images/settings/titleSettings.png").getImage();
        next = getImageIcon("resources/images/settings/next.png");
        back = getImageIcon("resources/images/settings/back.png");
        classicalType = getImageIcon("resources/images/settings/classicalType.png").getImage();
        teleportType = getImageIcon("resources/images/settings/teleportType.png").getImage();
        barrierType = getImageIcon("resources/images/settings/barrierType.png").getImage();
        blueColor = getImageIcon("resources/images/settings/blueColor.png").getImage();
        greenColor = getImageIcon("resources/images/settings/greenColor.png").getImage();
        orangeColor = getImageIcon("resources/images/settings/orangeColor.png").getImage();
        pinkColor = getImageIcon("resources/images/settings/pinkColor.png").getImage();
        violetColor = getImageIcon("resources/images/settings/violetColor.png").getImage();
        speedSmall = getImageIcon("resources/images/settings/speedSmall.png").getImage();
        speedMedium = getImageIcon("resources/images/settings/speedMedium.png").getImage();
        speedLarge = getImageIcon("resources/images/settings/speedLarge.png").getImage();
        //Game
        apple = getImageIcon("resources/images/game/apple.png").getImage();
        grass = getImageIcon("resources/images/game/grass.png").getImage();
        barrier = getImageIcon("resources/images/game/barrier.png").getImage();
        head = getImageIcon("resources/images/game/blueSnake/head.png").getImage();
        body = getImageIcon("resources/images/game/blueSnake/body.png").getImage();
        bodyAngle = getImageIcon("resources/images/game/blueSnake/bodyAngle.png").getImage();
        tail = getImageIcon("resources/images/game/blueSnake/tail.png").getImage();
    }

    public void updateSnakeColor(GameSettings settings) {
        //Зміна текстур змійки відносно настройок гри
        switch (settings.getSnakeColor()) {
            case BLUE:
                head = getImageIcon("resources/images/game/blueSnake/head.png").getImage();
                body = getImageIcon("resources/images/game/blueSnake/body.png").getImage();
                bodyAngle = getImageIcon("resources/images/game/blueSnake/bodyAngle.png").getImage();
                tail = getImageIcon("resources/images/game/blueSnake/tail.png").getImage();
                break;
            case GREEN:
                head = getImageIcon("resources/images/game/greenSnake/head.png").getImage();
                body = getImageIcon("resources/images/game/greenSnake/body.png").getImage();
                bodyAngle = getImageIcon("resources/images/game/greenSnake/bodyAngle.png").getImage();
                tail = getImageIcon("resources/images/game/greenSnake/tail.png").getImage();
                break;
            case ORANGE:
                head = getImageIcon("resources/images/game/orangeSnake/head.png").getImage();
                body = getImageIcon("resources/images/game/orangeSnake/body.png").getImage();
                bodyAngle = getImageIcon("resources/images/game/orangeSnake/bodyAngle.png").getImage();
                tail = getImageIcon("resources/images/game/orangeSnake/tail.png").getImage();
                break;
            case PINK:
                head = getImageIcon("resources/images/game/pinkSnake/head.png").getImage();
                body = getImageIcon("resources/images/game/pinkSnake/body.png").getImage();
                bodyAngle = getImageIcon("resources/images/game/pinkSnake/bodyAngle.png").getImage();
                tail = getImageIcon("resources/images/game/pinkSnake/tail.png").getImage();
                break;
            case VIOLET:
                head = getImageIcon("resources/images/game/violetSnake/head.png").getImage();
                body = getImageIcon("resources/images/game/violetSnake/body.png").getImage();
                bodyAngle = getImageIcon("resources/images/game/violetSnake/bodyAngle.png").getImage();
                tail = getImageIcon("resources/images/game/violetSnake/tail.png").getImage();
        }
    }

    private ImageIcon getImageIcon(String name) {
        try {     //Отримуємо шлях до зображення
            return new ImageIcon(ImageIO.read(getClass().getResourceAsStream(name)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Image getHead() {
        return head;
    }

    public Image getBody() {
        return body;
    }

    public Image getBodyAngle() {
        return bodyAngle;
    }

    public Image getTail() {
        return tail;
    }
}
