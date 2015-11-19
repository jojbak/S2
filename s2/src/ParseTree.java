//import java.util.List;
import java.util.ArrayList;
public class ParseTree {
	private String type; //this is for example "FORW" 
	private String data; //this is for example "34"
	private ArrayList<ParseTree> parseT;
	
	//leaf constructor
	public ParseTree(String type, String data){
		this.type = type;
		this.data = data;
	}
	//branch constructor
	public ParseTree(String type, String data, ArrayList<ParseTree> list){
		this.type = type;
		this.data = data;
		parseT = list; //byt listtyp
	}
	
	public String getData(){
		return data;
	}
	
	public String getType(){
		return type;
	}
	
	public ArrayList<ParseTree> getTree(){
		return parseT;
	}

}
