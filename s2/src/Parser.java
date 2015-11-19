//Jonathan Jefford-Baker & Rickard Kodet
//Modifierat kodskelett från kurshemsidan (skrivet av Per Austrin)
import java.util.List;
import java.util.ArrayList;

public class Parser {
	private ArrayList<Token> tokens;
	private int currentToken = 0;
	private SyntaxError se;
	private int lastCorrectLine = 0; // ändra
	private int lengthList;

	// TODO: FIXA PROBLEMET PÅ RAD 3 MED COLOR

	public ArrayList<ParseTree> createTree(List<Token> tokenList)
			throws SyntaxError {
		// tokens = tokenList;
		tokens = new ArrayList<Token>(tokenList);
		if(!(tokens.size() == 0)){
		//check for error that the last token is whitespace
		//this is ignored
		Token wCheck = tokens.get(tokens.size()-1);
		if(wCheck.getTokenType() == TokenType.Whitespace){
			tokens.remove(tokens.size()-1);
		}
		}
		lengthList = tokens.size();
		ArrayList<ParseTree> list = new ArrayList<ParseTree>();
		while (currentToken < lengthList) {
			ParseTree tree = parseTree();
			list.add(tree);
		}
		// System.out.println(list.toString());
		return list;
	}

	// returns a ParseTree which is leaf or tree
	// for Rep we just calls this repeatedly and get a linked list
	// that is what should happen inside
	public ParseTree parseTree() throws SyntaxError {
		Token t = peekToken();

		// only happens if first is whitespace
		if (t.getTokenType() == TokenType.Whitespace) {
			// do nothing and step forward
			// whitespace will consume all whitespace
			// so we know that there is only one
			nextToken();
			t = peekToken();
		}

		// we encounter a Command
		if (t.getTokenType() == TokenType.Command) {
			lastCorrectLine = t.getTokenLineNumber();
			return command(t);
		}
		// we encounter a PenCommand
		else if (t.getTokenType() == TokenType.PenCommand) {
			lastCorrectLine = t.getTokenLineNumber();
			return penCommand(t);
		}
		// we encounter a Rep command
		else if (t.getTokenType() == TokenType.Rep) {
			lastCorrectLine = t.getTokenLineNumber();
			return rep(t);
		} else if (t.getTokenType() == TokenType.Wrong) {
			throw new SyntaxError(t.getTokenLineNumber());
		}
		// if the tokens doesn't match we have an error
		else
			throw new SyntaxError(t.getTokenLineNumber());

		// return null;

	}

	// if Command tokenType
	// can be FORW,BACK,LEFT,RIGHT,COLOR
	public ParseTree command(Token start) throws SyntaxError {
		lastCorrectLine = start.getTokenLineNumber();
		String command = start.getTokenData();
		String data = null;

		// go forward
		nextToken();
		Token t = peekToken();

		// must be whitespace between command and following token
		if (!checkWhitespace(t))
			throw new SyntaxError(t.getTokenLineNumber());
		else
		{
			nextToken();
			t = peekToken();
		}

		// if COLOR command
		if (command.equals("COLOR")) {
			
			// lastCorrectLine = t.getTokenLineNumber();
			if (t.getTokenType() != TokenType.Hexcode) {
				// if COLOR is not followed by hexcode throw error
				throw new SyntaxError(t.getTokenLineNumber());
			} else {
				lastCorrectLine = t.getTokenLineNumber();
				data = t.getTokenData();
			}
		}
		// else is any of the other COMMANDS i.e uses numbers as data
		else {
			if (t.getTokenType() != TokenType.Number) {
				// if command !COLOR is not followed by number throw error
				throw new SyntaxError(t.getTokenLineNumber());
			} else {
				lastCorrectLine = t.getTokenLineNumber();
				data = t.getTokenData();
			}
		}
		// go forward
		nextToken();
		t = peekToken();

		// want to ignore whitespace between number and dot
		// if whitespace we jump forward
		if (checkWhitespace(t)) {
			nextToken();
			t = peekToken();
		}

		// check that we end the command with a dot
		if (t.getTokenType() != TokenType.Dot) {
			throw new SyntaxError(t.getTokenLineNumber());
		}
		// the last correct command happened on the dot
		lastCorrectLine = t.getTokenLineNumber();
		// goes to next token in list
		nextToken();
		// returns a leaf with the correct command and its corresponding data
		// example: COLOR,#fff000
		ParseTree leaf = new ParseTree(command, data);
		return leaf;
	}

	// if penCommand tokentype
	// can be DOWN,UP
	public ParseTree penCommand(Token start) throws SyntaxError {
		lastCorrectLine = start.getTokenLineNumber();
		String command = start.getTokenData();
		// goes to next token
		nextToken();
		Token t = peekToken();

		// check whitespace
		// optional whitespace between penCommand and dot
		if (checkWhitespace(t)) {
			nextToken();
			t = peekToken();
		}

		// checks that we have the dot following a pencommand
		if (t.getTokenType() != TokenType.Dot) {
			throw new SyntaxError(t.getTokenLineNumber());
		}
		lastCorrectLine = t.getTokenLineNumber();
		// goes to next token in list
		nextToken();
		// returns a leaf with the correct command and null as data
		// example: DOWN, "empty string"
		ParseTree leaf = new ParseTree(command, "empty string");
		return leaf;
	}

