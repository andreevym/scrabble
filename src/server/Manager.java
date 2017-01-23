package server;

import server.letter.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static server.letter.Player.Status.READY;

public class Manager {
    private static ConcurrentLinkedQueue<Player> readyToPlayPlayers = new ConcurrentLinkedQueue<>();
    private static List<Game> games = new CopyOnWriteArrayList<>();

    public void startGame(Player player) {
        if(player == null) {
            throw new IllegalArgumentException("player can't be null");
        }
        Optional<Player> readyToPlayPlayerOptional = readyToPlayPlayers.stream().filter(playerByFilter -> READY == playerByFilter.getStatus())
                .findFirst();

        if(readyToPlayPlayerOptional.isPresent()) {
            Player readyToPlayPlayer = readyToPlayPlayerOptional.get();

            readyToPlayPlayers.remove(readyToPlayPlayer);

            Game game = readyToPlayPlayer.getGame();
            game.setPlayer2(player);
            player.setGame(game);
            game.play();
        } else {
            Game game = new Game(player);
            games.add(game);
            player.setGame(game);
            readyToPlayPlayers.add(player);
        }
    }
}
