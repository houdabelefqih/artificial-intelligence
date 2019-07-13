/*
* HOUDA BELEFQIH 1001511875
* 5360-002 ARTIFICIAL INTELLIGENCE I
* ASSIGNMENT 4 : GAME PLAYING ALGORITHMS
* SPRING 2018
*/

/**
 * This is the AiPlayer class.  It simulates a minimax player for the max
 * connect four game.
 * The constructor essentially does nothing. 
 * 
 * @author james spargo
 *
 */
import static java.lang.Integer.*;
import java.util.*;

public class AiPlayer 
{
    /**
     * The constructor essentially does nothing except instantiate an
     * AiPlayer object.
     *
     */
    public AiPlayer() 
    {
	// nothing to do here
    }

    /**
     * This method plays a piece according to the minmax algorithm with alpha beta prunning
     * It returns the move to play for the max player, ie in which column to play the piece
     * Uses an evaluation function for non terminal nodes
     */
    public int findBestPlay(GameBoard currentGame, int depth ) 
    {
        int maxPlayer = currentGame.getCurrentTurn();
        int minPlayer = maxPlayer % 2 + 1;
        int alpha = -9999999;
        int beta = 9999999;
                
        int max_score = -9999999;
        int max_index =0;
        
        
        //Getting the possible valid moves for the maxplayer
        List<Integer> moves = new ArrayList<Integer>();
        
        for (int i=0; i<7; i++)
        {
            if(currentGame.isValidPlay(i))
                {
                    moves.add(i);
                    
                    
                }       
        }
        
                   
        //Call the MIn-value function function for each possible move
            for (Integer column: moves)
            {
                
                if(currentGame.playPiece(column))
                {
                    int maximize = minValue(currentGame, depth, maxPlayer, alpha, beta);
                    
                    
                    if (max_score <= maximize)
                    {
                        max_score = maximize;
                        max_index = column;
                    }
                    

                  //undo last move
                  currentGame.removePiece(column);
                }
            }
   
        return max_index;
    }
    
 
 int maxValue(GameBoard currentGame, int depth, int maxPlayer, int alpha, int beta)
   {
       int bestVal = -9999999;
       int value;

    if (currentGame.getPieceCount()==42)
            {return (currentGame.getScore(maxPlayer) - currentGame.getScore((maxPlayer % 2)+1));}
    
    if (depth ==0)
    {
        return (currentGame.evaluate_board(maxPlayer) - currentGame.evaluate_board((maxPlayer % 2)+1));
    }
    
     
        //for each child of node
        for (int i=0; i<7; i++)
        {  
           if(currentGame.playPiece(i))
            {
                value = minValue(currentGame, depth - 1, maxPlayer, alpha, beta);
                bestVal = Math.max( bestVal, value); 
                
                //Undo last piece played
                 currentGame.removePiece(i);
                
                //alpha beta pruning
                alpha = Math.max(alpha,bestVal);
                if (beta <= alpha)
                {break;}
                
               
            }   

        }
        return bestVal;
    }
     
   

    int minValue(GameBoard currentGame, int depth, int maxPlayer, int alpha, int beta)
     
    {
         int bestVal= 9999999;
         int value;
         
        if (currentGame.getPieceCount()==42)
            {return (currentGame.getScore(maxPlayer) - currentGame.getScore((maxPlayer % 2)+1));}
        
         
          if (depth ==0)
            {
                return (currentGame.evaluate_board(maxPlayer) - currentGame.evaluate_board((maxPlayer % 2)+1));
            }
    
         
         for (int i=0; i<7; i++)
        {  
           if(currentGame.playPiece(i))
            {
                value = maxValue(currentGame,depth - 1, maxPlayer, alpha, beta);
                bestVal = Math.min( bestVal, value); 
                
                //Undo last piece played
                currentGame.removePiece(i);
               
                //alpha beta prunning
                beta = Math.min(beta, bestVal);
                if (beta <= alpha)
                {break;}
                
 
               
            }
            
           

        }
          return bestVal;
    }
   

}
 
