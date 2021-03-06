package server;

import server.letter.Player;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Объект Сервер.
 * Уровень взаимодействия с игроками.
 * Объени
 */
public class Application {

    /**
     * Unix systems reserve ports 1 through 1023 for administrative functions
     * leaving DEFAULT_PORT numbers greater than 1024 available for use.
     */
    private static final int DEFAULT_PORT = 1024;

    private static ServerSocket server;

    public static void main(String[] args) {
        listenSocket();
        connectPlayers();
    }

    private static void connectPlayers() {
        while (true) {
            try {
                Player player = new Player(server.accept());
                Thread thread = new Thread(player);
                thread.start();
            } catch (IOException e) {
                System.out.println("Не удалось принять нового игрока. Ошибка: " + e.getMessage());
                System.exit(-1);
            }
        }
    }

    private static void listenSocket() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
            System.out.println("Сервер не может быть поднят на этом порту");
            System.exit(-1);
        }
        System.out.printf("сервер запущен на порту %s\n", DEFAULT_PORT);
        System.out.printf("для соединения в терминале напишите: nc localhost %s\n", DEFAULT_PORT);
    }

    protected void finalize() {
        //Objects created in run method are finalized when
        //program terminates and thread exits
        try {
            server.close();
        } catch (IOException e) {
            System.out.println("Не удалось закрыть Socket");
            System.exit(-1);
        }
    }
}
