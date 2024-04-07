/**
 * @author Anson
 * @version 1
 * @since 03/12/2023
 */
public class Game_status {
	public static final int not_thing = 0;
	public static final int win1 = 1;
	public static final int win2 = 2;
	public static final int draw = 3;

	private boolean isPlayer1Turn = true;
	private int[][] gameboard = new int[3][3];
	private int count = 0;
	
	public int checkResult() {
		int row, col, d1, d2;
		//row case
		for (int i = 0; i < 3; i++) {
			row = gameboard[i][0] + gameboard[i][1] + gameboard[i][2];
			if (row == 3)
				return Game_status.win1;
			if (row == -3)
				return Game_status.win2;
		}
		//column case
		for (int j = 0; j < 3; j++) {
			col = gameboard[0][j] + gameboard[1][j] + gameboard[2][j];
			if (col == 3)
				return Game_status.win1;
			if (col == -3)
				return Game_status.win2;
		}
		//diagonal case1
		d1 = gameboard[0][0] + gameboard[1][1] + gameboard[2][2];
		if (d1 == 3)
			return Game_status.win1;
		if (d1 == -3)
			return Game_status.win2;

		d2 = gameboard[2][0] + gameboard[1][1] + gameboard[0][2];
		if (d2 == 3)
			return Game_status.win1;
		if (d2 == -3)
			return Game_status.win2;
		
		if (count == 9)
			return Game_status.draw;
		
		return Game_status.not_thing;
	}
	
	/**
	 * This is a function that turn the game status into a string
	 * 
	 * @return the function return a string
	 * 
	 * */
	public String getInfo(){
		String boardinfo = "";
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boardinfo += Integer.toString(gameboard[i][j])+",";
			}
		}
		int start = 0;
		int end = boardinfo.length()-1;
		return boardinfo.substring(start, end);
	}
	
	/**
	 * This method allows a player to make a move if it is their turn and the specified space on the game board is empty. 
	 * The method takes in parameters indicating whether the moving player is player 1, as well as the row and column indices of the desired space. 
	 * 
	 * @param	isPlayer1	which player
	 * @param	row			index of row
	 * @param	col			index of column
	 * @return 	return true if move is this ok, return false if move is invaild 
	 * 
	 * */
	public synchronized boolean Move(boolean isPlayer1, int row, int col) {
		//check is ok to move
		boolean ok_to_move = (isPlayer1 && isPlayer1Turn) || (!isPlayer1 && !isPlayer1Turn);
		if (ok_to_move == false)
			return false;
		//Occupied
		if (gameboard[col][row] != 0)
			return false;
		
		if (isPlayer1)
			gameboard[col][row] = 1;
		else
			gameboard[col][row] = -1;
		//after each move, increase count to keep check of the game board occupation numbers
		count += 1;
		//switch turn
		isPlayer1Turn = !isPlayer1Turn;
		return true;
	}
	/**
	 * This method is for the game to restart
	 * reinitialize all the game data
	 */
	public void restart() {
		count = 0;
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				gameboard[i][j] = 0;
			}
		}
		isPlayer1Turn = true;
	}
}

