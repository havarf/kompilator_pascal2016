package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class InnerExpr extends Factor{
	Expression e;

	InnerExpr(int lNum){
		super(lNum);
	}	
	@Override public String identify(){
		return "<inner expr> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		e.genCode(f);
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("(");
		e.prettyPrint();
		Main.log.prettyPrint(")");
	}

	@Override void check(Block curScope, Library lib) {
		e.check(curScope, lib);
		type=e.type;
	}
	
	public static InnerExpr parse(Scanner s){
		enterParser("inner expr");
		InnerExpr ie = new InnerExpr(s.curLineNum());
		s.skip(leftParToken);
		ie.e=Expression.parse(s);
		s.skip(rightParToken);
		leaveParser("inner expr");
		return ie;
	}
}