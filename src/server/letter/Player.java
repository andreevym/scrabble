package server.letter;

import server.Game;
import server.Manager;
import server.Orientation;
import server.player.LetterEnum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static server.letter.Player.Status.NOT_READY;
import static server.letter.Player.Status.READY;

public class Player implements Runnable {

    private final static AtomicInteger numberOfPlayers = new AtomicInteger();
    private final static Manager manager = new Manager();
    private final static AtomicInteger maxPassTime = new AtomicInteger();
    private final String msg = "Когда вы будите готовы к игре введите: 'r' или 'ready'";
    private Socket client;
    private BufferedWriter bufferedWriter;
    private String name;
    private Status status;
    private List<Character> letterCardsInHands = new ArrayList<>();
    private Game game;
    private boolean isFirstWord = false;
    private int balance;

    public Player(Socket client) {
        this.client = client;
        int number = numberOfPlayers.incrementAndGet();
        this.name = "Player" + number;
        status = NOT_READY;
    }

    private void addToReadyList() {
        System.out.printf("%s: был добавлен в список игроков готовых играть\n", name);

        manager.startGame(this);
    }

    public void run() {
        System.out.printf("К нам присоединился игрок '%s'\n", name);

        try {
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            write(msg);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (!inputLine.isEmpty()) {

                    switch (status) {
                        case NOT_READY:
                            noReady(inputLine);

                            break;
                        case READY:
                            write("Пожалуйста подождите игрока 2");

                            break;
                        case PLAY:
                            play(inputLine);

                            break;
                        default:
                            write("Возникла техническая ошибка.");
                            throw new IllegalArgumentException(String.format("Статус игрока %s не может быть распознан!", name));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.out.printf("У игрока %s возникла ошибка\n", name);
            System.exit(-1);
        }

        System.out.println(name + " уже существует");
    }

    private void noReady(String inputLine) {
        System.out.println(name + ": " + inputLine);
        if (isReady(inputLine)) {
            setStatus(READY);
            addToReadyList();
            write("Пожалуйста подождите...");
        } else {
            write(msg);
        }
    }

    private boolean isReady(String inputLine) {
        return READY.name().toLowerCase().contains(inputLine.toLowerCase());
    }

    private void play(String inputLine) {
        if (this == game.getActivePlayer()) {
            String[] strings = inputLine.split(" ");
            // command can be executed after pass
            if (strings[0].startsWith(Command.FINISH)) {
                finish();
            } else if (strings[0].startsWith(Command.PASS)) {
                pass(strings);
            } else {
                sayWord(strings);
            }
        } else {
            write("Подождите пока другой игрок закончит свой ход");
        }
    }

    private int getIndexByString(String s) {
        int charAt = s.charAt(0);
        int a = 'a';
        return charAt - a + 1;
    }

    private void sayWord(String[] strings) {
        if (strings.length == 4) {
            sayWord(strings[3].toCharArray(), getIndexByString(strings[0]), getIndexByString(strings[1]), Orientation.getByValue(strings[2]));
        } else {
            String word = strings[0];
            if (isFirstWord && !word.isEmpty()) {
                sayFirstWord(word);
            } else {
                invalidCommand();
            }
        }
    }

    private void sayWord(char[] word, int startX, int startY, Orientation orientation) {
        if(isChecked(word, startX, startY, orientation)) {
            sayCommand(word, startX, startY, orientation);
        }

    }

    private boolean isChecked(char[] word, int startX, int startY, Orientation orientation)  {
        System.out.println("Идет проверка..");
        if(isCheckedParameters(word, startX, startY, orientation)) {
            return false;
        }

        final Integer indexReferenceLetter = game.checkAndGetIndexReferenceLetter(word, startX, startY, orientation);
        if(indexReferenceLetter == null) {
            write("Слово которое вы написали не пересекается ни с одним словом на доске.");
            write("Повторите еще раз.");
            write("Пример: i h v тест");
            write("i - начальная координата по горизонтали");
            write("h - начальная координата по вертикали");
            write("v - расположение слов по вертикали");
            write("тест - слово");
            return false;
        }

        if(!checkExistsLetterInHands(word, indexReferenceLetter)) {
            return false;
        }

        return true;
    }

    private boolean isCheckedParameters(char[] word, int startX, int startY, Orientation orientation) {
        System.out.println("Проверяем параметры");
        System.out.println("Параметры:");
        System.out.println("startX = " + startX);
        System.out.println("startY = " + startY);
        System.out.println("orientation = " + orientation);
        System.out.println("слово = " + Arrays.toString(word));

        if (orientation == null) {
            write("Не удалось определить тип расположения слова h/v. Повторите еще раз");
            return false;
        }

        return false;
    }

    private void invalidCommand() {
        write("Не правильная команда. Повторите еще раз.");
    }

    private void sayFirstWord(String word) {
        char[] charsOfWord = word.toCharArray();

        if (charsOfWord.length <= 0) {
            System.out.println("Не может быть здесь");
            throw new IllegalArgumentException("Не может быть здесь");
        }

        if (isAllLetterInHands(charsOfWord)) {
            if (game.firstWrite(charsOfWord)) {
                handleLettersInHands(charsOfWord);
                game.changeActivePlayer(this);
                isFirstWord = false;
            }
        }
    }

    private boolean isAllLetterInHands(char[] charsOfWord) {
        for (Character letter : charsOfWord) {
            if (!letterCardsInHands.contains(letter)) {
                return false;
            }
        }
        return true;
    }

    private void sayCommand(char[] word, int startX, int startY, Orientation orientation) {
        if (game.writeToGameboard(word, startX, startY, orientation)) {
            handleLettersInHands(word);
            game.changeActivePlayer(this);
        } else {
            invalidCommand();
        }
    }

    private void handleLettersInHands(char[] word) {
        for (Character letter : word) {
            addToBalance(letter);
            letterCardsInHands.remove(letter);
        }
    }

    private void addToBalance(Character letter) {
        balance += LetterEnum.valueOf(letter.toString()).getCost();
    }

    public int getBalance() {
        return balance;
    }

    private void pass(String[] strings) {
        if (strings.length != 2) {
            finish();
        } else {
            char[] passLetters = strings[1].toCharArray();

            boolean isAllLettersExists = true;

            for (Character letter : passLetters) {
                if (!letterCardsInHands.contains(letter)) {
                    isAllLettersExists = false;
                    write("буква '" + letter + "' не найдена");
                }
            }

            if (isAllLettersExists) {
                for (Character letter : passLetters) {
                    letterCardsInHands.remove(letter);
                }
                finish();
            } else {
                write("Пожалуйста попробуйте еще раз. "
                        + "Нужно указывать только существующие буквы. Спасибо.");
            }
        }
    }

    private void finish() {
        if (maxPassTime.incrementAndGet() > 4) {
            game.finish("Each of player 2 times skip game.");
        } else {
            game.changeActivePlayer(this);
        }
    }

    /*public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println("i = " + i);
            if(i == 2) {
                System.out.println("i is 2");
                continue;
            }
            System.out.println("other check i : " + i);
        }
    }*/

    private boolean checkExistsLetterInHands(char[] word, Integer indexReferenceLetter) {
        System.out.println("checkExistsLetterInHands");

        for (int i = 0; i < word.length; i++) {
            System.out.println("i = " + i);

            if(i == indexReferenceLetter) {
                // т.к эта буква есть на игровом поле, здесь проверка не нужна
                continue;
            }
            char letter = word[i];
            if (!letterCardsInHands.contains(letter)) {
                System.out.printf("У вас нет на руках буквы %s\n'", letter);
                write(String.format("У вас нет на руках буквы %s\n'", letter));
                return false;
            }
        }
        return true;
    }

    public void write(String msg) {
        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Character> getLetterCardsInHands() {
        return letterCardsInHands;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setFirstWord(boolean firstWord) {
        isFirstWord = firstWord;
    }

    public enum Status {
        NOT_READY,
        READY,
        PLAY;
    }

    private static class Command {
        static final String PASS = "pass";
        static final String FINISH = "finish";
    }
}
