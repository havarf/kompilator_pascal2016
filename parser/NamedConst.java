package parser;

import main.*;
import scanner.*;
import scanner.TokenKind.*;

class NamedConst extends UnsignedConstant{
	String typen;
	ConstDecl ref; 
	NamedConst(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<named constant> " + typen + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "movl", "&"+ref.iValue+"%eax", ""+typen);
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(typen);
	}

	@Override void check(Block curScope, Library lib) {
		PascalDecl pd = curScope.findDecl(typen, this);
		ref = (ConstDecl) pd;
		iValue=ref.iValue;
		type= pd.type;
	}	

	public static NamedConst parse(Scanner s){
		enterParser("named constant");
		NamedConst nc = new NamedConst(s.curLineNum());
		nc.typen=s.curToken.id;
		s.readNextToken();
		leaveParser("named constant");
		return nc;
	}
	
}