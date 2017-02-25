package server;

import server.letter.Player;
import server.player.LetterDeck;
import server.player.LetterDeckImpl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class Game {

    private static final int MAX_PLAYER_LETTER = 7;

    private static Gameboard gameboard = new GameboardImpl();
    private static LetterDeck letterDeckForOneGame = new LetterDeckImpl();
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
            System.out.println("Вы не можете играть без второго игрока");
        }

        player1.setStatus(Player.Status.PLAY);
        player2.setStatus(Player.Status.PLAY);

        gameboard.init();
        gameboard.printToPlayers(player1, player2);

        writeAll("Добро пожаловать в игру");
        writeAll(String.format("Новая игра между игроком '%s' и '%s'\n", player1.getName(), player2.getName()));

        fillAllHands();

        activePlayer.set(player1);
        player1.write("Придумай первое слово и добавь его на Игровое поле");
        player1.write("Первое составленное слово будет проходить через центральную клетку.");
        player1.setFirstWord(true);
        player2.write("Первый ход у вашего противника. Ждем...");
    }

    public void changeActivePlayer(Player player) {
        if (player != player1 && activePlayer.get() != player1) {
            nextStepFromTo(player2, player1);
        } else if (player != player2 && activePlayer.get() != player2) {
            nextStepFromTo(player1, player2);
        } else {
            throw new IllegalArgumentException("не могу найти активного игрока для игры");
        }
    }

    private void setActivePlayer(Player player) {
        this.activePlayer.set(player);
    }

    private void nextStepFromTo(Player from, Player to) {
        setActivePlayer(to);
        fillHands(to);

        from.write("Ждем пока сходит соперник...");
        to.write("Теперь ваш ход.");
        to.write("Каждое новое слово должно соприкасаться или иметь общую букву (или буквы) с ранее составленными словами.");
        to.write("Слова должны читаться слева направо (по горизонтали) и сверху вниз (по вертикали).");

        to.write("Пример: i h v тест");
        to.write("i - начальная координата по горизонтали");
        to.write("h - начальная координата по вертикали");
        to.write("v - расположение слов по вертикали");
        to.write("тест - слово");

        showBalance();
    }

    private void showBalance() {
        player1.write("Ваш баланс: " + player1.getBalance());
        player2.write("Ваш баланс: " + player2.getBalance());
    }

    private void fillAllHands() {
        writeAll("Получить карточки с буквами");

        fillHands(player1);
        fillHands(player2);
    }

    private void fillHands(Player player) {
        player.write("Буквы, из которых вы можете составить слово:");
        // добавить описание,
        // ограничить время
        // не выводить игроку 2
        getLetterCardsInHands(player.getLetterCardsInHands());
        player.write(player.getLetterCardsInHands().toString());
    }

    private void writeAll(String msg) {
        player1.write(msg);
        player2.write(msg);
    }

    private void getLetterCardsInHands(Collection<Character> letterCardsInHands) {
        int size = letterCardsInHands.size();
        System.out.printf("Колличество карточек с буквами в ваших руках %s\n", size);

        for (int i = size; i < MAX_PLAYER_LETTER; i++) {
            Character letter = letterDeckForOneGame.poll();
            System.out.printf("Вы получили новые буквы: %s\n", letter);
            letterCardsInHands.add(letter);
        }
        System.out.printf("Колличество карточек с буквами в ваших руках после раздачи карточке %s\n", size);
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

        return false;
    }
    public boolean writeToGameboard(char[] word, int startX, int startY, Orientation orientation) {
        if (gameboard.write(word, startY, startX, orientation)) {
            gameboard.printToPlayers(player1, player2);
            return true;
        }

        return false;
    }

    public Player getActivePlayer() {
        return activePlayer.get();
    }

    public void finish(String reason) {
        isFinished = !isFinished;
        writeAll("Игра завершена. Причина " + reason);
    }

    public Integer checkAndGetIndexReferenceLetter(char[] word, int startX, int startY, Orientation orientation) {
        System.out.println("Идет проверка правила:");
        System.out.println("Каждое новое слово должно соприкасаться или иметь общую букву (или буквы) с ранее составленными словами.");
        return gameboard.checkAndGetIndexReferenceLetter(word, startX, startY, orientation);
    }
}

// TODO За один ход можно составить несколько слов.
// TODO правило количество гласных и согласных
