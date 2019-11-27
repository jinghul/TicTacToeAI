import java.util.*;

public class aiTicTacToe2 extends aiTicTacToe {

    public int player; //1 for player 1 and 2 for player 2
    private List<List<positionTicTacToe>> winningLines;
    private int lookAheadCounter;
    private int considered = 0;

    public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player) {
        //TODO: this is where you are going to implement your AI algorithm to win the game. The default is an AI randomly choose any available move.
        positionTicTacToe potentialMove = null;
        int bestScore;
        int score;
        int bestScorex = -1;
        int bestScorey = -1;
        int bestScorez = -1;
        int opponent = player == 1 ? 2 : 1;

        bestScore = -1000;
        considered = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++) {
                    potentialMove = new positionTicTacToe(i, j, k, player);
                    List<positionTicTacToe> copiedBoard = deepCopyATicTacToeBoard(board);
                    if (getStateOfPositionFromBoard(potentialMove, board) == 0) {
                        makeMove(potentialMove, player, copiedBoard);
                        score = lookAhead(opponent, 1000, -1000, copiedBoard);
                        lookAheadCounter = 0;
                        if (score >= bestScore) {
                            bestScore = score;
                            bestScorex = i;
                            bestScorey = j;
                            bestScorez = k;
                        }
                    }
                }
        positionTicTacToe myNextMove = new positionTicTacToe(bestScorex, bestScorey, bestScorez, player);
