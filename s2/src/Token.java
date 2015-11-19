//Jonathan Jefford-Baker & Rickard Kodet
//Modifierat kodskelett från kurshemsidan (skrivet av Per Austrin)
//The different token types in the parser
enum TokenType{
	Command, PenCommand, Rep, Number, Wrong, Dot, Quote, Hexcode, Whitespace
}
class Token {
	private TokenType type;
	private String data;
	private int line;
	
	//A token doesn't have to contain any data(Dot,Quote,Whitespace)
	public Token(TokenType type, int line){
		this.type = type;
		this.line = line;
		data = null;
	}
	//This is for the tokens that needs data(Command,Number,DoubleNumber,Hexcode
	public Token(TokenType type, String data, int line){
		this.type = type;
		this.line = line;
		this.data = data;
	}
	//Gets the type of the token
	public TokenType getTokenType(){
		return type;
	}
	//Gets the data of the token
	public String getTokenData(){
		return data;
	}
	//Gets the linenumber of the token
	public int getTokenLineNumber(){
		return line;
	}
}