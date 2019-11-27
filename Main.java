import java.util.Random;

class Main {

    public static int NUM_TRIALS = 100;

    public static void main(String[] args) {
        test(-1, NUM_TRIALS);
        // run();
    }

    public static void run()
    {
        runTicTacToe rttt = new runTicTacToe();
        rttt.run();
    }

    public static void test(int startingPlayer, int numTrials)
    {   
        Random random = new Random();

        int ai1wins = 0;
        int ai2wins = 0;
        int draws = 0;

        for (int i = 0; i < numTrials; i++) {
            aiTicTacToe ai1 = new aiTicTacToe(1, 4, false);
            aiTicTacToe ai2 = new aiTicTacToe2(2);

            int turn = startingPlayer;
            if (turn == -1) turn = random.nextInt(2) + 1;

            runTicTacToe rttt = new runTicTacToe(ai1, ai2);

            // start time
            long startTime = System.nanoTime();

            int result = rttt.run(turn, false);

            // end time
            long endTime = System.nanoTime(); 
            double timeTaken = (endTime - startTime) / 1000000000.0;

            System.out.print(String.format("game time: %.2fs", timeTaken));
            if (result == 1) System.out.println(" | player 1 wins");
            else if (result == 2) System.out.println(" | player 2 wins");
            else if (result == 3) System.out.println(" | draw");

            if (result == -1) return;
            else if (result == 1) ai1wins++;
            else if (result == 2) ai2wins++;
            else if (result == 3) draws++;
        }

        System.out.println("Test Results\n----------------");
        System.out.println("AI 1 Wins: " + ai1wins);
        System.out.println("AI 2 Wins: " + ai2wins);
        System.out.println("Draws: " + draws);
    }


}