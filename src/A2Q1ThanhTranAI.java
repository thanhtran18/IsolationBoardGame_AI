//-----------------------------------------
// NAME		    : CONG THANH TRAN
// STUDENT NUMBER	: 7802106
// COURSE		: COMP 3190 - Introduction to Artificial Intelligence
// INSTRUCTOR	: JOHN BRAICO
// ASSIGNMENT	: assignment #2
// QUESTION	    : question #1
//
// REMARKS: Implement the computer player (AI player) for the Isolation board game.
//
//-----------------------------------------

import java.util.*;

public class A2Q1ThanhTranAI implements A2Q1AI
{
    //this internal class is the object type will be returned by alpha-beta method, which contains the score and the move will be made
    class ValueAndMove
    {
        private double value;
        private A2Q1GameI.Move move;

        //------------------------------------------------------
        // ValueAndMove Constructor
        //
        // PURPOSE:	Initializes this object
        // PARAMETERS:
        //		double: the value returned by the algorithm
        //      Move:   the best move returned by the algorithm
        // Returns: None
        //------------------------------------------------------
        public ValueAndMove(double value, A2Q1GameI.Move move)
        {
            this.value = value;
            this.move = move;
        }

        //------------------------------------------------------
        // ValueAndMove Constructor
        //
        // PURPOSE:	Initializes this object
        // PARAMETERS:
        //		double: the value returned by the algorithm
        //      Move:   the best move returned by the algorithm
        // Returns: None
        //------------------------------------------------------
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

        //------------------------------------------------------
        // getValue
        //
        // PURPOSE:	get the the returned value
        // PARAMETERS: none
        // Returns:
        //		double: the value
        //------------------------------------------------------
        public double getValue()
        {
            return value;
        }

        //------------------------------------------------------
        // getMove
        //
        // PURPOSE:	get the best move chosen by the algorithm
        // PARAMETERS: none
        // Returns:
        //		Move: the move
        //------------------------------------------------------
        public A2Q1GameI.Move getMove()
        {
            return move;
        }
    } //class ValueAndMove

    //The move method implemented from the interface.
    //Call the alpha-beta method to get the "best" move chosen by the algorithm and pass it ready for execution.
    public A2Q1GameI.Move move(A2Q1GameI game)
    {
        if (game.players() == 2)    //2-player case
            return alphaBetaPruning(game, -Double.MAX_VALUE, Double.MAX_VALUE, 7, game.currentPlayer()).getMove();
        else    //more than 2 players
            return alphaBetaPruning(game, -Double.MAX_VALUE, Double.MAX_VALUE, 4, game.currentPlayer()).getMove();
    }

