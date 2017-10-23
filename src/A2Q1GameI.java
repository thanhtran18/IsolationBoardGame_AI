public interface A2Q1GameI {
    public enum Move { NONE, N, S, E, W };
    public char[][] board();
    public int score(char player);
    public int millisLeft();
    public int currentPlayer();
    public int players();
    public boolean canMove(Move m);
}

