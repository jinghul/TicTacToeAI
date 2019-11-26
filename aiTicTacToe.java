import java.util.*;

public class aiTicTacToe {

    public static final int maxDepth = 1; // max lookahead depth to explore

    // TODO: convert to array?
    public static List<List<positionTicTacToe>> winningLines = initializeWinningLines(); // list of winning lines
    public static List<List<Integer>> possibleWins = mapWinningLines(); // list of possible wins for each block

    public int player; // 1 for player 1 and 2 for player 2

    /**
     * Public constructor that instantiated the class with the player number.
     * @param setPlayer
     */
    public aiTicTacToe(int setPlayer) {
        player = setPlayer;
    }

    /**
     * Returns a boolean representing whether the requested position is an empty board position.
     * @param position
     * @param board
     * @return boolean
     */
    private boolean isEmptyPosition(positionTicTacToe position, List<positionTicTacToe> board) {
        return getStateOfPositionFromBoard(position, board) == 0;
    }

    /**
     * A helper function to get state of a certain position in the Tic-Tac-Toe board
     * by given position TicTacToe.
     * @param position
     * @param board
     * @return state
     */
    private int getStateOfPositionFromBoard(positionTicTacToe position, List<positionTicTacToe> board) {
        int index = position.x * 16 + position.y * 4 + position.z;
        return board.get(index).state;
    }

    /**
     * Returns the opponent number of the given player.
     * @param player
     * @return opponent number
     */
    public int opponent(int player) {
        return player == 1 ? 2 : 1;
    }

    /**
     * Deep copies the TicTacToe board for modification. Returns the deepcopy.
     * @param board
     * @return copied board
     */
    private List<positionTicTacToe> deepCopyATicTacToeBoard(List<positionTicTacToe> board)
	{
		//deep copy of game boards
		List<positionTicTacToe> copiedBoard = new ArrayList<positionTicTacToe>();
		for(int i=0;i<board.size();i++)
		{
			copiedBoard.add(new positionTicTacToe(board.get(i).x,board.get(i).y,board.get(i).z, board.get(i).state));
		}
		return copiedBoard;
	}

