package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class RelOperator extends Operator{
	String type;
	//SimpleExpr left;
	//SimpleExpr right;
	RelOperator(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<rel opr> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	 
	 
	@Override void prettyPrint(){
		Main.log.prettyPrint(" " + type + " ");
	}

	@Override void check(Block curScope, Library lib) {}

	public static RelOperator parse(Scanner s){
		enterParser("rel opr");
		RelOperator ro = new RelOperator(s.curLineNum());
		switch (s.curToken.kind){
			case equalToken: ro.type="="; break;
			case notEqualToken: ro.type="<>"; break;
			case lessToken: ro.type="<"; break;
			case lessEqualToken: ro.type="<="; break;
			case greaterToken: ro.type=">"; break;
			case greaterEqualToken: ro.type=">="; break;
			default: Main.error("RelOperator: Not a valid Token: " + s.curToken.kind); break;
		}
		s.readNextToken();
		leaveParser("rel opr");
		return ro;
	}
}