import java.util.*;

public class A2Q1ThanhTranAI implements A2Q1AI
{
    class ValueAndMove
    {
        private double value;
        private A2Q1GameI.Move move;

        public ValueAndMove(double value, A2Q1GameI.Move move)
        {
            this.value = value;
            this.move = move;
        }

        public ValueAndMove()
        {
            this.value = 0.0;
            this.move = A2Q1GameI.Move.NONE;
        }

        public void setMove(A2Q1GameI.Move move)
        {
            this.move = move;
        }

        public void setValue(double value)
        {
            this.value = value;
        }

        public double getValue()
        {
            return value;
        }

        public A2Q1GameI.Move getMove()
        {
            return move;
        }
    }

    public A2Q1GameI.Move move(A2Q1GameI game)
    {
        return alphaBetaPruning(game, -Double.MAX_VALUE, Double.MAX_VALUE, 10, game.currentPlayer()).getMove();
        //return alphaBetaPruning(game, Double.MIN_VALUE, Double.MAX_VALUE, 3, getMyStartNumber(game)).getMove();
    }
/*
    private A2Q1GameI.Move alphaBetaPruning(A2Q1GameI game, int depth)
    {
        A2Q1GameI.Move result;
        ArrayList<A2Q1GameI.Move> moves = generateMoves(game, game.currentPlayer());
        //System.out.println("current player " + game.currentPlayer());
        if (moves.size() == 0)
            return null;

1        result = maxValue(game, Double.MIN_VALUE, Double.MAX_VALUE, depth, game.currentPlayer(), moves.get(0)).getMove();

        return result;
    }*/

