import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class A2Q1ThanhTranAI implements A2Q1AI
{
    public A2Q1GameI.Move move(A2Q1GameI game)
    {
        return alphaBetaPruning(game);
    }

    public A2Q1GameI.Move alphaBetaPruning(A2Q1GameI game)
    {

        return null;
    }

    public int maxValue(A2Q1GameI game, int alpha, int beta)
    {
        return 0;
    }

    public int minValue(A2Q1GameI game, int alpha, int beta)
    {
        return 0;
    }

    public ArrayList<A2Q1GameI.Move> generateMoves(A2Q1GameI game)
    {
        ArrayList<A2Q1GameI.Move> children = new ArrayList<>();
        for (A2Q1GameI.Move move: A2Q1GameI.Move.values())
        {
            if (game.canMove(move))
                children.add(move);
        }
        return children;
    }

    public double heuristicValue(A2Q1GameI game, int numOfPlayers)
    {
        ArrayList<A2Q1GameI.Move> myPossibleMoves = generateMoves(game);
        return 0.0;
    }

    private char getMyStartNumber(A2Q1GameI game)
    {
        return (char) (game.currentPlayer() + 48);
    }

    public int[] getPositionOf(A2Q1GameI game, int playerNumber)
    {
        int[] position = {-1, -1};
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[i].length; j++)
            {
                if (game.board()[i][j] == playerNumber)
                {
                    position[0] = i;
                    position[1] = j;
                }
            }
        }
        return position;
    }

    public int getNumOfPlayers(A2Q1GameI game)
    {
        ArrayList<Integer> possiblePlayer = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9));
        int numOfPlayers = 0;
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[i].length; j++)
            {
                for (int k : possiblePlayer)
                {
                    if (game.board()[i][j] == k)
                    {
                        numOfPlayers++;
                        possiblePlayer.remove(k);
                    }
                }
            }
        }
        return numOfPlayers;
    } //getNumOfPlayers

    public boolean canOpponentMove(A2Q1GameI game, A2Q1GameI.Move move, int playerNumber)
    {
        int[] opponentPos = getPositionOf(game, playerNumber);
        if (move == A2Q1GameI.Move.N) {
            return (opponentPos[0] >= 1) && (opponentPos[1] >= 0) && (game.board()[(opponentPos[0] - 1)][opponentPos[1]] == ' ');
        }
        if (move == A2Q1GameI.Move.S) {
            return (opponentPos[0] < game.board().length - 1) && (opponentPos[1] >= 0) && (game.board()[(opponentPos[0] + 1)][opponentPos[1]] == ' ');
        }
        if (move == A2Q1GameI.Move.E) {
            return (opponentPos[0] >= 0) && (opponentPos[1] < game.board()[opponentPos[0]].length - 1) && (game.board()[opponentPos[0]][(opponentPos[1] + 1)] == ' ');
        }
        if (move == A2Q1GameI.Move.W) {
            return (opponentPos[0] >= 0) && (opponentPos[1] >= 1) && (game.board()[opponentPos[0]][(opponentPos[1] - 1)] == ' ');
        }
        return true;
    }

    public ArrayList<A2Q1GameI.Move> generateOpponentPossMoves(A2Q1GameI game, int playerNumber)
    {
        ArrayList<A2Q1GameI.Move> children = new ArrayList<>();
        for (A2Q1GameI.Move currMove : A2Q1GameI.Move.values())
        {
            if (canOpponentMove(game, currMove, playerNumber))
                children.add(currMove);
        }
        return children;
    }

    public String toString()
    {
        return "Thanh Tran";
    }
}
