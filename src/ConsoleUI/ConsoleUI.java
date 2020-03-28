package ConsoleUI;

import minesweeper.Action;
import minesweeper.GameStatus;
import minesweeper.Minesweeper;
import minesweeper.MinesweeperUI;
import minesweeper.Position;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsoleUI implements MinesweeperUI {

    private Minesweeper game;

    private final Scanner scanner = new Scanner(System.in);

    private class Payload {
        Position position;
        Action action;

        public Payload(Position position, Action action) {
            this.position = position;
            this.action = action;
        }
    }

    @Override
    public void play(Minesweeper game) {
        this.game = game;
        run();
    }

    @Override
    public void playAgain() {
        if (game == null) {
            return;
        }

        run();
    }

    @Override
    public boolean gameEnded() {
        if (game == null) {
            return false;
        }

        GameStatus status = game.getGameStatus();
        return status == GameStatus.WIN || status == GameStatus.LOSS;
    }

    private int columnWidth() {
        return String.valueOf(game.getColumns()).length();
    }

    private void drawXCoordinates() {
        String format = "%" + columnWidth() + "s";

        String coordinates = IntStream.range(1, game.getColumns() + 1)
          .mapToObj(String::valueOf)
          .map(s -> String.format(format, s))
          .collect(Collectors.joining(" "));

        System.out.println(" ".repeat(rowWidth()) + "│" + coordinates + "│");
    }

    /**
     * @return position and action; otherwise empty
     */
    private Optional<Payload> getPositionAndAction() {
        System.out.print("Enter X Y Action: ");
        String[] data = scanner.nextLine().split("\\s+");

        if (data.length != 3) {
            System.out.println("Invalid number of arguments!");
            return Optional.empty();
        }

        Position position;

        try {
            // -1 because indexes start at 0
            int x = Integer.parseInt(data[0]) - 1;
            int y = Integer.parseInt(data[1]) - 1;
            position = new Position(x, y);
        } catch (NumberFormatException e) {
            System.out.println("Wrong coordinates!");
            return Optional.empty();
        }

        String command = data[2];
        Action action;

        if (command.equalsIgnoreCase("free")) {
            action = Action.DISCOVER;
        } else if (command.equalsIgnoreCase("mine")) {
            action = Action.MARK;
        } else {
            System.out.println("Unsupported action!");
            return Optional.empty();
        }

        return Optional.of(new Payload(position, action));
    }

    private int rowWidth() {
        return String.valueOf(game.getRows()).length();
    }

    private void run() {
        game.start();
        GameStatus status = game.getGameStatus();

        while (status == GameStatus.RUNNING) {
            drawXCoordinates();
            // TODO draw field

            Optional<Payload> payload = getPositionAndAction();

            if (payload.isEmpty()) {
                continue;
            }

            Payload positionAndAction = payload.get();
            game.mark(positionAndAction.position, positionAndAction.action);
            status = game.getGameStatus();
        }
    }
}