    /**
     * Encapsulated function to call for making a TicTacToe move.
     * Runs minimax and returns the best move (position) given the lookahead depth.
     * @param board
     * @param player
     * @return next move to make
     */
    public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player) {
        // deepcopy board state for modification
        List<positionTicTacToe> boardCopy = deepCopyATicTacToeBoard(board);

        // set up next move object
        positionTicTacToe nextMove = new positionTicTacToe(0, 0, 0);

        // run minimax
        int maxScore = maximize(nextMove, boardCopy, player, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("score: " + maxScore);

        // return best move
        return nextMove;
    }

    /**
     * The maximizer portion of the minimax algorithm.
     * @param nextMove object to store next move
     * @param board
     * @param player
     * @param depth
     * @param alpha
     * @param beta
     * @return int representing best board state that can be reached.
     */
    public int maximize(positionTicTacToe nextMove, List<positionTicTacToe> board, int player,
            int depth, int alpha, int beta) {

        if (depth == maxDepth) {
            return heuristic(board, player);
        }

        // System.out.println(String.format("maximize - player %d : depth %d | a %d | b %d", player, depth, alpha, beta));

        int maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < board.size(); i++) {
            if (isEmptyPosition(board.get(i), board)) {
                
                // make move
                board.get(i).state = player;
                
                // change according to the player
                int score = minimize(board, opponent(player), (depth + 1), alpha, beta);

                if (score >= maxScore) {
                    maxScore = score;
                    if (depth == 0)
                    {
                        positionTicTacToe move = board.get(i);
                        nextMove.x = move.x;
                        nextMove.y = move.y;
                        nextMove.z = move.z;
                    }
                }

                alpha = Math.max(alpha, maxScore);
                if (beta <= alpha)
                    break;

                // backtrack and reverse move
                board.get(i).state = 0;
            }
        }
        return maxScore;
    }

    /**
     * The minimizer portion of the minimax algorithm.
     * @param board
     * @param player
     * @param depth
     * @param alpha
     * @param beta
     * @return int representing the minimum board state
     */
    public int minimize(List<positionTicTacToe> board, int player,
            int depth, int alpha, int beta) {

        if (depth == maxDepth) {
            return heuristic(board, player);
        }

        int minScore = Integer.MAX_VALUE;
        for (int i = 0; i < board.size(); i++) {
            if (isEmptyPosition(board.get(i), board)) {
                board.get(i).state = player;

                // change according to the player
                int score = maximize(null, board, opponent(player), (depth + 1), alpha, beta);
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, minScore);
                if (beta <= alpha)
                    break;

                // backtrack and reverse move
                board.get(i).state = 0;
            }
        }
        return minScore;
    }

    /**
     * The heuristic function to calculate a number representing the state of the board.
     * Calculates the number of pieces in each possible "open", winning row, where open 
     * means there is only one user's pieces in that row.
     * @param board
     * @param player
     * @return the heuristic number of the board
     */
    private int heuristic(List<positionTicTacToe> board, int player) {
        int opponent = opponent(player);
        int playerScore = 0;
        int opponentScore = 0;

        for (List<positionTicTacToe> winningLine : winningLines) {
            List<Integer> states = new ArrayList<>();
            for (positionTicTacToe pos : winningLine) {
                states.add(getStateOfPositionFromBoard(pos, board));
            }

            int playerSpots = (int) states.stream().filter(state -> state == player).count();
            switch (playerSpots) {
            case 4:
                playerScore += 10000;
                break;
            case 3:
                playerScore += 100;
                break;
            case 2:
                playerScore += 10;
                break;
            case 1:
                playerScore += 1;
                break;
            }

            int otherSpots = (int) states.stream().filter(state -> state == opponent).count();
            switch (otherSpots) {
            case 4:
                opponentScore += 10000;
                break;
            case 3:
                opponentScore += 100;
                break;
            case 2:
                opponentScore += 10;
                break;
            case 1:
                opponentScore += 1;
                break;
            }
        }

        return playerScore - opponentScore;
    }

    /**
     * Create a list of winning line so that the game will "brute-force" check if a
     * player satisfied any winning condition(s).
     * @return A list of the 76 winning lines.
     */
    public static List<List<positionTicTacToe>> initializeWinningLines() {
        List<List<positionTicTacToe>> winningLines = new ArrayList<List<positionTicTacToe>>();

        // 48 straight winning lines
        // z axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(i, j, 0, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 1, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 2, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 3, -1));
                winningLines.add(oneWinCondtion);
            }
        // y axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(i, 0, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 1, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 2, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 3, j, -1));
                winningLines.add(oneWinCondtion);
            }
        // x axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(0, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(1, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(2, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(3, i, j, -1));
                winningLines.add(oneWinCondtion);
            }

        // 12 main diagonal winning lines
        // xz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, i, 0, -1));
            oneWinCondtion.add(new positionTicTacToe(1, i, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(2, i, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(3, i, 3, -1));
            winningLines.add(oneWinCondtion);
        }
        // yz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(i, 0, 0, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 1, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 2, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 3, 3, -1));
            winningLines.add(oneWinCondtion);
        }
        // xy plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, 0, i, -1));
            oneWinCondtion.add(new positionTicTacToe(1, 1, i, -1));
            oneWinCondtion.add(new positionTicTacToe(2, 2, i, -1));
            oneWinCondtion.add(new positionTicTacToe(3, 3, i, -1));
            winningLines.add(oneWinCondtion);
        }

        // 12 anti diagonal winning lines
        // xz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, i, 3, -1));
            oneWinCondtion.add(new positionTicTacToe(1, i, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(2, i, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(3, i, 0, -1));
            winningLines.add(oneWinCondtion);
        }
        // yz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(i, 0, 3, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 1, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 2, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 3, 0, -1));
            winningLines.add(oneWinCondtion);
        }
        // xy plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, 3, i, -1));
            oneWinCondtion.add(new positionTicTacToe(1, 2, i, -1));
            oneWinCondtion.add(new positionTicTacToe(2, 1, i, -1));
            oneWinCondtion.add(new positionTicTacToe(3, 0, i, -1));
            winningLines.add(oneWinCondtion);
        }

        // 4 additional diagonal winning lines
        List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(0, 0, 0, -1));
        oneWinCondtion.add(new positionTicTacToe(1, 1, 1, -1));
        oneWinCondtion.add(new positionTicTacToe(2, 2, 2, -1));
        oneWinCondtion.add(new positionTicTacToe(3, 3, 3, -1));
        winningLines.add(oneWinCondtion);

        oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(0, 0, 3, -1));
        oneWinCondtion.add(new positionTicTacToe(1, 1, 2, -1));
        oneWinCondtion.add(new positionTicTacToe(2, 2, 1, -1));
        oneWinCondtion.add(new positionTicTacToe(3, 3, 0, -1));
        winningLines.add(oneWinCondtion);

        oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(3, 0, 0, -1));
        oneWinCondtion.add(new positionTicTacToe(2, 1, 1, -1));
        oneWinCondtion.add(new positionTicTacToe(1, 2, 2, -1));
        oneWinCondtion.add(new positionTicTacToe(0, 3, 3, -1));
        winningLines.add(oneWinCondtion);

        oneWinCondtion = new ArrayList<positionTicTacToe>();
        oneWinCondtion.add(new positionTicTacToe(0, 3, 0, -1));
        oneWinCondtion.add(new positionTicTacToe(1, 2, 1, -1));
        oneWinCondtion.add(new positionTicTacToe(2, 1, 2, -1));
        oneWinCondtion.add(new positionTicTacToe(3, 0, 3, -1));
        winningLines.add(oneWinCondtion);

        return winningLines;

    }
}