    //------------------------------------------------------
    // alphaBetaPruning
    //
    // PURPOSE:	get the best move using alpha-beta pruning algorithm combined with iterative deepening
    // PARAMETERS:
    //      A2Q2GameI: the given state of the whole game.
    //      double:    alpha, the max value
    //      double:    beta, the min value
    //      depth:     the initial depth
    //      int:       the number of the player being considered
    // Returns:
    //		ValueAndMove: the object contains both best score and best move.
    //------------------------------------------------------
    private ValueAndMove alphaBetaPruning(A2Q1GameI game, double alpha, double beta, int depth, int playerNumber)
    {
        if (depth == 0) //if reaches the depth limit, then chose the move with the best score in available moves.
        {
            ArrayList<ValueAndMove> values = new ArrayList<>();
            double max = -Double.MAX_VALUE;
            ValueAndMove maxMove = new ValueAndMove();
            //calculate score for each move, get the maximum one.
            for (A2Q1GameI.Move possMove: generateMoves(game, game.currentPlayer()))
            {
                double value = heuristicValue(game, possMove, game.players());
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
        //if timeout, or there is no possible move available, then return None as default move
        else if (game.millisLeft() <= 0 || generateMoves(game, game.currentPlayer()).size() == 0 ||
                (generateMoves(game, game.currentPlayer()).size() == 1
                        && generateMoves(game, game.currentPlayer()).get(0) == A2Q1GameI.Move.NONE))
            return new ValueAndMove(heuristicValue(game, A2Q1GameI.Move.NONE, game.players()), A2Q1GameI.Move.NONE);

        //apply alpha-beta pruning algorithm
        //this is max case
        if (playerNumber == game.currentPlayer()) //max value
        {
            ValueAndMove result = new ValueAndMove();
            double value = -Double.MAX_VALUE;
            for (A2Q1GameI.Move possMove : generateMoves(game, game.currentPlayer()))
            {
                if (playerNumber < game.players()) //get the correct number of the next player
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1);
                    value = Math.max(value, result.getValue());
                }
                else
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, 1);
                    value = Math.max(value, result.getValue());
                }
                alpha = Math.max(alpha, value);
                if (beta <= alpha)
                    break;
            }
            return result;
        }
        else    //min case
        {
            ValueAndMove result = new ValueAndMove();
            double value = Double.MAX_VALUE;
            for (A2Q1GameI.Move possMove : generateMoves(game, playerNumber))
            {
                if (playerNumber < game.players())
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, playerNumber + 1);
                    value = Math.min(value, result.getValue());
                }
                else
                {
                    result = alphaBetaPruning(game, alpha, beta, depth - 1, 1);
                    value = Math.min(value, result.getValue());
                }
                beta = Math.min(beta, value);
                if (beta <= alpha)
                    break;
            }
            return result;
        }
    } //alphaBetaPruning

    //------------------------------------------------------
    // heuristicValue
    // PURPOSE:	return the score for the move is being considered
    // PARAMETERS:
    //      A2Q1GameI: the whole game board.
    //      A2Q1GameI.Move: the possible move is being considered
    //      numOfPlayers: number of total players on the board.
    // Returns: the score of the move
    //------------------------------------------------------
    private double heuristicValue(A2Q1GameI game, A2Q1GameI.Move newMove, int numOfPlayers)
    {

        int opponentNumber = 0;
        if (game.currentPlayer() == game.players())
            opponentNumber = 1;
        else if (game.currentPlayer() < game.players())
            opponentNumber = game.currentPlayer() + 1;
        ArrayList<A2Q1GameI.Move> myNewPossibleMoves = generateMovesForChild(game, newMove);
        ArrayList<A2Q1GameI.Move> opponentNewPossibleMoves = generateOpponentChildMoves(game, opponentNumber, newMove);

        if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() != 0)
            return -Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() != 0 && opponentNewPossibleMoves.size() == 0)
            return Double.MAX_VALUE;
        else if (myNewPossibleMoves.size() == 0 && opponentNewPossibleMoves.size() == 0)
            return -10.0;
        double sum = 0.0;
        for (A2Q1GameI.Move move : myNewPossibleMoves)
            sum += centrality(game, move);

        int[] playerPos = getPositionOf(game, game.currentPlayer());
        int[] childPos = getChildPosition(game, playerPos, newMove);

        //case 1: the beginnign stage, when more than 87.5% of the tiles on the board are movable, priority would be
        // trying to move to the centre of the board
        if (percentageMovableCells(game) >= 0.875)
            return Math.pow(centrality(game, newMove), 4.0);

        //case 2: the stage after the beginning stage, the result will be a combination of different heuristic.
        //        Please refer to the README file for more explanation
        return Math.pow((double) getAvailableTilesAround(game, childPos), 4.0) + sum + commonMoves(game, 2)
                + Math.pow(centrality(game, newMove), 2);
    } //heuristicValue

    //------------------------------------------------------
    // centrality
    // PURPOSE:	this heuristic shows how "close" the current position is to the centre of the board using Manhattan distance
    // PARAMETERS:
    //      A2Q1GameI: the whole game board.
    //      A2Q1GameI.Move: the possible move is being considered
    // Returns: the Manhattan distance to the centre of the board
    //------------------------------------------------------
    private double centrality(A2Q1GameI game, A2Q1GameI.Move newMove)
    {
        int[] playerPos = getPositionOf(game, game.currentPlayer());
        int[] childPos = getChildPosition(game, playerPos, newMove);
        int[] centre = new int[2];
        centre[0] = (int)Math.ceil(game.board().length/2);
        centre[1] = (int)Math.ceil(game.board()[0].length/2);
        return (double) Math.abs(centre[0] - childPos[0]) + Math.abs(centre[1] - childPos[1]);
    }

    //------------------------------------------------------
    // commonMoves
    // PURPOSE:	this heuristic shows number of uncommon moves between the AI player and its opponent
    // PARAMETERS:
    //      A2Q1GameI: the whole game board.
    //      int: the opponent number
    // Returns: the number of uncommon moves
    //------------------------------------------------------
    private int commonMoves(A2Q1GameI game, int opponentNumber)
    {
        ArrayList<A2Q1GameI.Move> myPossMoves = generateMoves(game, game.currentPlayer());
        ArrayList<A2Q1GameI.Move> opponentPossMoves = generateOpponentPossMoves(game, opponentNumber);
        ArrayList<A2Q1GameI.Move> commonMoves = new ArrayList<>(myPossMoves);
        commonMoves.retainAll(opponentPossMoves);
        return (5 - commonMoves.size());
    }

    //------------------------------------------------------
    // getAvailableTilesAround
    // PURPOSE:	this heuristic calculate number of movable tiles around the given position on the board
    // PARAMETERS:
    //      A2Q1GameI: the whole game board.
    //      int[]:     the given position on the board.
    // Returns: the number of movable tiles around
    //------------------------------------------------------
    private int getAvailableTilesAround(A2Q1GameI game, int[] position)
    {
        //make up an internal square inside the board with the width of half the size of the whole board
        int count = 0;
        int halfRowSize = game.board().length/4;
        int halfColSize = game.board()[0].length/4;

        //calculate position of the four angles of the internal square
        int[] upLeft = {position[0] - halfRowSize, position[1] - halfColSize};
        int[] lowLeft = {position[0] + halfRowSize, position[1] - halfColSize};
        int[] upRight = {position[0] - halfRowSize, position[1] + halfColSize};
        int[] lowRight = {position[0] + halfRowSize, position[1] + halfColSize};

        //update number of movable tiles inside the made up square
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[0].length; j++)
            {
                if (i >= upLeft[0] && i <= lowLeft[0] && j >= upLeft[1] && j <=  upRight[1] && game.board()[i][j] == ' ')
                    count++;
            }
        }
        return count;
    }

    //------------------------------------------------------
    // getChildPosition
    //
    // PURPOSE:	get the position (coordinate) of the child tile.
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      int[]:     position of the current player or parent
    //      newMove:   the move will be considered
    // Returns: a list of all children
    //------------------------------------------------------
    private int[] getChildPosition(A2Q1GameI game, int[] parentPos, A2Q1GameI.Move newMove)
    {
        int[] childPos = {parentPos[0], parentPos[1]};
        if (newMove == A2Q1GameI.Move.N)
            childPos[0] = parentPos[0] - 1;
        else if (newMove == A2Q1GameI.Move.S)
            childPos[0] = parentPos[0] + 1;
        else if (newMove == A2Q1GameI.Move.E)
            childPos[1] = parentPos[1] + 1;
        else if (newMove == A2Q1GameI.Move.W)
            childPos[1] = parentPos[1] - 1;
        return childPos;
    }

    //------------------------------------------------------
    // generateMovesForChild
    //
    // PURPOSE:	generate all possible children for a child of the current position of the player
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      newMove:   the move will be considered (children of the child)
    // Returns: a list of all children of child moves
    //------------------------------------------------------
    private ArrayList<A2Q1GameI.Move> generateMovesForChild(A2Q1GameI game, A2Q1GameI.Move newMove)
    {
        int[] parentPos = getPositionOf(game, game.currentPlayer());
        int[] childPos = {parentPos[0], parentPos[1]};
        ArrayList<A2Q1GameI.Move> childrenOfParent = generateMoves(game, game.currentPlayer());
        ArrayList<A2Q1GameI.Move> childrenOfChild = new ArrayList<>();

        if (childrenOfParent.contains(newMove))
        {
            childPos = getChildPosition(game, parentPos, newMove);
            for (A2Q1GameI.Move possMove : A2Q1GameI.Move.values())
            {
                if (canChildMove(game, possMove, childPos))
                    childrenOfChild.add(possMove);
            }
        }
        return childrenOfChild;
    }

    //------------------------------------------------------
    // generateOpponentChildMoves
    //
    // PURPOSE:	generate all possible children for a child of the opponent
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      int:       the opponent number
    //      newMove:   the move will be considered (children of the child)
    // Returns: a list of all children of child moves
    //------------------------------------------------------
    private ArrayList<A2Q1GameI.Move> generateOpponentChildMoves(A2Q1GameI game, int playerNumber, A2Q1GameI.Move newMove)
    {
        int[] parentPos = getPositionOf(game, playerNumber);
        int[] childPos = {parentPos[0], parentPos[1]};
        ArrayList<A2Q1GameI.Move> childrenOfParent = generateOpponentPossMoves(game, playerNumber);
        ArrayList<A2Q1GameI.Move> childrenOfChild = new ArrayList<>();

        if (childrenOfParent.contains(newMove))
        {
            childPos = getChildPosition(game, parentPos, newMove);
            for (A2Q1GameI.Move possMove : A2Q1GameI.Move.values())
            {
                if (canChildMove(game, possMove, childPos))
                    childrenOfChild.add(possMove);
            }
        }
        return childrenOfChild;
    }

    //------------------------------------------------------
    // generateMoves
    //
    // PURPOSE:	generate all possible children for the current position of the player
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      playerNumber: the player is being processed
    // Returns: a list of all children of child moves
    //------------------------------------------------------
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

    //------------------------------------------------------
    // getPositionOf
    //
    // PURPOSE:	get the position (coordinate) of the a player on the board
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      int:       the player number that we want to know its position
    // Returns: the position of that player
    //------------------------------------------------------
    private int[] getPositionOf(A2Q1GameI game, int playerNumber)
    {
        int[] position = {-1, -1};
        //traverse through the board
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

    //------------------------------------------------------
    // percentageMovableCells
    //
    // PURPOSE:	get the ratio between the number of movable tiles and total number of tiles on the board
    // PARAMETERS:
    //      A2Q1GameI: the game board
    // Returns: the percentage
    //------------------------------------------------------
    private double percentageMovableCells(A2Q1GameI game)
    {
        int count = 0;
        //traverse throught the baord
        for (int i = 0; i < game.board().length; i++)
        {
            for (int j = 0; j < game.board()[i].length; j++)
            {
                if (game.board()[i][j] == ' ')
                    count++;
            }
        }

        return ((double)count/(double)(game.board().length*game.board()[0].length));
    }

    //------------------------------------------------------
    // canOpponentNumber
    //
    // PURPOSE:	check if the opponent can execute a specific move
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      A2Q1GameI.Move: the desired move that will be checked
    //      int:    the opponent number
    // Returns: true if the opponent can move that way, false otherwise.
    //------------------------------------------------------
    private boolean canOpponentMove(A2Q1GameI game, A2Q1GameI.Move move, int playerNumber)
    {
        int[] opponentPos = getPositionOf(game, playerNumber);
        if (move == A2Q1GameI.Move.N)
            return (opponentPos[0] >= 1) && (opponentPos[1] >= 0) && (game.board()[(opponentPos[0] - 1)][opponentPos[1]] == ' ');
        if (move == A2Q1GameI.Move.S)
            return (opponentPos[0] < game.board().length - 1) && (opponentPos[1] >= 0) && (game.board()[(opponentPos[0] + 1)][opponentPos[1]] == ' ');
        if (move == A2Q1GameI.Move.E)
            return (opponentPos[0] >= 0) && (opponentPos[1] < game.board()[opponentPos[0]].length - 1) && (game.board()[opponentPos[0]][(opponentPos[1] + 1)] == ' ');
        if (move == A2Q1GameI.Move.W)
            return (opponentPos[0] >= 0) && (opponentPos[1] >= 1) && (game.board()[opponentPos[0]][(opponentPos[1] - 1)] == ' ');
        return true;
    }

    //------------------------------------------------------
    // canChildMove
    //
    // PURPOSE:	check if the child can keep moving to different direction
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      A2Q1GameI.Move: the desired move that will be checked
    //      int[]: position of the child tile on the board.
    // Returns: true if the child can move that way, false otherwise.
    //------------------------------------------------------
    private boolean canChildMove(A2Q1GameI game, A2Q1GameI.Move move, int[] childPos)
    {
        if (move == A2Q1GameI.Move.N)
            return (childPos[0] >= 1) && (childPos[1] >= 0) && (game.board()[(childPos[0] - 1)][childPos[1]] == ' ');
        if (move == A2Q1GameI.Move.S)
            return (childPos[0] < game.board().length - 1) && (childPos[1] >= 0) && (game.board()[(childPos[0] + 1)][childPos[1]] == ' ');
        if (move == A2Q1GameI.Move.E)
            return (childPos[0] >= 0) && (childPos[1] < game.board()[childPos[0]].length - 1) && (game.board()[childPos[0]][(childPos[1] + 1)] == ' ');
        if (move == A2Q1GameI.Move.W)
            return (childPos[0] >= 0) && (childPos[1] >= 1) && (game.board()[childPos[0]][(childPos[1] - 1)] == ' ');
        return true;
    }

    //------------------------------------------------------
    // generateOpponentMoves
    //
    // PURPOSE:	generate all possible children of an opponent
    // PARAMETERS:
    //      A2Q1GameI: the game board
    //      int: the opponent number
    // Returns: a list of all children of the opponent
    //------------------------------------------------------
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

    //------------------------------------------------------
    // toString
    //
    // PURPOSE:	get the name of the author (or the AI player)
    // PARAMETERS: none
    // Returns: the name.
    //------------------------------------------------------
    public String toString()
    {
        return "Thanh Tran";
    }

} //class

