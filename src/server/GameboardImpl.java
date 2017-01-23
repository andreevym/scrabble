package server;

import server.letter.Player;

import java.util.Arrays;

public class GameboardImpl implements Gameboard {

    private int GEMEBOARD_HEIGHT = 16;
    private int GEMEBOARD_WIDTH = 16;

    private char[][] GAMEBOARD;

    @Override
    public void init() {
        GAMEBOARD = new char[GEMEBOARD_HEIGHT][GEMEBOARD_WIDTH];

        for (int i = 1; i < GEMEBOARD_HEIGHT; i++) {
            GAMEBOARD[0][i] = (char) ('a'-1+i);
        }

        for (int i = 1; i < GEMEBOARD_WIDTH; i++) {
            GAMEBOARD[i][0] = (char) ('a'-1+i);
        }

        for (int h = 1; h < GEMEBOARD_HEIGHT; h++) {
            for (int w = 1; w < GEMEBOARD_WIDTH; w++) {
                GAMEBOARD[h][w] = '-';
            }
        }

        System.out.printf("Gameboard Was init %s x %s\n", GEMEBOARD_HEIGHT, GEMEBOARD_WIDTH);
        printToServer();
    }

    private void printToServer() {
        for (int h = 0; h < GEMEBOARD_HEIGHT; h++) {
            for (int w = 0; w < GEMEBOARD_WIDTH; w++) {
                System.out.print(GAMEBOARD[h][w]);
            }
            System.out.println();
        }
    }

    @Override
    public void printToPlayer(Player player) {
        for (int h = 0; h < GEMEBOARD_HEIGHT; h++) {
            player.write(Arrays.toString(GAMEBOARD[h]));
        }
    }

    @Override
    public void printToPlayers(Player player1, Player player2) {
        printToPlayer(player1);
        printToPlayer(player2);
    }

    private boolean writeHorizontal(char[] word, int startY, int startX) {
        if ((GEMEBOARD_WIDTH - (startX + 1)) < word.length) {
            return false;
        }

        for (int indexGameboard = startX, indexWord = 0;
             indexGameboard < startX + word.length; indexGameboard++) {
            GAMEBOARD[startY][indexGameboard] = word[indexWord];
            indexWord++;
        }
        // TODO проверить.
        return true;
    }

    private boolean writeVertical(char[] word, int startY, int startX) {
        if ((GEMEBOARD_HEIGHT - (startY + 1)) < word.length) {
            return false;
        }

        for (int indexGameboard = startY, indexWord = 0;
             indexGameboard < startY + word.length; indexGameboard++) {
            GAMEBOARD[indexGameboard][startX] = word[indexWord];
            indexWord++;
        }
        // TODO проверить.
        return true;
    }

    /**
     * Написать слово
     *
     * @param word      слово
     * @param startY    - сдвиг по высоте (от 0)
     * @param startX    - сдвиг по ширине (от 0)
     * @param orientation написать слово по горизонтали или по вертикали?
     */
    public boolean write(char[] word, int startY, int startX, Orientation orientation) {
        if (word == null || word.length == 0) {
            return false;
        }

        boolean result;
        switch (orientation) {
            case HORIZONTAL:
                result = writeHorizontal(word, startY, startX);
                break;
            case VERTICAL:
                result = writeVertical(word, startY, startX);
                break;
            default:
                return false;
        }

        if(result) {
            printToServer();
        }
        return result;
    }

    @Override
    public boolean check(char[] word) {
        return false;
    }
}
