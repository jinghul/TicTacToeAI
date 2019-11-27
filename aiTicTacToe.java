import java.util.*;

public class aiTicTacToe {

    public static final int BOARD_SIZE = 64; // 4x4x4
    public static final int DEFAULT_DEPTH = 4; // max lookahead depth to explore
    public static positionTicTacToe[][] winningLines; // list of winning lines
    public static int[] moveOrdering; // list of possible wins for each block

    static {
        winningLines = initializeWinningLines(); 
        moveOrdering = mapWinningLines(winningLines);
    }

    public int player; // 1 for player 1 and 2 for player 2
    private int considered = 0; // count number of moves considered
    private int maxDepth = DEFAULT_DEPTH;
    private boolean verbose; // whether to print out moves

    /**
     * Public constructor that instantiated the class with the player
     * number and the max depth as well as verbose printing.
     * @param setPlayer
     * @param maxDepth
     * @param verbose
     */
    public aiTicTacToe(int setPlayer, int maxDepth, boolean verbose) {
        player = setPlayer;
        this.maxDepth = maxDepth;
        this.verbose = verbose;
    }

    /**
     * Public constructor that instantiated the class with the player
     * number and the verbose printing.
     * @param setPlayer
     * @param verbose
     */
    public aiTicTacToe(int setPlayer, boolean verbose) {
        this(setPlayer);
        this.verbose = verbose;
    }

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
     * Gets the list index given a position
     * @param position
     * @return the index
     */
    public static int getListIndex(positionTicTacToe position)
    {
        return position.x * 16 + position.y * 4 + position.z;
    }

    /**
     * A helper function to get state of a certain position in the Tic-Tac-Toe board
     * by given position TicTacToe.
     * @param position
     * @param board
     * @return state
     */
    public int getStateOfPositionFromBoard(positionTicTacToe position, List<positionTicTacToe> board) {
        int index = position.x * 16 + position.y * 4 + position.z; // getIndex()
        return board.get(index).state;
    }

