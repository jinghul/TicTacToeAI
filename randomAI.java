import java.util.*;

public class randomAI extends aiTicTacToe
{
    public randomAI(int player) {
        super(player);
    }

    public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player) {
        positionTicTacToe myNextMove = new positionTicTacToe(0, 0, 0);

        do {
            Random rand = new Random();
            int x = rand.nextInt(4);
            int y = rand.nextInt(4);
            int z = rand.nextInt(4);
            myNextMove = new positionTicTacToe(x, y, z);
        } while (getStateOfPositionFromBoard(myNextMove, board) != 0);

        return myNextMove;
    }
}