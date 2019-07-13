/*
* HOUDA BELEFQIH 1001511875
* 5360-002 ARTIFICIAL INTELLIGENCE I
* ASSIGNMENT 4 : GAME PLAYING ALGORITHMS
* SPRING 2018
*/

/**
 * 
 * @author James Spargo
 * This class controls the game play for the Max Connect-Four game. 
 * To compile the program, use the following command from the maxConnectFour directory:
 * javac *.java
 *
 * the usage to run the program is as follows:
 * ( again, from the maxConnectFour directory )
 *
 *  -- for interactive mode:
 * java MaxConnectFour interactive [ input_file ] [ computer-next / human-next ] [ search depth]
 *
 * -- for one move mode
 * java maxConnectFour.MaxConnectFour one-move [ input_file ] [ output_file ] [ search depth]
 * 
 * description of arguments: 
 *  [ input_file ]
 *  -- the path and filename of the input file for the game
 *  
 *  [ computer-next / human-next ]
 *  -- the entity to make the next move. either computer or human. can be abbreviated to either C or H. This is only used in interactive mode
 *  
 *  [ output_file ]
 *  -- the path and filename of the output file for the game.  this is only used in one-move mode
 *  
 *  [ search depth ]
 *  -- the depth of the minimax search algorithm
 * 
 *   
 */

import java.io.*;
import java.util.Scanner;

