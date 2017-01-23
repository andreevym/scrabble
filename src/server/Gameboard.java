package server;

import server.letter.Player;

/**
 * Игровая доска - взаимодействует с объектом server.Сroupier
 */
interface Gameboard {

    /***
     * Загружаем игровое поле 15x15
     */
    void init();

    /**
     * Игра может записать слово
     */
    boolean write(char[] word, int startY, int startX, Orientation orientation);

    /**
     * Игра может проверить слово
     */
    boolean check(char[] word);

    void printToPlayer(Player player);

    void printToPlayers(Player player1, Player player2);
}
