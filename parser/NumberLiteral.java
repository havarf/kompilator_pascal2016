package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class NumberLiteral extends UnsignedConstant{
	NumberLiteral(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<number literal> " + iValue + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "movl", "$"+iValue+",%eax", "	"+iValue);
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(""+iValue);
	}
	@Override void check(Block curScope, Library lib) {
		type = lib.integerType;
	}
	
	public static NumberLiteral parse(Scanner s){
		enterParser("number literal");
		NumberLiteral nl = new NumberLiteral(s.curLineNum());
		s.test(intValToken);
		nl.iValue=s.curToken.intVal;
		s.readNextToken();
		leaveParser("number literal");
		return nl;
	}	
}