//Jonathan Jefford-Baker & Rickard Kodet
import java.util.List;
import java.util.ArrayList;

//TODO Lyckas inte med Ett program med radbrytning mitt i ett token, ska ge syntaxfel. (off by one)
public class Main {

	public static void main(String[] args) throws SyntaxError, Exception {
		try{
		Lexer lexer = new Lexer();
		List<Token> tokens = lexer.getTokens();
		Parser parser = new Parser();
		Translator translator = new Translator(parser.createTree(tokens));
		}catch(SyntaxError e){
			String rad = e.getMessage();
			System.out.println(rad);
		}
	}

}