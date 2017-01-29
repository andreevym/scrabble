package server;

import server.letter.Player;

import java.util.Arrays;

class GameboardImpl implements Gameboard {

    private static char[][] GAMEBOARD;

    @Override
    public boolean checkReference(char[] word, int startX, int startY, Orientation orientation) {
        int length = word.length;

        int endX = startX;
        int endY = startY;

        int indexX = startX;
        int indexY = startY;

        if (orientation == Orientation.HORIZONTAL) {
            endX += length;
        } else if (orientation == Orientation.VERTICAL) {
            endY += length;
        }

/*        System.out.println("endX = " + endX);
        System.out.println("endY = " + endY);
        System.out.println("indexX = " + indexX);
        System.out.println("indexY = " + indexY);

        System.out.println("word = " + Arrays.toString(word));
        System.out.println("length = " + length);*/

        int indexWord = 0;
        while (indexX !=  endX || indexY != endY) {

/*
            System.out.println("______________________________________________");
            System.out.println("indexWord = " + indexWord);

            System.out.println("indexX = " + indexX);
            System.out.println("indexY = " + indexY);

            System.out.println("word[indexWord] = " + word[indexWord]);
            System.out.println("GAMEBOARD[indexX][indexY] = " + GAMEBOARD[indexY][indexX]);
*/

            if (word[indexWord] == GAMEBOARD[indexY][indexX]) {
                return true;
            }

            if (orientation == Orientation.HORIZONTAL) {
                indexX++;
            } else if (orientation == Orientation.VERTICAL) {
                indexY++;
            }
            indexWord++;
        }
        return false;
    }

    /*public static void main(String[] args) {
        char[] word = {'a', 'b', 'c'};
        int startX = 0;
        int startY = 0;
        GAMEBOARD = new char[15][15];
        GAMEBOARD[1][0] = 'b';
        boolean checkReference = checkReference(word, startX, startY, Orientation.HORIZONTAL);
        System.out.println(checkReference);
    }
*/
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

        System.out.printf("Gameboard Was init %s x %s\n", GEMEBOARD_HEIGHT-1, GEMEBOARD_WIDTH-1);
        printGemeboard();
    }

    private void printGemeboard() {
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
            // TODO write TEST
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

        boolean result = false;
        try {
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (result) {
            printGemeboard();
        } else {
            printError();
        }
        return result;
    }

    private void printError() {
        System.out.println("Ошибка при записи слова");
    }

}
