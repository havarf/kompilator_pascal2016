package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class PrefixOperator extends Operator{
	String type;
	PrefixOperator(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<prefix opr> on line " + lineNum; 
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(" " +type+ " ");
	}
	@Override void check(Block curScope, Library lib) {

	}
	
	public static PrefixOperator parse(Scanner s){
		enterParser("prefix opr");
		PrefixOperator po = new PrefixOperator(s.curLineNum());
		if(s.curToken.kind.equals(addToken)){
			s.skip(addToken);
			po.type="+";
		}
		else if(s.curToken.kind.equals(subtractToken)){
			s.skip(subtractToken);
			po.type="-";
		}
		else{
			Main.error("PrefixOperator: not av valid token: " + s.curToken.kind);
		}

		leaveParser("prefix opr");
		return po;
	}
}