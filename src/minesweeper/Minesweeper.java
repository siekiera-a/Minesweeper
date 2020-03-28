package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minesweeper {

    public static final int MAX_COLUMNS = 30;
    public static final int MIN_COLUMNS = 8;

    public static final int MAX_ROWS = 24;
    public static final int MIN_ROWS = 8;

    public static final int MIN_MINES = 10;

    private final int columns;
    private final int rows;
    private final int countOfMines;
    private Field[][] fields;

    private final Random random = new Random();

    private final List<Position> mines = new ArrayList<>();

    private GameStatus gameStatus;

    // first discovered field is always empty
    private boolean firstDiscover;

    public Minesweeper(int columns, int rows, int countOfMines) throws IllegalArgumentException {

        if (!isBetween(MIN_COLUMNS, MAX_COLUMNS, columns)) {
            throw new IllegalArgumentException("Illegal number of columns");
        }

        if (!isBetween(MIN_ROWS, MAX_ROWS, rows)) {
            throw new IllegalArgumentException("Illegal number of rows");
        }

        int maxCountOfMines = columns * rows / 3;

        if (!isBetween(MIN_MINES, maxCountOfMines, countOfMines)) {
            throw new IllegalArgumentException("Illegal number of mines");
        }

        this.columns = columns;
        this.rows = rows;
        this.countOfMines = countOfMines;
        gameStatus = GameStatus.NOT_STARTED;
    }

    /**
     * Start new game
     */
    public void start() {
        fields = new Field[rows][columns];
        mines.clear();
        gameStatus = GameStatus.RUNNING;
        firstDiscover = false;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getCountOfMines() {
        return countOfMines;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Calculate mines around given position
     *
     * @return -1 if mine is on given position,
     * 0 if position is clear,
     * otherwise number of mines around
     */
    private int calculateMines(Position position) {
        if (mines.contains(position)) {
            return -1;
        }

        int countOfMines = 0;

        // check if there are any mines around position p
        for (int y = position.y - 1; y <= position.y + 1; y++) {
            for (int x = position.x - 1; x <= position.x + 1; x++) {
                if (!(position.x == x && position.y == y)) {
                    Position p = new Position(x, y);

                    if (mines.contains(p)) {
                        countOfMines++;
                    }
                }
            }
        }

        return countOfMines;
    }

    /**
     * calculate number of mines for each field
     * and set state to FieldState.MARKED or FieldState.COVERED
     */
    private void createField() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Position pos = new Position(x, y);
                int mines = calculateMines(pos);

                Field field = new Field(fields[y][x].getState(), mines);
                fields[y][x] = field;
            }
        }
    }

    /**
     * check if the number is in the range <min; max>
     */
    private boolean isBetween(int min, int max, int num) {
        return min <= num && num <= max;
    }

    /**
     * Place mines away from the freeOfMines position
     */
    private void placeMines(Position freeOfMines) {
        int minesPlaced = 0;
        int x = freeOfMines.x;
        int y = freeOfMines.y;

        while (minesPlaced < countOfMines) {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);

            // check if generated mine isn't around freeOfMines
            if (isBetween(x - 1, x + 1, column) && isBetween(y - 1, y + 1, row)) {
                continue;
            }

            Position position = new Position(column, row);

            if (!mines.contains(position)) {
                mines.add(position);
                minesPlaced++;
            }
        }
    }

}