    private ValueAndMove alphaBetaPruning(A2Q1GameI game, double alpha, double beta, int depth, int playerNumber)
    {
        //ValueAndMove result = new ValueAndMove();
        //ArrayList<A2Q1GameI.Move> moves = generateMoves(game, playerNumber);
        //ArrayList<A2Q1GameI.Move> moves = generateMoves(game, game.currentPlayer());
        if (depth == 0)
            //return A2Q1GameI.Move.NONE;
        {
            ArrayList<ValueAndMove> values = new ArrayList<>();
            double max = -Double.MAX_VALUE;
            ValueAndMove maxMove = new ValueAndMove();
            for (A2Q1GameI.Move possMove: generateMoves(game, game.currentPlayer()))
            {
                double value = heuristicValue(game, possMove);
                ValueAndMove result = new ValueAndMove(value, possMove);

                values.add(result);
                if (value > max)
                {
                    max = value;
                    maxMove = result;
                }
            }
            return maxMove;
        }
        else if (game.millisLeft() <= 0 || generateMoves(game, game.currentPlayer()).size() == 0 ||
                (generateMoves(game, game.currentPlayer()).size() == 1 && generateMoves(game, game.currentPlayer()).get(0) == A2Q1GameI.Move.NONE))
            //return A2Q1GameI.Move.NONE;
            return new ValueAndMove(heuristicValue(game, A2Q1GameI.Move.NONE), A2Q1GameI.Move.NONE);


        if (playerNumber == game.currentPlayer()) //max value
        {
            ValueAndMove result = new ValueAndMove();
            double value = -Double.MAX_VALUE;
            for (A2Q1GameI.Move possMove : generateMoves(game, game.currentPlayer()))
            {
                if (playerNumber < game.players())
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1);
                    value = Math.max(value, result.getValue());
                    //value = Math.max(value, alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1).getValue());
                    //result = new ValueAndMove(value, possMove);
                }
                else
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, 1);
                    value = Math.max(value, result.getValue());
                    //value = Math.max(value, alphaBetaPruning(game, alpha, beta, depth - 1, 1).getValue());
                    //result = new ValueAndMove(value, possMove);
                }
                alpha = Math.max(alpha, value);
                if (beta <= alpha)
                    break;
            }
            return result;
        }
        else
        {
            ValueAndMove result = new ValueAndMove();
            double value = Double.MAX_VALUE;
            //for (A2Q1GameI.Move possMove : generateMoves(game, game.currentPlayer()))
            for (A2Q1GameI.Move possMove : generateMoves(game, playerNumber))
            {
                if (playerNumber < game.players())
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1);
                    value = Math.min(value, result.getValue());
                    //value = Math.min(value, alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1).getValue());
                    //result = new ValueAndMove(value, possMove);
                }
                else
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, 1);
                    value = Math.min(value, result.getValue());
                    //value = Math.min(value, alphaBetaPruning(game, alpha, beta, depth - 1, 1).getValue());
                    //result = new ValueAndMove(value, possMove);
                }
                beta = Math.min(beta, value);
                if (beta <= alpha)
                    break;
            }
            return result;
        }

    }

    private ArrayList<A2Q1GameI.Move> generateMovesForChild(A2Q1GameI game, A2Q1GameI.Move newMove)
    {
        int[] parentPos = getPositionOf(game, game.currentPlayer());
        int[] childPos = {parentPos[0], parentPos[1]};
        ArrayList<A2Q1GameI.Move> childrenOfParent = generateMoves(game, game.currentPlayer());
        ArrayList<A2Q1GameI.Move> childrenOfChild = new ArrayList<>();
        if (childrenOfParent.contains(newMove))
        {
            if (newMove == A2Q1GameI.Move.N) {
                childPos[0] = parentPos[0] - 1;
            }
            else if (newMove == A2Q1GameI.Move.S) {
                childPos[0] = parentPos[0] + 1;
            }
            else if (newMove == A2Q1GameI.Move.E) {
                childPos[1] = parentPos[1] + 1;
            }
            else if (newMove == A2Q1GameI.Move.W)
            {
                childPos[1] = parentPos[1] - 1;
            }
            for (A2Q1GameI.Move possMove : A2Q1GameI.Move.values())
            {
                if (canChildMove(game, possMove, childPos))
                    childrenOfChild.add(possMove);
            }
        }
        return childrenOfChild;
    }

    private ArrayList<A2Q1GameI.Move> generateOpponentChildMoves(A2Q1GameI game, int playerNumber, A2Q1GameI.Move newMove)
    {
        int[] parentPos = getPositionOf(game, playerNumber);
        int[] childPos = {parentPos[0], parentPos[1]};
        ArrayList<A2Q1GameI.Move> childrenOfParent = generateOpponentPossMoves(game, playerNumber);
        ArrayList<A2Q1GameI.Move> childrenOfChild = new ArrayList<>();
        if (childrenOfParent.contains(newMove))
        {
            if (newMove == A2Q1GameI.Move.N) {
                childPos[0] = parentPos[0] - 1;
            }
            else if (newMove == A2Q1GameI.Move.S) {
                childPos[0] = parentPos[0] + 1;
            }
            else if (newMove == A2Q1GameI.Move.E) {
                childPos[1] = parentPos[1] + 1;
            }
            else if (newMove == A2Q1GameI.Move.W)
            {
                childPos[1] = parentPos[1] - 1;
            }
            for (A2Q1GameI.Move possMove : A2Q1GameI.Move.values())
            {
                if (canChildMove(game, possMove, childPos))
                    childrenOfChild.add(possMove);
            }
        }
        return childrenOfChild;
    }

    private ArrayList<A2Q1GameI.Move> generateMoves(A2Q1GameI game, int playerNumber)
    {
        if (playerNumber == game.currentPlayer())
        {
            ArrayList<A2Q1GameI.Move> children = new ArrayList<>();
            for (A2Q1GameI.Move move : A2Q1GameI.Move.values())
            {
                if (game.canMove(move))
                    children.add(move);
            }
            return children;
        }
        else
            return generateOpponentPossMoves(game, playerNumber);
    }

    private double heuristicValue(A2Q1GameI game, A2Q1GameI.Move newMove)
    {

        int opponentNumber = 0;
        if (game.currentPlayer() == 1)
            opponentNumber = 2;
        else if (game.currentPlayer() == 2)
            opponentNumber = 1;
        ArrayList<A2Q1GameI.Move> myNewPossibleMoves = generateMovesForChild(game, newMove);
        ArrayList<A2Q1GameI.Move> opponentNewPossibleMoves = generateOpponentChildMoves(game, opponentNumber, newMove);
        int numOfMyPastMoves = game.score(getMyStartNumber(game));
        int numOfOpponentPastMoves = game.score((char) (opponentNumber + 48));


        if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() != 0)
            return -Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() != 0 && opponentNewPossibleMoves.size() == 0)
            return Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() == 0)
            return -10.0;
        if (percentageMovableCells(game) >= 0.2)
            return ((double) (myNewPossibleMoves.size() - opponentNewPossibleMoves.size()));
        return (Math.pow(myNewPossibleMoves.size(), 2) - Math.pow(opponentNewPossibleMoves.size(),2)
                + numOfMyPastMoves/numOfOpponentPastMoves); //myNewPossibleMoves.size()/opponentNewPossibleMoves.size() +

    /*
        if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() != 0)
            return -Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() != 0 && opponentNewPossibleMoves.size() == 0)
            return Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() == 0)
            return -10.0;
        else if (myNewPossibleMoves.size() >= opponentNewPossibleMoves.size())
        {
            double a = (Math.pow((double) (myNewPossibleMoves.size()/opponentNewPossibleMoves.size()), 2)) + (numOfOpponentPastMoves - opponentNewPossibleMoves.size());
            return a;
        }    //return (Math.pow((double) (myNewPossibleMoves.size() - opponentNewPossibleMoves.size()), 2)) + (numOfOpponentPastMoves - opponentNewPossibleMoves.size());
        else if (myNewPossibleMoves.size() < opponentNewPossibleMoves.size())
            return -((Math.pow((double) (myNewPossibleMoves.size()/opponentNewPossibleMoves.size()), 2)) + (numOfOpponentPastMoves - opponentNewPossibleMoves.size()));
            //return -((Math.pow((double) (myNewPossibleMoves.size() - opponentNewPossibleMoves.size()), 2)) + (numOfOpponentPastMoves - opponentNewPossibleMoves.size()));
        return 0.0;*/
        //return (Math.random() * 4 + 1);
    }

    private int getAvailableCellsAround(A2Q1GameI game, int playerNumber)
    {
        int count = 0;
        int halfRowSize = game.board().length/4;
        int halfColSize = game.board()[0].length/4;
        int[] playerPos = getPositionOf(game, playerNumber);
        int[] upLeft = {playerPos[0] - halfRowSize, playerPos[0] - halfColSize};
        int[] lowLeft = {playerPos[0] + halfRowSize, playerPos[0] - halfColSize};
        int[] upRight = {playerPos[0] - halfRowSize, playerPos[0] + halfColSize};
        int[] lowRight = {playerPos[0] + halfRowSize, playerPos[0] + halfColSize};
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[0].length; j++)
            {
                if (i <= upLeft[0] && i >= lowLeft[0] && j >= upLeft[1] && j <=  upRight[1] && game.board()[i][j] == ' ')
                    count++;
            }
        }
        return count;

    }

    private char getMyStartNumber(A2Q1GameI game)
    {
        return (char) (game.currentPlayer() + 48);
    }

    private int[] getPositionOf(A2Q1GameI game, int playerNumber)
    {
        int[] position = {-1, -1};
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[i].length; j++)
            {
                if (game.board()[i][j] - 48 == playerNumber)
                {
                    position[0] = i;
                    position[1] = j;
                }
            }
        }
        return position;
    }

    private double percentageMovableCells(A2Q1GameI game)
    {
        int count = 0;
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[i].length; j++)
            {
                if (game.board()[i][j] == ' ')
                    count++;
            }
        }
        return count/(game.board().length*game.board()[0].length);
    }

    private boolean canOpponentMove(A2Q1GameI game, A2Q1GameI.Move move, int playerNumber)
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

    private boolean canChildMove(A2Q1GameI game, A2Q1GameI.Move move, int[] childPos)
    {
        if (move == A2Q1GameI.Move.N) {
            return (childPos[0] >= 1) && (childPos[1] >= 0) && (game.board()[(childPos[0] - 1)][childPos[1]] == ' ');
        }
        if (move == A2Q1GameI.Move.S) {
            return (childPos[0] < game.board().length - 1) && (childPos[1] >= 0) && (game.board()[(childPos[0] + 1)][childPos[1]] == ' ');
        }
        if (move == A2Q1GameI.Move.E) {
            boolean a = (childPos[0] >= 0) && (childPos[1] < game.board()[childPos[0]].length - 1) && (game.board()[childPos[0]][(childPos[1] + 1)] == ' ');
            return a;
            //return (childPos[0] >= 0) && (childPos[1] < game.board()[childPos[0]].length - 1) && (game.board()[childPos[0]][(childPos[1] + 1)] == ' ');
        }
        if (move == A2Q1GameI.Move.W) {
            return (childPos[0] >= 0) && (childPos[1] >= 1) && (game.board()[childPos[0]][(childPos[1] - 1)] == ' ');
        }
        return true;
    }

    private ArrayList<A2Q1GameI.Move> generateOpponentPossMoves(A2Q1GameI game, int playerNumber)
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