public class maxconnect4
{
  public static void main(String[] args) 
  {
    // check for the correct number of arguments
    if( args.length != 4 ) 
    {
      System.out.println("Four command-line arguments are needed:\n"
                         + "Usage: java [program name] interactive [input_file] [computer-next / human-next] [depth]\n"
                         + " or:  java [program name] one-move [input_file] [output_file] [depth]\n");

      exit_function( 0 );
     }
		
    // parse the input arguments
    String game_mode = args[0].toString();// the game mode
    String input = args[1].toString();// the input game file
    int depthLevel = Integer.parseInt( args[3] );  		// the depth level of the ai search
		
    // create and initialize the game board
    GameBoard currentGame = new GameBoard(input);
    
    // create the Ai Player
    AiPlayer calculon = new AiPlayer();
		
    //  variables to keep up with the game
    int playColumn = 99;				//  the players choice of column to play
    boolean playMade = false;			//  set to true once a play has been made

    
     
    if(game_mode.equalsIgnoreCase( "one-move" )) 
    {

    /****************************************ONE-MOVE MODE *****************************************************/
    
    // get the output file name
    String output = args[2].toString();				// the output game file
    
    System.out.print("\nMaxConnect-4 game\n");
    System.out.print("Game state before move:\n");
    
    //print the current game board
    currentGame.printGameBoard();
    
    // print the current scores
    System.out.println( "Score: Player 1 = " + currentGame.getScore( 1 ) +
			", Player2 = " + currentGame.getScore( 2 ) + "\n " );
    
    int current_player = currentGame.getCurrentTurn();

    // ****************** this chunk of code makes the computer play
    if( currentGame.getPieceCount() < 42 && depthLevel != 0) 
    {
	// AI play
	playColumn = calculon.findBestPlay( currentGame, depthLevel);

	
	// play the piece
	currentGame.playPiece( playColumn );
        	
        // display the current game board
        System.out.println("move " + currentGame.getPieceCount() 
                           + ": Player " + current_player
                           + ", column " + playColumn);
        System.out.print("game state after move:\n");
        currentGame.printGameBoard();
    
        // print the current scores
        System.out.println( "Score: Player 1 = " + currentGame.getScore( 1 ) +
                            ", Player2 = " + currentGame.getScore( 2 ) + "\n " );
        
        currentGame.printGameBoardToFile( output );
    } 
    else 
    {
	System.out.println("\nI can't play.\nThe Board is Full\n\nGame Over");
        return;
    }
    
    }//end of one-move mode	
   
    /*v******************** INTERACTIVE MODE *******************v*/
    
     else if( game_mode.equalsIgnoreCase( "interactive" ) ) 
    {
          // get the next player (computer or human)
           String next_player = args[2].toString();
           String human_output = "human.txt";
           String computer_output = "computer.txt";
           int counter = 0;
           
           System.out.print("\nMaxConnect-4 game\n");
           System.out.println("-----------------------------------------------------");
           
       
       do{
            
           
            //printing current board state & scores for both players

             System.out.print("Game state before move:\n");

             //print the current game board
             currentGame.printGameBoard();

             // print the current scores
             System.out.println( "Score: Player 1 = " + currentGame.getScore( 1 ) +
                                 ", Player2 = " + currentGame.getScore( 2 ) + "\n " );
             
       
          
            //IF THE NEXT PLAYER IS THE COMPUTER, play and print result in computer.txt
            if(next_player.equalsIgnoreCase( "computer-next" ))
            {
                if( currentGame.getPieceCount() < 42 && depthLevel != 0) 
                {
                    int current_player = currentGame.getCurrentTurn();
                    // AI play 
                    playColumn = calculon.findBestPlay( currentGame, depthLevel  );

                    // play the piece
                    currentGame.playPiece( playColumn );
                    
                    
                    int computer_column = (playColumn+1);
                    // display the current game board
                    System.out.println("Computer played move #" + currentGame.getPieceCount() 
                                       + ": Player " + current_player
                                       + ", column " +  computer_column);
                    

                    // print the current scores
                    System.out.println( "Score: Player 1 = " + currentGame.getScore( 1 ) +
                                        ", Player2 = " + currentGame.getScore( 2 ) + "\n " );
                    System.out.println("-------------------------------------------------------");

                    currentGame.printGameBoardToFile(computer_output);
                } 
                else 
                {
                    System.out.println("\nI can't play.\nThe Board is Full\n\nGame Over");
                    return;
                }
                
             //After saving the result in the human_output file, pass the turn to the computer
                next_player = "human-next";
                
                
                if(counter>0)
                {   // create and initialize the game board
                input = computer_output;
                currentGame = new GameBoard(input);
                }
                
                counter++;
                
            }
             
            //if next player is human
            else if(next_player.equalsIgnoreCase( "human-next" ))
              {
                  int column_human_input = 0;
                  
                 if( currentGame.getPieceCount() < 42 && depthLevel != 0)
                 {
                    int human_player = currentGame.getCurrentTurn();
                    
                    //KEEP ASKING FOR MOVE FROM HUMAN UNTIL VALID MOVE
                   do 
                   {
                       System.out.println("Please enter a non-full column number to play a move.");
                       System.out.println("Columns are numbered from 1 to 7.\n");
                       
                        Scanner scanner = new Scanner(System.in);
                        
                        do {
                            
                            System.out.println("NUMBERS from 1 to 7 ONLY!");
                            while (!scanner.hasNextInt()) {
                                String user_input = scanner.next();
                                System.out.println("NUMBERS from 1 to 7 ONLY!");
                            }
                            
                            column_human_input = scanner.nextInt();
                            
                           
                        } while (column_human_input < 1 || column_human_input > 7 );
                       
                       
                    } while (!currentGame.playPiece(column_human_input-1));                  

                    // display the current game board
                    System.out.println("Human played move #" + currentGame.getPieceCount() 
                                       + ": Player " + human_player
                                       + ", column " + column_human_input);
                    
                  
                    // print the current scores
                    System.out.println( "Score: Player 1 = " + currentGame.getScore( 1 ) +
                                        ", Player2 = " + currentGame.getScore( 2 ) + "\n " );
                    
                    System.out.println("-------------------------------------------------------");

                    currentGame.printGameBoardToFile(human_output);

                 }
                 else 
                {
                    System.out.println("\nI can't play.\nThe Board is Full\n\nGame Over");
                    return;
                }
                   
                                    
                //After saving the result in the human_output file, pass the turn to the computer
                next_player = "computer-next";
                input = human_output;
                // create and initialize the game board
                if(counter>0)
                 {currentGame = new GameBoard(input);}

                 counter++;
                        
              }//end if human is next
            
            //If user enters wrong turn
            else {
            System.out.println( "Error! Next player can be either : computer-next or human-next " );
            return;
            
            }
            
        } while(true);
           
           
            

    } //end of interactive 
    
     else{System.out.println("MODE GAME NOT RECOGNIZED!"); return;}
    
return;
} // end of main()
	
  /**
   * This method is used when to exit the program prematurely.
   * @param value an integer that is returned to the system when the program exits.
   */
  private static void exit_function( int value )
  {
      System.out.println("exiting from MaxConnectFour.java!\n\n");
      System.exit( value );
  }
} // end of class connectFour
