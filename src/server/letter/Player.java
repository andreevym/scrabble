package server.letter;

import server.Game;
import server.Manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player implements Runnable {

    private final static AtomicInteger numberOfPlayers = new AtomicInteger();
    private final static Manager manager = new Manager();
    private final static AtomicInteger maxPassTime = new AtomicInteger();
    private Socket client;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int number;
    private String name;
    private Status status;
    private List<Character> letterCardsInHands = new ArrayList<>();
    private Game game;
    private boolean isFirstWord = false;

    public Player(Socket client) {
        this.client = client;
        this.number = numberOfPlayers.incrementAndGet();
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

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            write("When you will ready to play just say: 'READY'");

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (!inputLine.isEmpty()) {

                    switch (status) {
                        case NOT_READY:
                            if (Status.READY.name().toLowerCase().equals(inputLine.toLowerCase())) {
                                setStatus(Status.READY);

                                addToReadyList();
                            } else {
                                write("Please write 'READY', when you will really READY");
                                System.out.println(name + ": " + inputLine);
                            }

                            break;
                        case READY:
                            write("Please wait player2");

                            break;
                        case PLAY:
                            if (this == game.getActivePlayer()) {
                                String[] strings = inputLine.split(" ");
                                // command can be executed after pass
                                if (strings[0].startsWith("finish")) {
                                    finish();
                                } else if (strings[0].startsWith("pass")) {
                                    if (strings.length != 2) {
                                        finish();
                                    } else {
                                        char[] passLetters = strings[1].toCharArray();

                                        for (Character letter : passLetters) {
                                            if (letterCardsInHands.contains(letter)) {
                                                letterCardsInHands.remove(letter);
                                                write("letter '" + letter + "' success passed");
                                            } else {
                                                write("letter '" + letter + "' not found");
                                            }
                                        }
                                    }
                                } else {
                                    if (strings.length == 4) {
                                        char[] word = strings[3].toCharArray();
                                        if (checkWord(word)) {
                                            if (game.write(word, strings)) {
                                                for (Character letter : word) {
                                                    letterCardsInHands.remove(letter);
                                                }
                                                game.changeActivePlayer(this);
                                            } else {
                                                write("Write the word");
                                                write("for example: 'k k vertical test'");
                                            }
                                        }
                                    } else {
                                        String word = strings[0];
                                        if (isFirstWord && !word.isEmpty()) {
                                            boolean allSuccessPassed = true;
                                            char[] charsOfWord = word.toCharArray();

                                            if(charsOfWord.length <= 0) {
                                                System.out.println("can't be here");
                                                throw new IllegalArgumentException("can't be here");
                                            }
                                            for (Character letter : charsOfWord) {
                                                if (letterCardsInHands.contains(letter)) {
                                                    write("letter '" + letter + "' success passed");
                                                } else {
                                                    allSuccessPassed = false;
                                                    write("letter '" + letter + "' not found");
                                                }
                                            }
                                            if(allSuccessPassed) {
                                                if(game.firstWrite(charsOfWord)) {
                                                    letterCardsInHands.remove(charsOfWord);
                                                }
                                                isFirstWord = false;
                                                game.changeActivePlayer(this);
                                            }
                                        } else {
                                            write("Write the word");
                                            write("for example: 'x y h test'");
                                            write("for example: 'x y v test'");
                                            write("k - coordinate start by x");
                                            write("k - coordinate start by y");
                                            write("h - horizontal");
                                            write("v - verstival");
                                            write("test - word");
                                        }
                                    }
                                }
                            } else {
                                write("Подождите пока другой игрок закончит свой ход");
                            }

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

    private void finish() {
        if (maxPassTime.incrementAndGet() > 4) {
            game.finish("Each of player 2 times skip game.");
        } else {
            game.changeActivePlayer(this);
        }
    }

    private boolean checkWord(char[] word) {
        boolean isValid = true;

        for (char c : word) {
            if (!letterCardsInHands.contains(c)) {
                write("letter '" + c + "' dose not exists");
                isValid = false;
            }
        }

        return isValid;
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
}
