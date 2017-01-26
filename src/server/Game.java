package server;

import server.letter.Player;
import server.player.LetterDeck;
import server.player.LetterDeckImpl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class Game {

    private static final int MAX_PLAYER_LETTER = 7;

    private static LetterDeck letterDeckForOneGame = new LetterDeckImpl();
    private static Gameboard gameboard = new GameboardImpl();
    private AtomicReference<Player> activePlayer = new AtomicReference<>();
    private Player player1;
    private Player player2;

    private boolean isFinished = false;

    Game(Player player1) {
        this.player1 = player1;
    }

    void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    void play() {
        if (player1 == null || player2 == null) {
            System.out.println("Can't play without other player");
        }

        player1.setStatus(Player.Status.PLAY);
        player2.setStatus(Player.Status.PLAY);

        gameboard.init();
        gameboard.printToPlayers(player1, player2);

        System.out.printf("New Game started: '%s' play with '%s'\n", player1.getName(), player2.getName());
        writeAll("WELCOME TO THE GAME !!!");

        fillAllHands();

        activePlayer.set(player1);
        player1.write("Придумай первое слово и добавь его на Игровое поле");
        player1.setFirstWord(true);
        player2.write("Первый ход у вашего противника. Ждем...");
    }

    public void changeActivePlayer(Player player) {
        if (player != player1 && activePlayer.get() != player1) {
            changeActivePlayerFromTo(player2, player1);
        } else if (player != player2 && activePlayer.get() != player2) {
            changeActivePlayerFromTo(player1, player2);
        } else {
            throw new IllegalArgumentException("can't find active player for Game");
        }
    }

    private void changeActivePlayerFromTo(Player from, Player to) {
        activePlayer.set(to);
        fillHands(to);
        from.write("Ждем пока сходит соперник...");
        to.write("Теперь ваш ход.");
    }

    private void fillAllHands() {
        writeAll("fill hands");

        fillHands(player1);
        fillHands(player2);
    }

    private void fillHands(Player player) {
        player.write("Буквы из которых вы можите составить слово:");
        getLetterCardsInHands(player.getLetterCardsInHands());
        player.write(player.getLetterCardsInHands().toString());
    }

    private void writeAll(String msg) {
        player1.write(msg);
        player2.write(msg);
    }

    private void getLetterCardsInHands(Collection<Character> letterCardsInHands) {
        int size = letterCardsInHands.size();
        System.out.printf("in your hands %s letters\n", size);

        for (int i = size; i < MAX_PLAYER_LETTER; i++) {
            Character letter = letterDeckForOneGame.poll();
            System.out.printf("you are get new letter: %s\n", letter);
            letterCardsInHands.add(letter);
        }
    }
    public boolean firstWrite(char[] word) {
        int startY = Gameboard.GEMEBOARD_HEIGHT / 2;

        int startXHalf = Gameboard.GEMEBOARD_HEIGHT / 2;
        Orientation orientation = Orientation.HORIZONTAL;

        int wordHalf = word.length / 2;
        int startX = startXHalf - wordHalf;

        if (gameboard.write(word, startY, startX, orientation)) {
            gameboard.printToPlayers(player1, player2);
            return true;
        }

        System.out.println("FALSE");
        return false;
    }
    public boolean write(char[] word, String[] strings) {
        int startY = getIndexByString(strings[0]);
        int startX = getIndexByString(strings[1]);

        Orientation orientation = null;
        if (strings[2].startsWith("v")) {
            orientation = Orientation.VERTICAL;
        } else if (strings[2].startsWith("h")) {
            orientation = Orientation.HORIZONTAL;
        }

        if (gameboard.write(word, startY, startX, orientation)) {
            gameboard.printToPlayers(player1, player2);
            return true;
        }

        System.out.println("FALSE");
        return false;
    }

    private int getIndexByString(String s) {
        int charAt = s.charAt(0);
        int a = 'a';
        return charAt - a + 1;
    }

    public Player getActivePlayer() {
        return activePlayer.get();
    }

    public void finish(String reason) {
        isFinished = !isFinished;
        writeAll("GAME IS FINISHED. reason " + reason);
    }
}

// Слова должны читаться слева направо (по горизонтали) и сверху вниз (по вертикали).
// За один ход можно составить несколько слов.
// Каждое новое слово должно соприкасаться или иметь общую букву (или буквы) с ранее составленными словами.
// правило количество гласных и согласных
// Первое составленное слово должно проходить через центральную клетку.