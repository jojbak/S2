//Jonathan Jefford-Baker & Rickard Kodet

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.math.*;

public class Translator {
	private int listSize;
	private int incr = 0;
	private double posXStart = 0.0;
	private double posYStart = 0.0;
	private double posXEnd = 0.0;
	private double posYEnd = 0.0;
	private double angle = 0;
	private String color = "#0000FF";
	private boolean makeMark= false;
	
	public Translator(ArrayList<ParseTree> list){
		listSize = list.size();
		while(incr < listSize){
			ParseTree tree = list.get(incr);
			if(tree.getType().equals("Rep")){
				translateRep(tree);
			}
			else{
				translateOtherCmd(tree);
			}
			//move forwards in list
			incr++;
		}
	}
	//translates the rep command
	private void translateRep(ParseTree tree){
		int iterations = toInt(tree.getData());
				
		ArrayList<ParseTree> treeList = tree.getTree();
		//treeList = new ArrayList<ParseTree>();
		
		int treeListLen = treeList.size();
		//repeat given amount of times
		for(int i=0; i < iterations; i++){
			//iterate through the list of ParseTrees
			for(int j=0; j < treeListLen; j++){
				ParseTree t = treeList.get(j);
				if(t.getType().equals("Rep"))
					translateRep(t);
				else
					translateOtherCmd(t);
			}
		}
	}
	//this method takes care of the commands that are not Rep
	private void translateOtherCmd(ParseTree tree){
		String command = tree.getType();
		String data = tree.getData();
		switch(command){
		case "FORW" :
			posXStart = posXEnd;
			posXEnd = posXStart + toInt(data) * Math.cos((Math.PI * angle)/180);
			posYStart = posYEnd;
			posYEnd = posYStart + toInt(data) * Math.sin((Math.PI * angle)/180);
			printPattern();
			break;
		case "BACK" :
			posXStart = posXEnd;
			posXEnd = posXStart - toInt(data) * Math.cos((Math.PI * angle)/180);
			posYStart = posYEnd;
			posYEnd = posYStart - toInt(data) * Math.sin((Math.PI * angle)/180);
			printPattern();
			break;
		case "LEFT" :
			angle += toInt(data);
			break;
		case "RIGHT":
			angle -= toInt(data);
			if (angle < 0)
				angle = 360 + angle; //angles can't be negative
			break;
		case "DOWN" :
			makeMark = true;
			break;
		case "UP"	:
			makeMark = false;
			break;
		case "COLOR":
			color = data.toUpperCase();
			break;
			
		}
	}
	
	//prints Leonardo's pattern
	private void printPattern(){
		DecimalFormat df = new DecimalFormat("0.0000");
		//posXStart = Double.valueOf(df.format(posXStart));
		//posXStart = (posXStart * 10000.0) / 10000.0;
		//checks if the pen is down or up
		if(makeMark)
		System.out.println(color + " " + df.format(posXStart) + " " + df.format(posYStart) + " " + df.format(posXEnd) + " " + df.format(posYEnd));
	}
	
	private int toInt(String s){
		return Integer.parseInt(s);
	}
}
