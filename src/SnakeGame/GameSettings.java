package SnakeGame;

//Тип гри: класичний, з можливістю виходом за межі карти та з перешкодами
enum type {CLASSICAL, TELEPORT, BARRIER}
//Колір змійки
enum color {BLUE, GREEN, ORANGE, PINK, VIOLET}
//Швидкість руху змійки
enum speed {SMALL, MEDIUM, LARGE}

public class GameSettings {
    private type typeOfGame = type.CLASSICAL;     //Початкові настройки
    private color snakeColor = color.BLUE;
    private speed snakeSpeed = speed.SMALL;

    public type getTypeOfGame() {
        return typeOfGame;
    }

    public void setTypeOfGame(type typeOfGame) {
        this.typeOfGame = typeOfGame;
    }

    public color getSnakeColor() {
        return snakeColor;
    }

    public void setSnakeColor(color snakeColor) {
        this.snakeColor = snakeColor;
    }

    public speed getSnakeSpeed() {
        return snakeSpeed;
    }

    public void setSnakeSpeed(speed snakeSpeed) {
        this.snakeSpeed = snakeSpeed;
    }
}
