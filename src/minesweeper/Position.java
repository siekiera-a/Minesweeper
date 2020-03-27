package minesweeper;

public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        Position pos;

        if (obj instanceof Position) {
            pos = (Position) obj;
        } else {
            return false;
        }

        return pos.x == x && pos.y == y;
    }
}
