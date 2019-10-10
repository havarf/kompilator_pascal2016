package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class TermOperator extends Operator{
	String type;
	Term left;
	Term right;

	TermOperator(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<term operator> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(" "+type+" ");
	}
	
	@Override void check(Block curScope, Library lib) {
		if(!type.equals("or")){
			left.type.checkType(lib.integerType, "left " + type + " operand", this,
				"left side not integer");
			right.type.checkType(lib.integerType, "right " + type + " operand", this,
				"right side not integer");
		}else{
			left.type.checkType(lib.booleanType, "left '" + type + "' operand", this,
				"left side not boolean");
			right.type.checkType(lib.booleanType, "right '" + type + "' operand", this,
				"right side not boolean");
		}
	}

	public static TermOperator parse(Scanner s){
		enterParser("term opr");
		TermOperator to = new TermOperator(s.curLineNum());
		switch(s.curToken.kind){
			case addToken: to.type="+"; break;
			case subtractToken: to.type="-"; break;
			case orToken: to.type="or"; break;
			default: to.error("TermOperator: not a valid token: " + s.curToken.kind); break;
		}
		s.readNextToken();
		leaveParser("term opr");
		return to;
	}
	
}