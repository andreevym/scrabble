package server;

import server.letter.Player;

/**
 * Игровая доска - взаимодействует с объектом server.Сroupier
 */
interface Gameboard {

    int GEMEBOARD_HEIGHT = 16;
    int GEMEBOARD_WIDTH = 16;

    /***
     * Загружаем игровое поле 15x15
     */
    void init();

    /**
     * Игра может записать слово
     */
    boolean write(char[] word, int startY, int startX, Orientation orientation);

    void printToPlayer(Player player);

    void printToPlayers(Player player1, Player player2);

    boolean checkReference(char[] word, int startX, int startY, Orientation orientation);

}
