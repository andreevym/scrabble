package server;

import server.letter.Player;

import java.util.Arrays;

public class GameboardImpl implements Gameboard {

    private char[][] GAMEBOARD;

    @Override
    public void init() {
        GAMEBOARD = new char[GEMEBOARD_HEIGHT][GEMEBOARD_WIDTH];

        for (int i = 1; i < GEMEBOARD_HEIGHT; i++) {
            GAMEBOARD[0][i] = (char) ('a' - 1 + i);
        }

        for (int i = 1; i < GEMEBOARD_WIDTH; i++) {
            GAMEBOARD[i][0] = (char) ('a' - 1 + i);
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

        forWriteWord(word, startY, startX);

        return true;
    }

    private void forWriteWord(char[] word, int startY, int startX) {
        int indexGemeboard = startX;
        int indexWord = 0;

        while(indexWord < word.length) {
            char c = GAMEBOARD[startY][indexGemeboard];
            System.out.println("c: " + c);

            if (c != s) {
                // throw new IllegalArgumentException("char [ " + indexGameboard + ", " + startX + "] exists : " + GAMEBOARD[indexGameboard][startX]);
                System.out.println("word don't exists indexGemeboard: " + indexGemeboard);
                System.out.println("word don't exists startX: " + startY);
            } else {
                GAMEBOARD[startY][indexGemeboard] = word[indexWord];
                indexWord++;
            }
            indexGemeboard++;
        }
    }

    private boolean writeVertical(char[] word, int startY, int startX) {
        if ((GEMEBOARD_HEIGHT - (startY + 1)) < word.length) {
            return false;
        }

        forWriteWord2(word, startY, startX);

        return true;
    }

    private char s = '-';

    private void forWriteWord2(char[] word, int startY, int startX) {
        int indexGemeboard = startY;
        int indexWord = 0;

        while(indexWord < word.length) {
            char c = GAMEBOARD[indexGemeboard][startX];
            System.out.println("c: " + c);

            if (c != s) {
                // throw new IllegalArgumentException("char [ " + indexGameboard + ", " + startX + "] exists : " + GAMEBOARD[indexGameboard][startX]);
                System.out.println("word don't exists indexGemeboard: " + indexGemeboard);
                System.out.println("word don't exists startX: " + startX);
            } else {
                GAMEBOARD[indexGemeboard][startX] = word[indexWord];
                indexWord++;
            }
            indexGemeboard++;
        }
    }

    /**
     * Написать слово
     *
     * @param word        слово
     * @param startY      - сдвиг по высоте (от 0)
     * @param startX      - сдвиг по ширине (от 0)
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

        if (result) {
            printToServer();
        }
        return result;
    }

    @Override
    public boolean check(char[] word) {
        return false;
    }
}
