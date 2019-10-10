package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class EmptyStatm extends Statement{
	
	EmptyStatm(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<empty statm> at line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("");
	}
	@Override void check(Block curScope, Library lib) {
	}

	static EmptyStatm parse(Scanner s){
		enterParser("empty statm");
		EmptyStatm es = new EmptyStatm(s.curLineNum());
		leaveParser("empty statm");
		return es;
	}
}