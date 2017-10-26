public class RandomPlayer implements A2Q1AI {
    public A2Q1GameI.Move move(A2Q1GameI game) {
        // Percentage chance of timeout, just to see what happens!
        /*if (Math.random() < 0.05) {
            System.out.println("Will timeout in...");
            long last = game.millisLeft();
            while (true) {
                if (last - game.millisLeft() > 1000) {
                    last = game.millisLeft();
                    System.out.println(" " + last / 1000);
                }
            }
        }*/

        A2Q1GameI.Move move;
        do {
            move = A2Q1GameI.Move.values()[(int)(Math.random() * 4 + 1)];
            //System.out.println("Player number: " + game.currentPlayer()); //DELETE THESE TWO LINES
            //System.out.println("Random's Score: " + game.score('1'));
        } while (!game.canMove(move));
        return move;
    }

    public String toString() {
        return "Random player";
    }
}
