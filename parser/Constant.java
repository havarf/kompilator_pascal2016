package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class Constant extends PascalSyntax{
	PrefixOperator po;
	UnsignedConstant uc;
	types.Type type;
	int iValue;
	char cValue;
	boolean bValue;

	Constant(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<constant> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		if(po!=null){
			po.prettyPrint();
		}
		uc.prettyPrint();
	}
	@Override void check(Block curScope, Library lib) {
		uc.check(curScope, lib);
		type=uc.type;
		if(po!=null){
			po.check(curScope, lib);
			uc.type.checkType(lib.integerType, "prefix " + po.type+" operand", this,
				"constant not integer");
			if(po.type.equals("-")){
				iValue= -iValue;
			}
		}
		if(type ==lib.integerType){
			iValue=uc.iValue;
		}
		else if(type==lib.booleanType){
			bValue=uc.bValue;
		}
		else if(type==lib.charType){
			cValue=uc.cValue;
		}else{
			Main.panic("constant: " + type + " = " + lineNum);
		}
	}
	
	static Constant parse(Scanner s){
		enterParser("constant");

		Constant c = new Constant(s.curLineNum());

		if(s.curToken.kind.isPrefixOpr()){
			c.po=PrefixOperator.parse(s);
		}
		c.uc=UnsignedConstant.parse(s);
		leaveParser("constant");
		return c;
	}
}
