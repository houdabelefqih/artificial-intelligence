/*
* HOUDA BELEFQIH 1001511875
* 5360-002 ARTIFICIAL INTELLIGENCE I
* ASSIGNMENT 6 : PROPOSITIONAL LOGIC
* SPRING 2018
*/
import java.io.*;
import java.util.*;

/**
 * @author james spargo
 *
 */
public class CheckTrueFalse {
    
    static HashMap<String, Boolean> model = new HashMap<String, Boolean>();
    static List<String> known_symbols_list = new ArrayList<String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		
		//create the knowledge base and the statement
		LogicalExpression knowledge_base = new LogicalExpression();
		LogicalExpression statement = new LogicalExpression();

		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );
			
			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while(  ( buffer = inputStream.readLine() ) != null ) 
                        {
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) )) 
                                {
					//the line is not a comment
                                        /*Check if it's a line with one symbol
                                        only or negation of a single symbol*/
                                        initialize_known_symbols(buffer);
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				} 
                                else 
                                {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		} catch(Exception e) 
                {
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
		
		
		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );
			
			//load the additional knowledge
			System.out.println("loading the additional knowledge...");
			
			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while(  ( buffer = inputStream.readLine() ) != null) 
                        {
                                if( !(buffer.startsWith("#") || (buffer.equals("") ))) 
                                {
                                         /*Check if it's a line with one symbol
                                        only or negation of a single symbol*/
                                        initialize_known_symbols(buffer);
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
                                } 
                                else 
                                {
				//the line is a comment. do nothing and read the next line
                                }
                          }
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		
		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		// print the knowledge_base
		knowledge_base.print_expression("\n");
		
		
		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );
			
			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			
			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					    //the line is not a comment
						statement = readExpression( buffer );
                                                break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file
		
		// check for a valid statement
		if( !valid_expression( statement ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		
		//print the statement
		statement.print_expression( "" );
		//print a new line
		System.out.println("\n");
                
                /* TESTING
                *******************
                System.out.println("Symbols KNOWN: \n");
                for (String symbol : known_symbols_list) {
                       
			System.out.println(symbol);
		}
                System.out.println("__________________________________________________________ \n");
                
                System.out.println("Symbols extracted from knowledge base: \n");
                
                List<String> result = extract_symbols(knowledge_base);
                result.removeAll(known_symbols_list);
                
                int counter=0;
                
                for (String symbol : result) {
                       
			System.out.println(symbol);
                        counter++;
		}
                
                System.out.println( counter + " TOTAL SYMBOLS! \n");
                
                */
                System.out.println( "Calling inference engine KB entails alpha... \n");
                
                boolean KB_entails_alpha = tt_entails(knowledge_base, statement, false);
                
                System.out.println( "Calling inference engine KB entails NOT alpha... \n");
                boolean KB_entails_not_alpha = tt_entails(knowledge_base, statement, true);
                
                finalResultEvaluation(KB_entails_alpha,KB_entails_not_alpha);


	} //end of main

	/* this method reads logical expressions
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression( String input_string ) 
        {
          LogicalExpression result = new LogicalExpression();
          
          //testing
          //System.out.println("readExpression() beginning -"+ input_string +"-");
          //testing
          //System.out.println("\nread_exp");
          
          //trim the whitespace off
          input_string = input_string.trim();
          
          if( input_string.startsWith("(") ) 
          {
            //its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString = input_string.substring( 1 );
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if( !symbolString.endsWith(")" ) ) 
            {
              // missing the closing paren - invalid expression
              System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
              exit_function(0);
              
            }
            else 
            {
              //remove the last ')'
              //it should be at the end
              symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
              symbolString.trim();
              
              //testing
              //System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
              // read the connective into the result LogicalExpression object					  
              symbolString = result.setConnective( symbolString );
              
              //testing
              //System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
              //System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions( read_subexpressions( symbolString ) );
            
          } 
          else 
          {   	
            // the next symbol must be a unique symbol
            // if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol( input_string );
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
          }
          
          return result;
        }

	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 * 
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */
	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {

	Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
	LogicalExpression newExpression;// = new LogicalExpression();
	String newSymbol = new String();
	
	//testing
	//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
	//System.out.println("\nread_sub");

	input_string.trim();

	while( input_string.length() > 0 ) {
		
		newExpression = new LogicalExpression();
		
		//testing
		//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

		if( input_string.startsWith( "(" ) ) {
			//its a subexpression.
			// have readExpression parse it into a LogicalExpression object

			//testing
			//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
			// find the matching ')'
			int parenCounter = 1;
			int matchingIndex = 1;
			while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
					if( input_string.charAt( matchingIndex ) == '(') {
						parenCounter++;
					} else if( input_string.charAt( matchingIndex ) == ')') {
						parenCounter--;
					}
				matchingIndex++;
			}
			
			// read untill the matching ')' into a new string
			newSymbol = input_string.substring( 0, matchingIndex );
			
			//testing
			//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

			// pass that string to readExpression,
			newExpression = readExpression( newSymbol );

			// add the LogicalExpression that it returns to the vector symbolList
			symbolList.add( newExpression );

			// trim the logicalExpression from the input_string for further processing
			input_string = input_string.substring( newSymbol.length(), input_string.length() );

		} else {
			//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

			// I only want the first symbol, so, create a LogicalExpression object and
			// add the object to the vector
			
			if( input_string.contains( " " ) ) {
				//remove the first string from the string
				newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
				input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );
				
				//testing
				//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
			} else {
				newSymbol = input_string;
				input_string = "";
			}
			
			//testing
			//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
			newExpression.setUniqueSymbol( newSymbol );
			
	    	//testing
	    	//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

			symbolList.add( newExpression );
			
			//testing
			//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
		
		input_string.trim();
		
		if( input_string.startsWith( " " )) {
			//remove the leading whitespace
			input_string = input_string.substring(1);
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
	}
	return symbolList;
}


	/* this method checks to see if a logical expression is valid or not 
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *  
	 * */
	public static boolean valid_expression(LogicalExpression expression)
	{
		
		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
			}

		// symbol is empty, so
		// check to make sure the connective is valid
	  
		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
		      ( expression.getConnective().equalsIgnoreCase("iff") ) ) {
			
			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
				}
			}
		// end 'if / iff' check
	  
		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" ); 
				return false;
				}
			}
		// end check for 'not'
		
		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
			}
		// end check for 'and / or / not'
		// end connective check

	  
		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();
			
			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		// if the method made it here, the expression must be valid
		return true;
	}

    /** this function checks to see if a unique symbol is valid */
    //////////////////// this function should be done and complete
    // originally returned a data type of long.
    // I think this needs to return true /false
    //public long valid_symbol( String symbol ) {
    public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {
				
				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");
				
				return false;
			}
		}
		
		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
                
	}
    
    private static void exit_function(int value) {
                System.out.println("exiting from checkTrueFalse");
                  System.exit(value);
                }	
 
