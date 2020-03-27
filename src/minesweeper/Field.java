package minesweeper;

public class Field {

    /*
        COVERED
        DISCOVERED
        MARKED
     */
    private FieldState state;

    /*
        negative value - mine
        value = 0 - empty field
        positive value - number
     */
    private final int number;

    public Field(FieldState state, int number) {
        this.state = state;
        this.number = number;
    }

    public FieldState getState() {
        return state;
    }

    public int getNumber() {
        return number;
    }

    public void setState(FieldState state) {
        this.state = state;
    }
}
