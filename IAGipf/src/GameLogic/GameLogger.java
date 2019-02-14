package GameLogic;

import GameLogic.Game.GameType;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Created by frans on 5-10-2015.
 */
public class GameLogger implements Serializable {
    private final Instant gameStartedTime;
    public final LinkedList<String> logMessages;                // Messages displayed in the log in the window (if there is a GipfWindow instance connected to this game)

    public GameLogger(GameType gameType) {
        logMessages = new LinkedList<>();
        gameStartedTime = Instant.now();
        log("Started a new " + gameType + " GIPF game.");
    }

    public void log(String debug) {
        Duration durationOfGame = Duration.between(gameStartedTime, Instant.now());
        LocalTime time = LocalTime.ofNanoOfDay(durationOfGame.toNanos());
        String timeString = time.format(DateTimeFormatter.ofPattern("[HH:mm:ss.SSS]"));
        logMessages.add(timeString + ": " + debug);
    }

    public boolean isEmpty() { return logMessages.isEmpty(); }
}
