# Minesweeper
### Game minesweeper
Usage: java Main.class columns rows count_of_mines

### Gameplay

```
 │1 2 3 4 5 6 7 8 9│
————————————————————
1│· · · · · · · · ·│
2│· · · · · · · · ·│
3│· · · · · · · · ·│
4│· · · · · · · · ·│
5│· · · · · · · · ·│
6│· · · · · · · · ·│
7│· · · · · · · · ·│
8│· · · · · · · · ·│
9│· · · · · · · · ·│
————————————————————
Enter X Y Action: 
```
#### Coordinates
X - X coordinate

Y - Y coordinate

#### Action (mine/free) (case insensitive)
any action: if chosen field is discovered, take no action

mine: if chosen field is marked as mined, change state to undiscovered; otherwise change state to mined

free: discover field only if state is equal undiscovered