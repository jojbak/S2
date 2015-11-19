//Jonathan Jefford-Baker & Rickard Kodet
//Modifierat kodskelett från kurshemsidan (skrivet av Per Austrin)

//TODO Kolla upp regex så att matchningen av siffror, commands samt whitespaces blir korrekt.
//kolla även upp den negerade regexen

import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;

public class Lexer {
	private ArrayList<Token> tokens;
	private int currentLine = 1;
	SyntaxError se;
	private boolean newLine = false;
	private Token prevAdded; 
	
	//this function takes the input and turns it into string
	private static String readInput() throws Exception{
		//reads everything until end of file
		//String input = new Scanner(System.in).useDelimiter("\\z").next();
		//File file = new File("input.txt");
		Scanner sc = new Scanner(System.in);
		StringBuilder sb = new StringBuilder();
		while(sc.hasNext()){
			sb.append(sc.nextLine());
			sb.append("\n");
		}
		String input = sb.toString();
		sc.close();
		//System.out.println(input);
		return input.toUpperCase();
	}
	//performs the transformation from string to tokens
	public Lexer() throws Exception{
		String input = readInput();
		String newLineRegex = "\\r\\n|\\n|\\r";
		//"       (?<=\\s+|[.]|\")COMMAND/REP(?=(\\s+))     "
		//"       (?<=\\s+|[.]|\")PENCOMMAND(?=(\\s+|[.]))     "
		//String regex = "(FORW|BACK|LEFT|RIGHT|DOWN|UP|COLOR|REP|(?<=\\s+)[0-9]+(?=([.]|\\s+))|[.]|\"|\\s+|#[0-9a-fA-F]{6}|%.*\\n)";
		String regex = "FORW|BACK|LEFT|RIGHT|COLOR|REP|DOWN|UP|[0-9]+|[.]|\"|[\\f\\t]+|#[0-9a-fA-F]{6}|%.*\\n|\\r\\n|\\n|\\r";
		String invers = "[^((?<=\\s*|[.]|\")(FORW|BACK|LEFT|RIGHT|COLOR|REP)(?=\\s+|%)|(?<=\\s+|[.]|\")(DOWN|UP)(?=\\s+|[.]|%)|[0-9]+|\\s+[0-9]+|(?=[.]|%|\\s+)|[.]|\"|[\\f\\t]+|#[0-9a-fA-F]{6}(?=[.]|\\s+|%)|%.*\\n|\\r\\n|\\n|\\r)]";
		//Pattern hexcode = Pattern.compile("#[0-9a-fA-F]{6}");
		//Pattern wrongSyntax = Pattern.compile("[^FORW|BACK|LEFT|RIGHT|DOWN|UP|COLOR|REP|[0-9]+|[0-9]+[.][0-9]+|[.]|\"|\\s+|#[0-9a-fA-F]{6}|%.*\\n]");
		//This regex matches all valid inputs
		//also matches all invalid inputs so we can catch these on the correct line
		Pattern tokenPattern = Pattern.compile(regex + "|[^" + regex + "]");
				//+ "[^FORW|BACK|LEFT|RIGHT|DOWN|UP|COLOR|REP|[0-9]+|[.]|\"|\\s+|#[0-9a-fA-F]{6}|%.*\\n]");
		//Pattern notTokenPatten = Pattern.compile(tokenPattern|^tokenPattern)
		Matcher m = tokenPattern.matcher(input);
		tokens = new ArrayList<Token>();
		
		//finding tokens/whitespaces/comments in input
		while(m.find()) {
			//System.out.println(m.group());
			
			if(m.group().equals("FORW")){
				tokens.add(new Token(TokenType.Command,"FORW",currentLine));
				setFalse();
			}
			else if(m.group().equals("BACK")){
				tokens.add(new Token(TokenType.Command,"BACK",currentLine));
				setFalse();
			}
			else if(m.group().equals("LEFT")){
				tokens.add(new Token(TokenType.Command,"LEFT",currentLine));
				setFalse();
			}
			else if(m.group().equals("RIGHT")){
				tokens.add(new Token(TokenType.Command,"RIGHT",currentLine));
				setFalse();
			}
			else if(m.group().equals("DOWN")){
				tokens.add(new Token(TokenType.PenCommand,"DOWN",currentLine));
				setFalse();
			}
			else if(m.group().equals("UP")){
				tokens.add(new Token(TokenType.PenCommand,"UP",currentLine));
				setFalse();
			}
			else if(m.group().equals("COLOR")){
				tokens.add(new Token(TokenType.Command,"COLOR",currentLine));
				setFalse();
			}
			else if(m.group().equals("REP")){
				tokens.add(new Token(TokenType.Rep,currentLine));
				setFalse();
			}
			else if(m.group().equals(".")){
				tokens.add(new Token(TokenType.Dot,currentLine));
				setFalse();
			}
			else if(m.group().equals("\"")){
				tokens.add(new Token(TokenType.Quote,currentLine));
				setFalse();
			}
			else if(m.group().matches("#[0-9a-fA-F]{6}")){			
				tokens.add(new Token(TokenType.Hexcode,m.group(),currentLine));
				setFalse();
			}
			else if(m.group().matches("[0-9]+")){
				String str = m.group().toString().trim();
				if(Integer.parseInt(str) > 0){
				tokens.add(new Token(TokenType.Number,str,currentLine));
				setFalse(); 
				}
				else
					tokens.add(new Token(TokenType.Wrong,null,currentLine));
			}
			else if(m.group().matches("\\r\\n|\\n|\\r")){
				//if we previously added a whitespace no need for more
				if(!newLine){
				tokens.add(new Token(TokenType.Whitespace,currentLine));
				newLine = true;
				}
				currentLine++;
			}
			else if(m.group().matches("\\s+")){				
				//if we previously added a whitespace no need for more
				if(!newLine){
				tokens.add(new Token(TokenType.Whitespace,currentLine));
				newLine = true;
				}
			}
			//if comment we want to disregard the rest of the line
			//i.e start to read again after the next \n
			//possible error when we check 
			//"%.*\\n"
			else if(m.group().matches("%.*\\n"))	{
				//if we previously added a whitespace no need for more
				if(!newLine){
				tokens.add(new Token(TokenType.Whitespace,currentLine));
				newLine = true;
				}
				currentLine++;
			}			
				//currentLine++;
			//if noone of the above if statements catch 
			//this case should only match .* catchall
			//if its not port of the language then its syntaxerror 
			else if(m.group().matches("[^" + regex + "]")){
				//System.out.println(m.group());
				tokens.add(new Token(TokenType.Wrong,null,currentLine));
				//se = new SyntaxError(currentLine,"Lexer");
			} 	
		}
		/*for(Token token:tokens){
			System.out.println(token.getTokenType() + " " + token.getTokenLineNumber());
		}*/
	}
	
	//return the list of tokens
	public ArrayList<Token> getTokens(){
		return tokens;
	}
	
	private void setFalse(){
		newLine = false;
	}
}

//possible error: adding extra whitespace at the end of whole string