//		System.out.println("Player " + player + " considered " + considered + " moves.");
        return myNextMove;
    }

    private int heuristic(List<positionTicTacToe> copiedBoard) {
        int opponent = player == 1 ? 2 : 1;
        int playerScore = 0;
        int opponentScore = 0;
        positionTicTacToe p;
        int posState;
        int hValue = 0;

        for (int i = 0; i < winningLines.size(); i++) {
            int countPlayer = 0;
            int countOpponent = 0;
            for (int j = 0; j < 4; j++) {
                p = winningLines.get(i).get(j);
                posState = getStateOfPositionFromBoard(p, copiedBoard);
                if (posState == player)
                    countPlayer++;
                if (posState == opponent)
                    countOpponent++;
            }
            switch (countPlayer) {
                case 4:
                    playerScore += 1000;
                    break;
                case 3:
                    playerScore += 3;
                    break;
                case 2:
                    playerScore += 2;
                    break;
                case 1:
                    playerScore += 1;
                    break;
            }
            switch (countOpponent) {
                case 4:
                    opponentScore += 1000;
                    break;
                case 3:
                    opponentScore += 3;
                    break;
                case 2:
                    opponentScore += 2;
                    break;
                case 1:
                    opponentScore += 1;
                    break;
            }
        }
        hValue = playerScore - opponentScore;

        return hValue;
    }

    private List<List<positionTicTacToe>> initializeWinningLiness() {
        //create a list of winning line so that the game will "brute-force" check if a player satisfied any winning condition(s).
        List<List<positionTicTacToe>> winningLines = new ArrayList<List<positionTicTacToe>>();

        //48 straight winning lines
        //z axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(i, j, 0, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 1, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 2, -1));
                oneWinCondtion.add(new positionTicTacToe(i, j, 3, -1));
                winningLines.add(oneWinCondtion);
            }
        //y axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(i, 0, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 1, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 2, j, -1));
                oneWinCondtion.add(new positionTicTacToe(i, 3, j, -1));
                winningLines.add(oneWinCondtion);
            }
        //x axis winning lines
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
                oneWinCondtion.add(new positionTicTacToe(0, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(1, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(2, i, j, -1));
                oneWinCondtion.add(new positionTicTacToe(3, i, j, -1));
                winningLines.add(oneWinCondtion);
            }

        //12 main diagonal winning lines
        //xz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, i, 0, -1));
            oneWinCondtion.add(new positionTicTacToe(1, i, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(2, i, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(3, i, 3, -1));
            winningLines.add(oneWinCondtion);
        }
        //yz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(i, 0, 0, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 1, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 2, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 3, 3, -1));
            winningLines.add(oneWinCondtion);
        }
        //xy plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, 0, i, -1));
            oneWinCondtion.add(new positionTicTacToe(1, 1, i, -1));
            oneWinCondtion.add(new positionTicTacToe(2, 2, i, -1));
            oneWinCondtion.add(new positionTicTacToe(3, 3, i, -1));
            winningLines.add(oneWinCondtion);
        }

        //12 anti diagonal winning lines
        //xz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, i, 3, -1));
            oneWinCondtion.add(new positionTicTacToe(1, i, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(2, i, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(3, i, 0, -1));
            winningLines.add(oneWinCondtion);
        }
        //yz plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(i, 0, 3, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 1, 2, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 2, 1, -1));
            oneWinCondtion.add(new positionTicTacToe(i, 3, 0, -1));
            winningLines.add(oneWinCondtion);
        }
        //xy plane-4
        for (int i = 0; i < 4; i++) {
            List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
            oneWinCondtion.add(new positionTicTacToe(0, 3, i, -1));
            oneWinCondtion.add(new positionTicTacToe(1, 2, i, -1));
            oneWinCondtion.add(new positionTicTacToe(2, 1, i, -1));
            oneWinCondtion.add(new positionTicTacToe(3, 0, i, -1));
            winningLines.add(oneWinCondtion);
        }

        //4 additional diagonal winning lines
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

    public aiTicTacToe2(int setPlayer) {
        super(setPlayer);
        winningLines = initializeWinningLiness();
    }

    private List<positionTicTacToe> deepCopyATicTacToeBoard(List<positionTicTacToe> board) {
        List<positionTicTacToe> copiedBoard = new ArrayList<positionTicTacToe>();
        for (positionTicTacToe positionTicTacToe : board) {
            copiedBoard.add(new positionTicTacToe(positionTicTacToe.x, positionTicTacToe.y, positionTicTacToe.z, positionTicTacToe.state));
        }
        return copiedBoard;
    }

    private void makeMove(positionTicTacToe position, int player, List<positionTicTacToe> targetBoard) {
        for (int i = 0; i < targetBoard.size(); i++) {
            if (targetBoard.get(i).x == position.x && targetBoard.get(i).y == position.y && targetBoard.get(i).z == position.z) //if this is the position
            {
                if (targetBoard.get(i).state == 0) {
                    targetBoard.get(i).state = player;
                    return;
                } else {
                    System.out.println("Error: this is not a valid move.");
                }
            }

        }
    }

    private int lookAhead(int turn, int a, int b, List<positionTicTacToe> copiedBoard) {
        int opponent = turn == 1 ? 2 : 1;
        int alpha = a;
        int beta = b;
        int totalLooksAhead = 3;
        considered++;

        if (lookAheadCounter <= totalLooksAhead) {
            lookAheadCounter++;
            if (turn == player) {
                int hValue;
                positionTicTacToe myNextMove = new positionTicTacToe(0, 0, 0);

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                            myNextMove = new positionTicTacToe(i, j, k);
                            if (getStateOfPositionFromBoard(myNextMove, copiedBoard) == 0) {
                                makeMove(myNextMove, turn, copiedBoard);
                                if (isEnded(turn, copiedBoard)) {
                                    return 1000;
                                } else {
                                    hValue = lookAhead(opponent, alpha, beta, copiedBoard);
                                    if (hValue > alpha) {
                                        alpha = hValue;
                                    }
                                }

                                if (alpha >= beta)
                                    break;
                            }
                        }
                    }
                }

                return alpha;
            } else {
                int hValue;
                positionTicTacToe myNextMove = new positionTicTacToe(0, 0, 0);

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                            myNextMove = new positionTicTacToe(i, j, k);
                            if (getStateOfPositionFromBoard(myNextMove, copiedBoard) == 0) {
                                makeMove(myNextMove, opponent, copiedBoard);
                                if (isEnded(opponent, copiedBoard)) {
                                    return -1000;
                                } else {
                                    hValue = lookAhead(turn, alpha, beta, copiedBoard);
                                    if (hValue < beta) {
                                        beta = hValue;
                                    }
                                }

                                if (alpha >= beta)
                                    break;
                            }
                        }
                    }
                }

                return beta;
            }
        } else {
            return heuristic(copiedBoard);
        }
    }

    private boolean isEnded(int player, List<positionTicTacToe> board) {
        //test whether the current game is ended

        //brute-force
        boolean win = false;
        for (int i = 0; i < winningLines.size(); i++) {

            positionTicTacToe p0 = winningLines.get(i).get(0);
            positionTicTacToe p1 = winningLines.get(i).get(1);
            positionTicTacToe p2 = winningLines.get(i).get(2);
            positionTicTacToe p3 = winningLines.get(i).get(3);

            int state0 = getStateOfPositionFromBoard(p0, board);
            int state1 = getStateOfPositionFromBoard(p1, board);
            int state2 = getStateOfPositionFromBoard(p2, board);
            int state3 = getStateOfPositionFromBoard(p3, board);

            //if they have the same state (marked by same player) and they are not all marked.
            if (state0 == state1 && state1 == state2 && state2 == state3 && state0 == player) {
                //someone wins
                //print the satisified winning line (one of them if there are several)
                win = true;
            }
        }
        return win;
    }

}