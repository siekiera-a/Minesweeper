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
    }

}
