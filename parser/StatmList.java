package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class StatmList extends PascalSyntax{
	Statement s;
	ArrayList<Statement> sArray = new ArrayList<Statement>();
	StatmList(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<statm list> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		for(int i = 0; i <sArray.size(); i++){
			sArray.get(i).genCode(f);
		}
	}
	
	@Override void prettyPrint(){
		for(int i =0; i<sArray.size();i++){
			sArray.get(i).prettyPrint();
			if(i!=(sArray.size()-1)){
				Main.log.prettyIndent();
				Main.log.prettyPrint(";");
				Main.log.prettyOutdent();
			}
			Main.log.prettyPrintLn();
		}
	}

	@Override void check(Block curScope, Library lib) {
		for(int i = 0 ; i<sArray.size(); i++){
			sArray.get(i).check(curScope, lib);
		}
	}

	static StatmList parse(Scanner s){
		enterParser("statm list");
		StatmList sl = new StatmList(s.curLineNum());
		sl.s=Statement.parse(s);
		sl.sArray.add(sl.s);
		while(s.curToken.kind.equals(semicolonToken)){
			s.skip(semicolonToken);
			sl.s=Statement.parse(s);
			sl.sArray.add(sl.s);
		}
		leaveParser("statm list");
		return sl;
	}

}