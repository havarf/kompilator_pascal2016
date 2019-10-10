package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class CharLiteral extends UnsignedConstant{
	
	CharLiteral(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<char literal> " + type + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		int charValue = (int) cValue;
		f.genInstr("", "movl", "$"+charValue+",%eax", "	'"+cValue+"'");
	}
	@Override void prettyPrint(){
		Main.log.prettyPrint("'"+type+"'");
	}
	@Override void check(Block curScope, Library lib) {
		type=lib.charType;
	}

	static CharLiteral parse(Scanner s){
		enterParser("char literal");
		CharLiteral cl = new CharLiteral(s.curLineNum());
		cl.cValue=s.curToken.charVal;
		s.readNextToken();
		leaveParser("char literal");
		return cl;

	}
}