/*________________________________________________________________________________________________*/
/*________________________________________________________________________________________________*/
/*______________________________|| ADDED FUNCTIONS FOR ASSIGNMENT 6 ||____________________________*/    
/*________________________________________________________________________________________________*/   
/*________________________________________________________________________________________________*/ 
    

    
    //This method extracts the unique symbols from a given sentence
    public static void initialize_known_symbols(String line)       
    {  
        String symbol ="";
        line = line.toUpperCase();
        
        //Check that the line is a signle symbol only
        if (!line.startsWith("(")) 
        {
         symbol = line.substring(0);//get the symbol
         model.put(symbol,true);//assign true to its value in the model
         known_symbols_list.add(symbol);//add it to the known symbol values list
        
        }
        
        //Check if the line is the negation of one symbol only
        else if(line.startsWith("(NOT") && !(line.startsWith("(NOT (")))
        {
          symbol = line.substring(line.indexOf(" ") + 1, line.indexOf(")"));
          model.put(symbol,false);//assign false to its value in the model
          known_symbols_list.add(symbol);//add it to the known symbol values list
        }
        
        else
            return;
  
    }
    
    //This method extracts the unique symbols from a given sentence
    public static List<String> extract_symbols(LogicalExpression sentence)
    {
        
        List<String> symbols_list = new ArrayList<String>();
        
        //Check the root of the tree
        if(sentence.getUniqueSymbol() != null )
            {symbols_list.add(sentence.getUniqueSymbol().toUpperCase());}
        
        else 
        {
            LogicalExpression nextExpression;
            for( Enumeration child = sentence.getSubexpressions().elements(); child.hasMoreElements();)
            {
              nextExpression = ( LogicalExpression )child.nextElement();
              
              //Merge the resulting lists from each recursion step and remove duplicates
              symbols_list.removeAll(extract_symbols(nextExpression));
              symbols_list.addAll(extract_symbols(nextExpression));

            }
        }
       
        return symbols_list;
    }
   
    //This method implements the TT-entails? algorithm
    public static boolean tt_entails(LogicalExpression KB, LogicalExpression alpha, boolean alphaNegated)
    {
        List<String> symbols_KB = extract_symbols(KB);
        List<String> symbols_alpha = extract_symbols(alpha);
            //Merge the two lists of symbols
              symbols_KB.removeAll(symbols_alpha);
              symbols_KB.addAll(symbols_alpha);

        List<String> all_symbols = symbols_KB;
        all_symbols.removeAll(known_symbols_list);//remove already known symbol values
        

        return tt_check_all(KB, alpha, all_symbols, model, alphaNegated);
    }
   
    //This method implements the TT-entails? algorithm
    public static boolean tt_check_all(LogicalExpression KB, LogicalExpression alpha,
                                List<String> symbols, HashMap<String, Boolean> model, boolean alphaNegated)
    {
        if(symbols.isEmpty())
        {
            if (PL_true(KB,model))
            {
                //Check if we are calculatin entailment for not alpha
                if (!alphaNegated)
                {return PL_true(alpha, model);}
                
                else 
                {return !(PL_true(alpha, model));}
            
            }
            else {return true;}
        }
        else{
            
        //Get the first symbol in the symbols list
         String P = symbols.get(0);
         symbols.remove(0);
        //Put the remaining symbols in another list : rest
         List<String> rest = symbols;
        
         return tt_check_all(KB, alpha, rest, assignValue(P, true, model), alphaNegated) &&
         tt_check_all(KB, alpha, rest, assignValue(P, false, model), alphaNegated);
        }
  
    }
    
    
    //This method checks if the given sentence is true in the given model
    public static boolean PL_true(LogicalExpression sentence, HashMap<String, Boolean> model) {

        //For testing
        //System.out.println(sentence.getConnective());
        
        if (sentence.getUniqueSymbol()!= null)
        {return model.get(sentence.getUniqueSymbol().toUpperCase());}
        
        //Handle AND
        else if (sentence.getConnective().equalsIgnoreCase("and"))
        {
            LogicalExpression nextExpression;
            for( Enumeration child = sentence.getSubexpressions().elements(); child.hasMoreElements();)
            {
              nextExpression = ( LogicalExpression )child.nextElement();
              
              if (PL_true(nextExpression,model)== false)
              {return false;}
            }
            return true;
        }//end and
        
        //Handle OR
         else if (sentence.getConnective().equalsIgnoreCase("or"))
        {
            LogicalExpression nextExpression;
            for( Enumeration child = sentence.getSubexpressions().elements(); child.hasMoreElements();)
            {
              nextExpression = ( LogicalExpression )child.nextElement();
              if (PL_true(nextExpression,model)== true)
              {return true;}
              
            }
            return false;
        }//end or
        
        //Handle XOR
         else if (sentence.getConnective().equalsIgnoreCase("xor"))
        {
            int no_of_trues =0;
            LogicalExpression nextExpression;
            for( Enumeration child = sentence.getSubexpressions().elements(); child.hasMoreElements();)
            {
              nextExpression = ( LogicalExpression )child.nextElement();
              if (PL_true(nextExpression,model)== true)
              { no_of_trues++;
                if (no_of_trues >= 2)
                            return false;
              }
            
            }
            
            if (no_of_trues == 1){return true;}
            
            return false;
    
        }// end xor
        
        //Handle IF
         else if (sentence.getConnective().equalsIgnoreCase("if"))
        {
            LogicalExpression leftChild = (LogicalExpression) sentence.getSubexpressions().get(0);
            LogicalExpression rightChild = (LogicalExpression) sentence.getSubexpressions().get(1);
 
            if ((PL_true(leftChild, model) == true) && (PL_true(rightChild, model) == false))
            {return false;}
            
            else return true;
        }//end if
        
         //Handle IFF
         else if (sentence.getConnective().equalsIgnoreCase("iff"))
        {
            LogicalExpression leftChild = (LogicalExpression) sentence.getSubexpressions().get(0);
            LogicalExpression rightChild = (LogicalExpression) sentence.getSubexpressions().get(1);
 
            if (PL_true(leftChild, model) == (PL_true(rightChild, model)))
            {return true;}
            
            else return false;
        }//end iff
        
        //Handle NOT
         else if (sentence.getConnective().equalsIgnoreCase("not"))
        {
            LogicalExpression leftChild = (LogicalExpression) sentence.getSubexpressions().get(0);
 
            if (PL_true(leftChild, model) == true ){return false;}
            else return true;
        }//end not
        
        
        //For testing
       //System.out.println("SHOULDN'T BE HERE");
  
  //Function shouldn't arrive here      
  return false;
    }
    
    
    //This method assigns true or false to a given P string ie a given symbol
    public static HashMap<String, Boolean> assignValue(String P, boolean truth_value, HashMap<String, Boolean> model) {
        model.put(P.toUpperCase(), truth_value);
        return model;
    }
    

    
    //THIS METHOD CHECKS THE FINAL RESULT OF ENTAILMENT AND PRINTS RESULT INTO A TXT FILE                                          
    public static void finalResultEvaluation(boolean KB_entails_alpha, boolean KB_entails_not_alpha) {
        // Initialize the result statement to the program not knowing the result
        String finalResult = "I don't know if the statement is definitely true or definitely false.";
        
        /***************** EVALUATE THE FOUR CASES FOR KB entails alpha & KB entails (not alpha) ********************/

        if (KB_entails_alpha && !KB_entails_not_alpha)          {finalResult = "definitely true.";} 
        
        else if (!KB_entails_alpha && KB_entails_not_alpha)     {finalResult = "definitely false.";}
        
        else if (!KB_entails_alpha && !KB_entails_not_alpha)    {finalResult = "possibly true, possibly false.";}
        
        else if (KB_entails_alpha && KB_entails_not_alpha)      {finalResult = "both true and false.";}
        
        //System.out.println("\n RESULT FINAL :" + finalResult);
        printFinalResultToFile(finalResult, "result.txt");

    }//end finalResultEvaluation
       
    
    //THIS METHOD OUTPUTS THE FINAL ENTAILMENT DECISION TO A .TXT FILE                                                                  
    public static void printFinalResultToFile(String finalResult, String resultOutputFile)
    {
        try {
            // write the result of entailment
             BufferedWriter output = new BufferedWriter(new FileWriter(resultOutputFile));
            
            // write the result of entailment
                output.write(finalResult);
                
                output.close();
           

        } 
        catch (IOException e) {
            System.out.println("\nError writing in : " + resultOutputFile + " file!\n");
        }
    }


   
}