package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

abstract class UnsignedConstant extends Factor{
	UnsignedConstant(int lNum){
		super(lNum);
	}

	static UnsignedConstant parse(Scanner s){
		enterParser("unsigned constant");
		UnsignedConstant uc=null;
		switch(s.curToken.kind){
			case nameToken:
			uc=NamedConst.parse(s);
			break;
			case intValToken:
			uc=NumberLiteral.parse(s);
			break;
			case charValToken:
			uc=CharLiteral.parse(s);
			break;
			default:
			Main.error("unsigned constant: not a valid token: " + s.curToken.kind);
			break;
		}
		leaveParser("unsigned constant");
		return uc;
	}
}