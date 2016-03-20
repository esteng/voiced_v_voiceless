import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//     /Users/Elias/Desktop/LAB/GE.txt
public class day1 {
	private static ArrayList<String> lines = new ArrayList<String>();
	private static final Comparator<String> Comparator = null;
	private static HashSet<String> transcriptions = new HashSet<String>(50000);
	private static ArrayList<String> stopWords = new ArrayList<String>();
	public static void main(String[] args) throws IOException
	{	
		String line = null;
		Scanner scan = new Scanner(System.in);
		System.out.println("give the path (including filename) for the file you would like to use: "); //get the path from the user
		String path = scan.next();

		FileReader fr = null;
		try {
			fr = new FileReader(path); //read the file
		} catch (Exception e) {
			System.out.println("malformed path"); //if file does not exist/bad path
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr); //take the file line by line
			PrintStream out = new PrintStream(new FileOutputStream("/Users/Elias/Desktop/FinalGerOut2.txt"));
			System.setOut(out);
		try {
			while((line = br.readLine()) != null) { //assign each line to variable line
				lines.add(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		String excludeRegex=".*[^0-9<>'+.,]";
		String lengthRegex=".(([\t ][A-Za-z]+ *)+){2,}"; //at least 3 segments
	//	String test = "f il r u n t ts v a n ts i C s t etu n";
	//	System.out.println(test.matches(lengthRegex));
		for(String l : lines) getTranscription(l);
		//add more output, say what you've completed, with a counter
		for(String l : lines)
		{
			Word W = makeWord(l);
			if(getStops(W.ortho)&&W.ortho.matches(excludeRegex)&&W.trans.matches(lengthRegex)) //if it doesn't end in a non-word character
			{
				W.underlying= Confirm2(W);
				out.println(W.getOrtho() + " \t"	+  W.getUnderlying());
			}
		}
	
	}
	/**
	 * get just transcription, put it in ArrayList
	 * @param input
	 */
	public static void getTranscription(String input){ 
		String regex = "([a-zA-Z]{0,3} .*)+"; //this is the regex
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input); //match to each line
		if(m.find()){
			transcriptions.add(input.substring(m.start(), input.length())); //add the substring with just transcription
		}

	}


	public static boolean getVoicelessStops2(String input){
		String regex = "([A-Z a-z])+[tpk] *$"; //look for just word-final voiceless stops
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input); //match to each transcription 
		if(m.find())
		{
			return true;
		}
		return false;
	}
	public static boolean getStops(String input){
		String regex = "([A-Z a-z])+[tpkbdg] *$"; //look for just word-final voiceless stops
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input); //match to each transcription 
		if(m.find()&&!(input.charAt(input.length()-1)=='g'&&input.charAt(input.length()-2)=='n')) //doesn't end in -ng
		{
			return true;
		}
		return false;
	}


