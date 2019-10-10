package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class FactorOperator extends Operator{
	String type;
	Factor left;
	Factor right;
	FactorOperator(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<factor opr> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(" " + type+" ");
	}
	@Override void check(Block curScope, Library lib) {
		if(!type.equals("and")){
			left.type.checkType(lib.integerType, "left " + type + " operand", this,
				"left side not integer");
			right.type.checkType(lib.integerType, "right " + type + " operand", this,
				"right side not integer");
		}else{
			left.type.checkType(lib.booleanType, "left '" + type + "' operand", this,
				"left side not integer");
			right.type.checkType(lib.booleanType, "right '" + type + "' operand", this,
				"right side not integer");
		}
	}
	
	static FactorOperator parse(Scanner s){
		enterParser("factor opr");
		FactorOperator fo = new FactorOperator(s.curLineNum());
		switch(s.curToken.kind){
			case multiplyToken: fo.type="*"; break;
			case divToken: fo.type="div"; break;
			case modToken: fo.type="mod"; break;
			case andToken: fo.type="and"; break;
			default: Main.error("FactorOperator: No valid TokenKind: " + s.curToken.kind); break;
		}
		s.readNextToken();
		leaveParser("factor opr");
		return fo;

	}
	
}