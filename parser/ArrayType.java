package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class ArrayType extends Type{
	Constant c1;
	Constant c2;
	Type typeOf;
	types.Type indexType;

	ArrayType(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<array type> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("array ["); c1.prettyPrint();
		Main.log.prettyPrint(".."); c2.prettyPrint();
		Main.log.prettyPrint("] of "); typeOf.prettyPrint();
	}
	@Override void check(Block curScope, Library lib) {
		c1.check(curScope, lib);
		
		c2.check(curScope, lib);
		typeOf.check(curScope, lib);
		c1.type.checkType(c2.type, "array limits", this,
          "limits not the same type");
		
		indexType=c1.type;
	
		type = new types.ArrayType(typeOf.type, indexType, c1.iValue, c2.iValue);
	}

	static ArrayType parse(Scanner s){
		enterParser("array-type");
		ArrayType at = new ArrayType(s.curLineNum());
		s.skip(arrayToken);
		s.skip(leftBracketToken);
		at.c1=Constant.parse(s);
		s.skip(rangeToken);
		at.c2=Constant.parse(s);
		s.skip(rightBracketToken);
		s.skip(ofToken);
		at.typeOf=Type.parse(s);
		leaveParser("array-type");
		return at;
	}
}