	public static String Confirm2(Word root) throws FileNotFoundException
	{
		char oEnd = root.ortho.charAt(root.ortho.length()-1);
		//oEnd is last character of orthographic form
		String trans = root.getTrans();
		ArrayList<String> matching = new ArrayList<String>();
		String newWord = trans.substring(0,trans.length()-1);
	//	System.out.println(newWord+" is newWord" + " oEnd is " + oEnd);
		String newRegex = "";
		String endRegex = "";
		if(oEnd == 'd')  endRegex = "t.*"; //if ortho ends in d, look for ending in t in trans
		else if(oEnd == 'b')  endRegex = "p.*";
		else if(oEnd == 'g')  endRegex = "k.*";
		else endRegex = oEnd+" ?"; //this will always be true because the space is optional, but will fix the out of bounds exception
		newRegex = ".*"+newWord+endRegex;
	//	System.out.println(newRegex + " is newRegex");
		String tabRegex = "([\t ][A-Za-z]+){0,2}[\t \n]\n";
		Pattern r = Pattern.compile(newRegex);
		Pattern p = Pattern.compile(tabRegex);
		for(String t: transcriptions)
		{
			//check if it adds more than 2 sounds
			
			Matcher m = r.matcher(t); //match to each line
			Matcher d = p.matcher(t);
			if(m.find()) {
				matching.add(t); //if you find a confirmation
			}
		}
		MyComparator comp = new MyComparator();
		Collections.sort(matching, comp); //comparator that sorts in increasing string length
		String secondRegex = newRegex.substring(0, newRegex.length()-2)+" ?[aeiou]"; //get rid of the .* after the dbg and replace it with " ?[aeiou]"
	//	System.out.println(endRegex + " is endRegex");
		Pattern pat = Pattern.compile(secondRegex);
		for(String m : matching) {
			Matcher match = pat.matcher(m);
			if(match.find()){
				//	System.out.println(endRegex.substring(0,endRegex.length()-2));
				return endRegex.substring(0,endRegex.length()-2);
			}
		}
		return ""+oEnd;
	}
	/**
	 * make a word out of a line
	 * @param line
	 * @return a word from that line
	 */
	public static Word makeWord(String line){
		Word W = new Word();
		//String[] split = line.split(" +");

		String o = line.substring(0, line.indexOf("\t"));
		W.setOrtho(o); //set the orthographic part
		W.setLastChar(o.charAt(o.length()-1));
		String t = getTranscription2(line);
		W.setTrans(t); //set the transcription

		return W;
	}
	/**
	 * get the transcription portion of a line
	 * @param input
	 * @return trans
	 */
	public static String getTranscription2(String input){ 
		String regex = "([a-zA-Z]{0,3} .*)+"; //this is the regex
		String blank = "";
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input); //match to each line
		if(m.find()){
			return (input.substring(m.start(), input.length())); //add the substring with just transcription
		}
		return blank;
	}
	//search thru trans to find negative instances
	/*	public static int Deny(String root)
	{
		int negOcc = 0; //negative occurrence: a ptk segment really is ptk

		String newRegex = "";
		newRegex = ".*"+root+".*"; //any letter surrounded by stopWords element 

		Pattern r = Pattern.compile(newRegex);
		for(String t: transcriptions)
		{

			Matcher m = r.matcher(t); //match to each line
			if(m.find()) negOcc++; 
		}

		return negOcc;
	}
	/*public static void CompTest()
	{

		ArrayList<String > C = new ArrayList<String>();
		C.add("S t ue n d etu");
		C.add("S t ue n d etu n");
		C.add("a n d atu t h a l p S t ue n d i g etu n");
		C.add("aI n S t ue n d i g etu r");
		C.add("b etu S t ue n d etu");
		C.add("b etu S t ue n d etu n");
		C.add("d r aI S t ue n d i g atu");
		C.add("d r aI S t ue n d i g etu n");
		C.add("e n t S t ue n d etu n");
		C.add("f il l S t ue n d i g etu n");
		C.add("f il r S t ue n d i g etu n");
		C.add("m el r S t ue n d i g etu");
		C.add("m el r S t ue n d i g etu n");
		C.add("m el r S t ue n d i g etu r");
		C.add("ts ul S t ue n d etu n");
		C.add("ts v a i S t ue n d i g etu n");
		C.add("ts v aI S t ue n d i g etu n");
		MyComparator comp = new MyComparator();
		Collections.sort(C, comp);
		for(String c : C) System.out.println(c);

	}*/

	//search through transcriptions to find positive instances
/*	public static String Confirm(String root) throws FileNotFoundException
	{

		ArrayList<String> matching = new ArrayList<String>();
		String newWord = root.substring(0,root.length()-1);
		String newRegex = "";
		if(root.charAt(root.length()-1) == 't')  newRegex = ".*"+newWord+"d.*"; //if it ends in t, look for ending in d
		if(root.charAt(root.length()-1) == 'p')  newRegex = ".*"+newWord+"b.*";
		if(root.charAt(root.length()-1) == 'k')  newRegex = ".*"+newWord+"g.*";

		Pattern r = Pattern.compile(newRegex);
		for(String t: transcriptions)
		{

			Matcher m = r.matcher(t); //match to each line
			if(m.find()) {
				matching.add(t);

			}
		}
		MyComparator comp = new MyComparator();
		Collections.sort(matching, comp);
		String secondRegex = newRegex.substring(0, newRegex.length()-2)+" ?[aeiou]"; //get rid of the .* after the dbg and replace it with " ?[aeiou]"
		Pattern pat = Pattern.compile(secondRegex);
		String S = "";
		for(String m : matching) {
			Matcher match = pat.matcher(m);
			if(match.find()){
				S =root + " has been confirmed to be underlyingly " + newRegex.substring(2,newRegex.length()-2);
				break;
			}
		}
		return S;
	} */
	/**
	 * add all the words ending in ptk to stopWords ArrayList
	 * @param input
	 */
	/*	public static void getVoicelessStops(String input){
		String regex = "([A-Z a-z])+[tpk] *$"; //look for just word-final voiceless stops
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(input); //match to each transcription 
		if(m.find())
		{
			stopWords.add(input);
		}
	} */
	/*	PrintStream out1 = new PrintStream(new FileOutputStream("output2.txt"));

	PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
	System.setOut(out);*/

	/*		for(String s : transcriptions) {
//		out1.println(s);
		getVoicelessStops(s);
	}  */
	/*		for(String s: stopWords) {
		String S =Confirm(s);
		if(!S.equals("")) out.println(S);
	//	System.out.println(Deny(s));
	} 
	 */


}
