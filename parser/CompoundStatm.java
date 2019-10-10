package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
/* <Compund-Statm> ::= 'begin' <statm list> 'end'*/

class CompoundStatm extends Statement{
	StatmList list;
	CompoundStatm(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<compound statm> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		list.genCode(f);
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrintLn("begin"); Main.log.prettyIndent();
		list.prettyPrint(); Main.log.prettyOutdent();
		Main.log.prettyPrint("end"); 
	}
	@Override void check(Block curScope, Library lib) {
		list.check(curScope, lib);	
	}
	
	static CompoundStatm parse(Scanner s){
		enterParser("compound statm");

		CompoundStatm cs = new CompoundStatm(s.curLineNum());
		s.skip(beginToken);
		cs.list=StatmList.parse(s);
		s.skip(endToken);
		leaveParser("compound statm");
		return cs;
	}

}