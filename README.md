# IsolationBoardGame_AI
- The algorithm used for this question is alpha-beta pruning algorithm combined with iterative deepening technique. The quality of the heuristic value (i.e. the score for each move) is determined using multiple heuristic with two main cases: (Please refer to the calculation of these heuristic functions at after the descriptions of two cases, or refer to the actual implementation).

*** Case 1: 
    ------
    The beginning of the game (i.e. more than 87.5% (or 7/8) of the tiles of the board have not been occupied).
    In this case, I just want to make sure my AI player get closer to the central area of the board, where there will likely have more opportunities to proceed further in the game. The heuristic function is: (centrality(new position))^4 (explanation about this centrality heuristic is below).

*** Case 2:
    ------
    In other cases (any cases that comes after the beginning stage of the game), the score of a move is calculated in a more sophisticated way, which is a combination of multiple heuristic functions:
    
    - The top priority is to move to a new position that has the highest number of unoccupied tiles around it (i.e. there will likely be more chances to move after that). For example, if position A has 8 unoccupied square around it, and B has 10, then B will be more valuable in terms of this heuristic. In contribution to the total score for the game, this value will be powered to the fourth (highest weight).
    
    - The next priority is to get to the central area of the board (similar to the case at the beginning of the game), since chances are there will also be more possible moves for the player than at the edge or corner. In contribution to the total score for the game, this value will be squared (less important than the top priority element).
    
    - Another heuristic function is used how central the new position can be (i.e. will the children of the new possible position be central too?). The idea is the same, there will probably more chances in the central area of the game, but this goes one more step further to the possible move.
    
    - Last heuristic function is the number of uncommon moves between the players: This will get number of uncommon moves between the two players, the higher the better to avoid tie game. However, this heuristic is not really important.
    
    ==> The total score of each move will be the sum of all components above (different weight for each component based on the level of priority).

*** How to calculate those heuristic functions above?
    ------------------------------------------------
    - Available squares around the player heuristic: calculated by first making up an "around" area for the current player. The area will have the size of half the size of the actual game board. With this area, all we need to do now is traverse through this area and count the number of unoccupied squares.
    
    - Centrality: First calculate the position of the centre tile. Then measure the "centrality" of each possible new position by calculating the Manhattan distance between the centre tile and the new position of the player: absolute value of (xA - xB) + absolute value of (yA - yB) where A, B are the two positions.
    
    - Will the children of the new possible position be central too?: the same method but instead applying it with the new possible move, we apply it with each child of the new possible move, and add them up together after.
    
    - Uncommon moves between players: generate all possible moves for both players (the current player and the opponent), then get the common moves of them, then minus 5 by this number (5 - common moves, since there are 5 moves available in total).
