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

public class Player implements Runnable {

    private final static AtomicInteger numberOfPlayers = new AtomicInteger();
    private final static Manager manager = new Manager();
    private final static AtomicInteger maxPassTime = new AtomicInteger();
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
        status = Status.NOT_READY;
    }

    private void addToReadyList() {
        System.out.println(name + ": was added to ready list");

        manager.startGame(this);
    }

    public void run() {
        System.out.printf("Player '%s' connected\n", name);

        try {
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            write("When you will ready to play just say: 'READY'");

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (!inputLine.isEmpty()) {

                    switch (status) {
                        case NOT_READY:
                            noReady(inputLine);

                            break;
                        case READY:
                            write("Please wait player2");

                            break;
                        case PLAY:
                            play(inputLine);

                            break;
                        default:
                            write("ERROR!!!");
                            throw new IllegalArgumentException("Your status not recognize!");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
            System.out.println(name + " have error");
            System.exit(-1);
        }

        System.out.println(name + " is exit");
    }

    private void noReady(String inputLine) {
        if (Status.READY.name().toLowerCase().contains(inputLine.toLowerCase())) {
            setStatus(Status.READY);

            addToReadyList();
        } else {
            write("Please writeToGameboard 'READY', when you will really READY");
            System.out.println(name + ": " + inputLine);
        }
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
            int startX = getIndexByString(strings[0]);
            int startY = getIndexByString(strings[1]);
            Orientation orientation = Orientation.getByValue(strings[2]);
            if (orientation == null) {
                write("Не удалось определить тип расположения слова h/v. Повторите еще раз");
                return;
            }
            char[] word = strings[3].toCharArray();

            System.out.println("Параметры:");
            System.out.println("startX = " + startX);
            System.out.println("startY = " + startY);
            System.out.println("orientation = " + orientation);
            System.out.println("word = " + Arrays.toString(word));

            boolean isLettersExists = checkExistsLetterInHands(word);
            if(!isLettersExists) {
                write("Таких букв нет у вас в руках");
                return;
            }

            boolean isReferenceExists = game.checkReference(word, startX, startY, orientation);
            if(!isReferenceExists) {
                write("Слово которое вы написали "
                        + "не пересекается ни с одним словом на доске.");
                write("Повторите еще раз.");
                return;
            }

            sayCommand(word, startX, startY, orientation);
        } else {
            String word = strings[0];
            if (isFirstWord && !word.isEmpty()) {
                sayFirstWord(word);
            } else {
                invalidCommand();
            }
        }
    }

    private void invalidCommand() {
        write("Не правильная команда. Повторите еще раз.");
    }

    private void sayFirstWord(String word) {
        char[] charsOfWord = word.toCharArray();

        if (charsOfWord.length <= 0) {
            System.out.println("can't be here");
            throw new IllegalArgumentException("can't be here");
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

    private boolean checkExistsLetterInHands(char[] word) {
        for (char letter : word) {
            if (!letterCardsInHands.contains(letter)) {
                write("letter '" + letter + "' dose not exists");
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