	// if rep tokenType
	// can be REP
	public ParseTree rep(Token start) throws SyntaxError {
		lastCorrectLine = start.getTokenLineNumber();
		ArrayList<ParseTree> list = new ArrayList<ParseTree>();
		String command = "Rep";
		nextToken();
		Token t = peekToken();

		// need to have whitespace between rep and number
		if (!checkWhitespace(t)) {
			throw new SyntaxError(t.getTokenLineNumber());
		}
		nextToken();
		t = peekToken();

		// checks if there is a following number
		if (t.getTokenType() != TokenType.Number) {
			throw new SyntaxError(t.getTokenLineNumber());
		} else {
			lastCorrectLine = t.getTokenLineNumber();
			String data = t.getTokenData();
			nextToken();
			t = peekToken();

			// need to have whitespace between
			// number and command/"
			if (!checkWhitespace(t)) {
				throw new SyntaxError(t.getTokenLineNumber());
			}
			nextToken();
			t = peekToken();

			// we encounter a Command
			if (t.getTokenType() == TokenType.Command) {
				ParseTree pTree = command(t);
				list.add(pTree);
				ParseTree branch = new ParseTree(command, data, list);
				return branch;
			}
			// we encounter a PenCommand
			else if (t.getTokenType() == TokenType.PenCommand) {
				ParseTree pTree = penCommand(t);
				list.add(pTree);
				ParseTree branch = new ParseTree(command, data, list);
				return branch;
			}
			// we encounter a Rep
			else if (t.getTokenType() == TokenType.Rep) {
				ParseTree pTree = rep(t);
				list.add(pTree);
				ParseTree branch = new ParseTree(command, data, list);
				return branch;
			}
			// we encounter a quote, possibly followed by several commands
			else if (t.getTokenType() == TokenType.Quote) {
				nextToken();
				t = peekToken();

				// optional whitespace
				if (checkWhitespace(t)) {
					nextToken();
					t = peekToken();
				}

				lastCorrectLine = t.getTokenLineNumber();
				list = repCommandFirst(t);
				ParseTree branch = new ParseTree(command, data, list);
				return branch;
				// otherwise we have a syntax error
			} else {
				throw new SyntaxError(t.getTokenLineNumber());
			}
		}
		// return null;
	}

	// the first instance of the rep iteration
	private ArrayList<ParseTree> repCommandFirst(Token t) throws SyntaxError {
		ArrayList<ParseTree> list = new ArrayList<ParseTree>();
		// we encounter a Command
		if (t.getTokenType() == TokenType.Command) {
			ParseTree leaf = command(t);
			list.add(leaf);
			t = peekToken();
			lastCorrectLine = t.getTokenLineNumber();
			return repCommandLoop(t, list);
		}
		// we encounter a PenCommand
		else if (t.getTokenType() == TokenType.PenCommand) {
			ParseTree leaf = penCommand(t);
			list.add(leaf);
			t = peekToken();
			lastCorrectLine = t.getTokenLineNumber();
			return repCommandLoop(t, list);
		}
		// we encounter a Rep
		else if (t.getTokenType() == TokenType.Rep) {
			ParseTree leaf = rep(t);
			list.add(leaf);
			t = peekToken();
			return repCommandLoop(t, list);
		} else {
			throw new SyntaxError(t.getTokenLineNumber());
		}
	}

	private ArrayList<ParseTree> repCommandLoop(Token t,
			ArrayList<ParseTree> list) throws SyntaxError {

		// optional whitespace
		if (checkWhitespace(t)) {
			nextToken();
			t = peekToken();
		}
		// terminate loop because of ending quote
		// kan eventuellt ta lång tid
		while (t.getTokenType() != TokenType.Quote) {
			//allow optional whtiespace
			if (checkWhitespace(t)) {
				nextToken();
				t = peekToken();
			}
			lastCorrectLine = t.getTokenLineNumber();
			if (t.getTokenType() == TokenType.Command) {
				ParseTree leaf = command(t);
				list.add(leaf);
				t = peekToken();
			} else if (t.getTokenType() == TokenType.PenCommand) {
				ParseTree leaf = penCommand(t);
				list.add(leaf);
				t = peekToken();
			} else if (t.getTokenType() == TokenType.Rep) {
				ParseTree leaf = rep(t);
				list.add(leaf);
				t = peekToken();
			}
			// we have a syntax error
			else {
				throw new SyntaxError(t.getTokenLineNumber());
			}
		}
		nextToken();
		// t = peekToken();
		lastCorrectLine = t.getTokenLineNumber();
		return list;
	}

	// peek at the next token
	private Token peekToken() throws SyntaxError {
		if (!hasMoreElements()) {
			throw new SyntaxError(lastCorrectLine);
		}
		return tokens.get(currentToken);
	}

	// go to the next token
	public Token nextToken() throws SyntaxError {
		Token res = peekToken();
		++currentToken;
		return res;
	}

	// check if the list tokens has more elements left
	private boolean hasMoreElements() {
		return currentToken < tokens.size();
	}

	/*
	 * private void error(int errorLine) throws SyntaxError{ Token t =
	 * peekToken(); //System.out.println(t.getTokenData()); throw new
	 * SyntaxError(errorLine,"Parser"); }
	 */
	// checks if current tokentype is whitespace
	private boolean checkWhitespace(Token t) {
		return (t.getTokenType() == TokenType.Whitespace);
	}
}
