import ConsoleUI.ConsoleUI;
import minesweeper.Minesweeper;
import minesweeper.MinesweeperUI;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Parameters: columns rows count_of_mines");
            return;
        }

        int columns;
        int rows;
        int countOfMines;

        try {
            columns = Integer.parseInt(args[0]);
            rows = Integer.parseInt(args[1]);
            countOfMines = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Parameters must be a numbers!");
            return;
        }

        Minesweeper game = null;
        MinesweeperUI ui = new ConsoleUI();

        while (game == null) {
            try {
                game = new Minesweeper(columns, rows, countOfMines);
                ui.play(game);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
