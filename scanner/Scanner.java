package scanner;

import main.Main;
import static scanner.TokenKind.*;
import java.io.*;

public class Scanner {
	public Token curToken = null, nextToken = null; 

	private LineNumberReader sourceFile = null;
	private String sourceFileName, sourceLine = "";
	private int sourcePos = 0;
	//public static boolean slutt=false;
	public Scanner(String fileName) {
		sourceFileName = fileName;
		try {
			sourceFile = new LineNumberReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			Main.error("Cannot read " + fileName + "!");
		}
		
		readNextToken();  readNextToken();



	}


	public String identify() {
		return "Scanner reading " + sourceFileName;
	}


	public int curLineNum() {
		return curToken.lineNum;
	}


	private void error(String message) {
		Main.error("Scanner error on " +
			(curLineNum()>0 ? "last line" : "line "+curLineNum()) + 
			": " + message);
	}


	public void readNextToken() {
		curToken = nextToken;  nextToken = null;

	// Del 1 her:
		//boolean slutt =false;
		boolean funnet =false;
		//loop som looper til programmet finner en token
		while(funnet==false){
			int x=0;
			//leser ny linje om linjen er tom
			boolean whitespace=true;
			if(sourceLine.equals(" ")||sourceLine.isEmpty()){
				readNextLine();
			}
			for(int y=0; y <sourceLine.length(); y++){
				//litt usikker paa hva du mente med at Tab ogsaa regnes som whitespace
				//mente du at sjekken burde gjores slik?
				if(sourceLine.charAt(y)!=' '||sourceLine.charAt(y)!='\t'){
					whitespace=false;
				}
			}
			if(whitespace==true){
				readNextLine();
			}
			if(getFileLineNum()==0){
				nextToken=new Token(eofToken, getFileLineNum());
				funnet=true;
				Main.log.noteToken(nextToken);
				break;
			}
			/*if(slutt==true){
				nextToken=new Token(eofToken, getFileLineNum());
				Main.log.noteToken(nextToken);
				break;
			}*/

			if(sourceLine.charAt(x)== '.'){															
					//sjekkom om end of file
					/*if(curToken.kind.equals(endToken)){
						nextToken=new Token(dotToken, getFileLineNum());
						Main.log.noteToken(nextToken);
						//slutt=true;
						break;
							
					}*/
					//sjekker om det er rangeToken
					if(sourceLine.charAt(x+1)=='.'){
						nextToken=new Token(rangeToken, getFileLineNum());
						Main.log.noteToken(nextToken);
						sourceLine=sourceLine.substring(x+2);
						funnet=true;
						break;
					}
						//sjekker om det er dotToken
					else if(sourceLine.charAt(x+1) !='.'){
						nextToken = new Token(dotToken, getFileLineNum());
						Main.log.noteToken(nextToken);
						funnet=true;
						sourceLine=sourceLine.substring(x+1);
					}
				}
				//ser etter kommentar start:
				int tall=0;
				if(sourceLine.charAt(x) == '/' &&sourceLine.charAt((x+1))=='*'){
					//fant en kommentarstart, må finne slutten
					boolean fant=false;
					tall=getFileLineNum();
					while(fant==false){
						for(int y = x; y<(sourceLine.length()-1); y++){
							if(sourceLine.charAt(y)=='*'&&sourceLine.charAt((y+1))=='/'){
								//fant slutten på kommentaren
								fant=true;
								sourceLine=sourceLine.substring(y+2);
								break;
							}
						}
						if(fant==false){
							readNextLine();
							if(sourceLine.equals("")){
								Main.error("Scanner error on line "+tall+": No end for comment starting on line " + tall +"!");
							}

						}	
					}
				}
				else if(sourceLine.charAt(x) =='{'){
					//fant kommentarstart, må finne slutten
					boolean fantden=false;
					tall=getFileLineNum();
					while(fantden==false){
						for(int y = x; y<sourceLine.length(); y++){
							if(sourceLine.charAt(y) == '}'){
								//fant slutten
								sourceLine=sourceLine.substring(y+1);
								fantden=true;
								break;
							}
						}
						//om programmet har lest hele linjen leses neste
						if(fantden==false){
							readNextLine();
							//om filen som leses er tom logges deten feilmelding
							if(sourceLine.equals("")){
								Main.error("Scanner error on line "+tall+": No end for comment starting on line " + tall +"!");
							}

						}
					}
				}
				//sjekk for bokstav
				else if(isLetterAZ(sourceLine.charAt(x))){
					//fant bokstav
					for(int y=x; y<sourceLine.length(); y++){
						if(!isLetterAZ(sourceLine.charAt(y))&&!isDigit(sourceLine.charAt(y))){
							//ikke lenger bokstav eller tall;
							//la til toLowerCase() her
							nextToken = new Token(sourceLine.substring(x, y).toLowerCase(), getFileLineNum());
							sourceLine=sourceLine.substring(y);
							funnet = true;
							Main.log.noteToken(nextToken);
							break;
						}
					}
				}
				else if(isDigit(sourceLine.charAt(x))){
					//fant tall
					for(int y=x; y<sourceLine.length(); y++){
						if(!isDigit(sourceLine.charAt(y))){
							//ikke lenger tall!
							nextToken = new Token((Integer.parseInt(sourceLine.substring(x, y))), getFileLineNum());
							sourceLine=sourceLine.substring(y);
							funnet = true;
							Main.log.noteToken(nextToken);
							break;
						}
					}
				}
				//sjekker addToken
				else if(sourceLine.charAt(x) == '+'){
					nextToken = new Token(addToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker assignToken, og colonToken
				else if(sourceLine.charAt(x) == ':'){
					if(sourceLine.charAt(x+1) == '='){
						nextToken=new Token(assignToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+2);
						funnet=true;
						Main.log.noteToken(nextToken);
						break;
					}else{
						nextToken = new Token(colonToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+1);
						funnet=true;
						Main.log.noteToken(nextToken);
						break;
					}
				}
				//sjekker commaToken
				else if(sourceLine.charAt(x) == ','){
					nextToken=new Token(commaToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker equalToken
				else if(sourceLine.charAt(x) == '='){
					nextToken = new Token(equalToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					Main.log.noteToken(nextToken);
					funnet=true;
					break;
				}
				//sjekker greaterToken og greaterEqualToken
				else if(sourceLine.charAt(x)=='>'){
					if(sourceLine.charAt(x+1)=='='){
						nextToken=new Token(greaterEqualToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+2);
					}
					else{
						nextToken=new Token(greaterToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+1);
					}
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker leftBracketToken
				else if(sourceLine.charAt(x)=='['){
					nextToken=new Token(leftBracketToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker leftParToken
				else if(sourceLine.charAt(x) == '('){
					nextToken = new Token(leftParToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker lessToken
				else if(sourceLine.charAt(x)=='<'){
					//sjekker lessEqualToken
					if(sourceLine.charAt(x+1)=='='){
						nextToken=new Token(lessEqualToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+2);
					}
					//sjekker notEqualToken
					else if(sourceLine.charAt(x+1)=='>'){
						nextToken=new Token(notEqualToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+2);
					}else{
						nextToken=new Token(lessToken, getFileLineNum());
						sourceLine=sourceLine.substring(x+1);
					}
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker multiplyToken
				else if(sourceLine.charAt(x)=='*'){
					nextToken=new Token(multiplyToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker RightBracketToken
				else if(sourceLine.charAt(x)==']'){
					nextToken=new Token(rightBracketToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker rightParToken
				else if(sourceLine.charAt(x) == ')'){
					nextToken = new Token( rightParToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//Sjekker semicolonToken
				else if(sourceLine.charAt(x) == ';'){
					nextToken = new Token(semicolonToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break; 
				}
				//sjekker subtractToken
				else if(sourceLine.charAt(x)=='-'){
					nextToken=new Token(subtractToken, getFileLineNum());
					sourceLine=sourceLine.substring(x+1);
					funnet=true;
					Main.log.noteToken(nextToken);
					break;
				}
				//sjekker for char
				else if(sourceLine.charAt(x) == '\''){
					//sjekker om ''''
					if(sourceLine.charAt(x+1)=='\''&&sourceLine.charAt(x+2)=='\''&&sourceLine.charAt(x+3)=='\''){
						nextToken=new Token(sourceLine.charAt(x+2),getFileLineNum());
						sourceLine=sourceLine.substring(x+4);
						funnet=true;
						Main.log.noteToken(nextToken);
					}
					//sjekker om fnutten lukkes
					else if(sourceLine.charAt(x+2)=='\''){
						nextToken = new Token(sourceLine.charAt(x+1), getFileLineNum());
						sourceLine=sourceLine.substring(x+3);
						//x=0;
						funnet=true;
						Main.log.noteToken(nextToken);
						break;
					}
					else{
						Main.error("Scanner error on line "+ getFileLineNum()+ ": Illegal char literal!");
					}
				}
				//fjerner space
				else if(sourceLine.charAt(x)==' '){
							sourceLine=sourceLine.substring(x+1);
					}
					//sjekker om linjen er tom, og leser en ny linje ved behov
				else if(sourceLine.equals(" ")){
					readNextLine();
				}
				else if(sourceLine.charAt(x) == '\t'){
					//litt usikker paa om du mente jeg trengte denne eller ikke
					sourceLine=sourceLine.substring(x+1);
				}
				
				else{
					Main.error("Scanner error on line: " + getFileLineNum() + " Illegal character: '" +sourceLine.charAt(x)+"'!");
				}
				x=0;
		}//while

		

	}


	private void readNextLine() {
		if (sourceFile != null) {
			try {
				sourceLine = sourceFile.readLine();
				if (sourceLine == null) {
					sourceFile.close();  sourceFile = null;
					sourceLine = "";  
				} else {
					sourceLine += " ";
				}
				sourcePos = 0;
			} catch (IOException e) {
				Main.error("Scanner error: unspecified I/O error!");
			}
		}
		if (sourceFile != null) 
			Main.log.noteSourceLine(getFileLineNum(), sourceLine);
	}


	private int getFileLineNum() {
		return (sourceFile!=null ? sourceFile.getLineNumber() : 0);
	}


    // Character test utilities:

	private boolean isLetterAZ(char c) {
		return 'A'<=c && c<='Z' || 'a'<=c && c<='z';
	}


	private boolean isDigit(char c) {
		return '0'<=c && c<='9';
	}


    // Parser tests:

	public void test(TokenKind t) {
		if (curToken.kind != t)
			testError(t.toString());
	}

	public void testError(String message) {
		Main.error(curLineNum(), 
			"Expected a " + message +
			" but found a " + curToken.kind + "!");
	}

	public void skip(TokenKind t) {
		test(t);  
		readNextToken();
	}
}
