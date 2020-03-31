package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        // fill the game board with covered, empty fields
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Field field = new Field(FieldState.COVERED, 0);
                fields[y][x] = field;
            }
        }
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

    public Field[][] getFields() {
        return fields;
    }

    /**
     * return field with discovered mines
     */
    public Optional<Field[][]> getFinalBoard() {
        if (gameStatus == GameStatus.RUNNING || gameStatus == GameStatus.NOT_STARTED) {
            return Optional.empty();
        }

        // discover all mines
        for (Field[] row : fields) {
            for (Field field : row) {
                if (field.getNumber() < 0) {
                    field.setState(FieldState.DISCOVERED);
                }
            }
        }

        return Optional.of(fields);
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
     * Change state of given position
     */
    public void mark(Position position, Action action) {
        if (gameStatus != GameStatus.RUNNING) {
            return;
        }

        int x = position.x;
        int y = position.y;

        // check if position is on the board
        if (isBetween(0, rows - 1, y) && isBetween(0, columns - 1, x)) {

            // place mines and create game board if we try discover first time
            if (!firstDiscover && action == Action.DISCOVER) {
                firstDiscover = true;
                placeMines(position);
                createField();
            }

            Field field = fields[y][x];
            FieldState currentState = field.getState();

            if (action == Action.DISCOVER) {
                if (currentState == FieldState.COVERED) {
                    spread(x, y);
                }
            } else {
                FieldState newState = null;

                if (currentState == FieldState.MARKED) {
                    newState = FieldState.COVERED;
                } else if (currentState == FieldState.COVERED) {
                    newState = FieldState.MARKED;
                }

                if (newState != null) {
                    field.setState(newState);
                }
            }

            updateStatus();
        }
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

    private void spread(int x, int y) {
        // check if coordinates are on the game board
        if (!(isBetween(0, rows - 1, y) && isBetween(0, columns - 1, x))) {
            return;
        }

        Field field = fields[y][x];

        // spread only on covered fields
        if (field.getState() != FieldState.COVERED) {
            return;
        }

        field.setState(FieldState.DISCOVERED);

        // spread only on empty fields
        if (field.getNumber() != 0) {
            return;
        }

        for (int i = y - 1; i <= y + 1; i++) {
            for (int j = x - 1; j <= x + 1; j++) {
                // if coordinates are out of the game board, skip
                if (!(isBetween(0, rows - 1, i) && isBetween(0, columns - 1, j))) {
                    continue;
                }

                spread(j, i);
            }
        }
    }

    private void filterFields(FieldState state, List<Position> positions) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                if (fields[y][x].getState() == state) {
                    positions.add(new Position(x, y));
                }
            }
        }
    }

    private void updateStatus() {
        if (mines.isEmpty()) {
            return;
        }

        boolean anyMineDiscovered = Arrays.stream(fields)
          .flatMap(Arrays::stream)
          .anyMatch(field -> field.getNumber() < 0 && field.getState() == FieldState.DISCOVERED);

        // if any mine is discovered, game is lost
        if (anyMineDiscovered) {
            gameStatus = GameStatus.LOSS;
            return;
        }

        List<Position> positions = new ArrayList<>();
        filterFields(FieldState.MARKED, positions);

        // if count of marked fields is smaller than count of mines
        // check also covered fields
        if (positions.size() < mines.size()) {
            filterFields(FieldState.COVERED, positions);
        }

        if (positions.size() == mines.size()) {
            // remove all mines from list
            // if list is empty, we won
            positions.removeAll(mines);

            if (positions.isEmpty()) {
                gameStatus = GameStatus.WIN;
            }
        }
     }

}
