//Jonathan Jefford-Baker & Rickard Kodet

//throws syntaxerror which is the only exception in our parser
//gives information regarding what line the error occured
public class SyntaxError extends Exception{
	
	public SyntaxError(int lineNumber){
		super("Syntaxfel på rad " + lineNumber);
		//System.out.println("Syntaxfel på rad "+ lineNumber); //+ " i metod" + metod);
		//System.exit(0);
	}

}