    public int[] getStateOfPositionFromBoard(positionTicTacToe[] positions, List<positionTicTacToe> board)
    {   
        int[] states = new int[positions.length];
        for (int i = 0; i < positions.length; i++) 
        {
            int index = positions[i].x * 16 + positions[i].y * 4 + positions[i].z; // getIndex()
            states[i] = board.get(index).state;
        }
        return states;
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
		for(int i=0;i<BOARD_SIZE;i++)
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

        // start time
        long startTime = System.nanoTime();

        // deepcopy board state for modification
        List<positionTicTacToe> boardCopy = deepCopyATicTacToeBoard(board);

        // set up next move object
        positionTicTacToe nextMove = new positionTicTacToe(-1, -1, player);
        considered = 0;

        // run minimax
        int maxScore = maximize(nextMove, boardCopy, player, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        // end time
        long endTime = System.nanoTime(); 
        double timeTaken = (endTime - startTime) / 1000000000.0;
        if (verbose)
        {
            System.out.println(String.format("depth: %d | score: %d | time: %.2f| considered: %d", maxDepth, maxScore, timeTaken, considered));
        }

        // return best move
        return nextMove;
    }

    /**
     * Copy the content from the source position to the target position
     * @param source
     * @param target
     */
    private void copyMove(positionTicTacToe source, positionTicTacToe target)
    {
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
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
        
        // count considered moves
        considered++;

        if (depth == maxDepth) {
            return heuristic(board);
        }

        int maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < BOARD_SIZE; i++) {

            int positionIndex = moveOrdering[i];

            // break if this position is not empty
            if (!isEmptyPosition(board.get(positionIndex), board)) continue;
            
            // make move
            board.get(positionIndex).state = player;

            if (isEnded(board))
            {
                if (depth == 0) copyMove(board.get(positionIndex), nextMove);
                board.get(positionIndex).state = 0;
                return 1000000; // player win
            }
            
            // change according to the player
            int score = minimize(board, opponent(player), (depth + 1), alpha, beta);

            // update maxScore if score is larger
            if (score > maxScore) {
                maxScore = score;
                if (depth == 0) copyMove(board.get(positionIndex), nextMove);
            }

            // set alpha to the max of the seen scores
            alpha = Math.max(alpha, maxScore);

            // backtrack and reverse move
            board.get(positionIndex).state = 0;

            // break if path is worse than seen
            if (beta <= alpha)
                break;
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
        
        // count considered moves
        considered++;

        if (depth == maxDepth) {
            return heuristic(board);
        }

        int minScore = Integer.MAX_VALUE;
        for (int i = 0; i < BOARD_SIZE; i++) {

            int positionIndex = moveOrdering[i];

            // break if this position is not empty
            if (!isEmptyPosition(board.get(positionIndex), board)) continue;

            board.get(positionIndex).state = player;

            if (isEnded(board))
            {
                board.get(positionIndex).state = 0;
                return -1000000; // player loss
            }

            int score = maximize(null, board, opponent(player), (depth + 1), alpha, beta);

            // change according to the player
            minScore = Math.min(minScore, score);
            beta = Math.min(beta, minScore);

            // backtrack and reverse move
            board.get(positionIndex).state = 0;

            if (beta <= alpha)
                break;
        }
        return minScore;
    }

    /**
     * Get the heuristic state of the winning line.
     * If there are mixed pieces then this line is worthless and return -1.
     * @param line
     * @param board
     * @return if line is winnable number of pieces of a player else -1
     */
    private int getHeuristicStateFromLine(positionTicTacToe[] line, List<positionTicTacToe> board)
    {
        int playerCount = 0;
        int opponentCount = 0;

        for (int state : getStateOfPositionFromBoard(line, board))
        {
            if (state == 0) continue;
            else if (state == player) playerCount++;
            else opponentCount++;
        }

        if (playerCount > 0 && opponentCount > 0) return -1;
        else if (playerCount > 0) return playerCount;
        else return -opponentCount;
    }

    /**
     * The heuristic function to calculate a number representing the state of the board.
     * Calculates the number of pieces in each possible "open", winning row, where open 
     * means there is only one user's pieces in that row.
     * @param board
     * @param player
     * @return the heuristic number of the board
     */
    private int heuristic(List<positionTicTacToe> board) {
        int score = 0;
        for (positionTicTacToe[] winningLine : winningLines) {
            int lineState = getHeuristicStateFromLine(winningLine, board);

            if (lineState == -1) continue;

            switch (lineState) {
            case 4:
                return 1000000;
            case 3:
                score += 1000;
                break;
            case 2:
                score += 30;
                break;
            case 1:
                score += 1;
                break;
            case -1:
                score -= 1;
                break;
            case -2:
                score -= 30;
                break;
            case -3:
                score -= 1000;
                break;
            case -4:
                return -1000000;
            default:
                break;
            }
        }

        return score;
    }

    /**
     * Checks if the game is ended given the board state.
     * @param board
     * @return Boolean if the game is won or drawn.
     */
    public boolean isEnded(List<positionTicTacToe> board)
    {
        for (positionTicTacToe[] line : winningLines)
        {
            int marker = getStateOfPositionFromBoard(line[0], board);
            for (int i = 1; i < 4; i++) 
            {
                if (getStateOfPositionFromBoard(line[i], board) != marker) 
                {
                    marker = 0;
                    break;
                }
            }

            if (marker != 0) 
                return true;
        }
        return false;
    }

    /**
     * Maps each block on the board to their corresponding winning lines
     * @return the mapping
     */
    public static int[] mapWinningLines(positionTicTacToe[][] winningLines)
    {
        int[] possibleWins = new int[BOARD_SIZE];
        for (int i = 0; i < winningLines.length; i++)
        {
            positionTicTacToe[] line = winningLines[i];
            for (positionTicTacToe position : line)
            {
                int index = getListIndex(position);
                possibleWins[index]++;
            }
        }

        Integer[] objOrdering = new Integer[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) objOrdering[i] = i;
        Arrays.sort(objOrdering, (e1, e2) -> possibleWins[e2] - possibleWins[e1]);

        int[] ordering = new int[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) ordering[i] = objOrdering[i];

        return ordering;
    }

    /**
     * Create an array of winning line so that the game will "brute-force" check if a
     * player satisfied any winning condition(s).
     * @return An array of the 76 winning lines.
     */
    public static positionTicTacToe[][] initializeWinningLines() {
        positionTicTacToe[][] winningLines = new positionTicTacToe[76][4];

        // keep track of index for winningLines
        int count = 0;

        // 48 straight winning lines
        // z axis winning lines
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++) {
                positionTicTacToe[] line = new positionTicTacToe[4];
                line[0] = new positionTicTacToe(i, j, 0, -1);
                line[1] = new positionTicTacToe(i, j, 1, -1);
                line[2] = new positionTicTacToe(i, j, 2, -1);
                line[3] = new positionTicTacToe(i, j, 3, -1);
                winningLines[count++] = line;
            }
        }

        // y axis winning lines
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++) {
                positionTicTacToe[] line = new positionTicTacToe[4];
                line[0] = new positionTicTacToe(i, 0, j, -1);
                line[1] = new positionTicTacToe(i, 1, j, -1);
                line[2] = new positionTicTacToe(i, 2, j, -1);
                line[3] = new positionTicTacToe(i, 3, j, -1);
                winningLines[count++] = line;
            }
        }

        // x axis winning lines
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++) {
                positionTicTacToe[] line = new positionTicTacToe[4];
                line[0] = new positionTicTacToe(0, i, j, -1);
                line[1] = new positionTicTacToe(1, i, j, -1);
                line[2] = new positionTicTacToe(2, i, j, -1);
                line[3] = new positionTicTacToe(3, i, j, -1);
                winningLines[count++] = line;
            }
        }

        // 12 main diagonal winning lines
        // xz plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(0, i, 0, -1);
            line[1] = new positionTicTacToe(1, i, 1, -1);
            line[2] = new positionTicTacToe(2, i, 2, -1);
            line[3] = new positionTicTacToe(3, i, 3, -1);
            winningLines[count++] = line;
        }

        // yz plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(i, 0, 0, -1);
            line[1] = new positionTicTacToe(i, 1, 1, -1);
            line[2] = new positionTicTacToe(i, 2, 2, -1);
            line[3] = new positionTicTacToe(i, 3, 3, -1);
            winningLines[count++] = line;
        }

        // xy plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(0, 0, i, -1);
            line[1] = new positionTicTacToe(1, 1, i, -1);
            line[2] = new positionTicTacToe(2, 2, i, -1);
            line[3] = new positionTicTacToe(3, 3, i, -1);
            winningLines[count++] = line;
        }

        // 12 anti diagonal winning lines
        // xz plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(0, i, 3, -1);
            line[1] = new positionTicTacToe(1, i, 2, -1);
            line[2] = new positionTicTacToe(2, i, 1, -1);
            line[3] = new positionTicTacToe(3, i, 0, -1);
            winningLines[count++] = line;
        }

        // yz plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(i, 0, 3, -1);
            line[1] = new positionTicTacToe(i, 1, 2, -1);
            line[2] = new positionTicTacToe(i, 2, 1, -1);
            line[3] = new positionTicTacToe(i, 3, 0, -1);
            winningLines[count++] = line;
        }

        // xy plane-4
        for (int i = 0; i < 4; i++) {
            positionTicTacToe[] line = new positionTicTacToe[4];
            line[0] = new positionTicTacToe(0, 3, i, -1);
            line[1] = new positionTicTacToe(1, 2, i, -1);
            line[2] = new positionTicTacToe(2, 1, i, -1);
            line[3] = new positionTicTacToe(3, 0, i, -1);
            winningLines[count++] = line;
        }

        // 4 additional diagonal winning lines
        positionTicTacToe[] line1 = new positionTicTacToe[4];
        line1[0] = new positionTicTacToe(0, 0, 0, -1);
        line1[1] = new positionTicTacToe(1, 1, 1, -1);
        line1[2] = new positionTicTacToe(2, 2, 2, -1);
        line1[3] = new positionTicTacToe(3, 3, 3, -1);
        winningLines[count++] = line1;

        positionTicTacToe[] line2 = new positionTicTacToe[4];
        line2[0] = new positionTicTacToe(0, 0, 3, -1);
        line2[1] = new positionTicTacToe(1, 1, 2, -1);
        line2[2] = new positionTicTacToe(2, 2, 1, -1);
        line2[3] = new positionTicTacToe(3, 3, 0, -1);
        winningLines[count++] = line2;

        positionTicTacToe[] line3 = new positionTicTacToe[4];
        line3[0] = new positionTicTacToe(3, 0, 0, -1);
        line3[1] = new positionTicTacToe(2, 1, 1, -1);
        line3[2] = new positionTicTacToe(1, 2, 2, -1);
        line3[3] = new positionTicTacToe(0, 3, 3, -1);
        winningLines[count++] = line3;

        positionTicTacToe[] line4 = new positionTicTacToe[4];
        line4[0] = new positionTicTacToe(0, 3, 0, -1);
        line4[1] = new positionTicTacToe(1, 2, 1, -1);
        line4[2] = new positionTicTacToe(2, 1, 2, -1);
        line4[3] = new positionTicTacToe(3, 0, 3, -1);
        winningLines[count++] = line4;

        return winningLines;
    }
}
