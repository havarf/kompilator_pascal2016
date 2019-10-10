package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

abstract class Type extends PascalSyntax{
	types.Type type;

	Type(int lNum){
		super(lNum);
	}
		
	static Type parse(Scanner s){
		enterParser("type");
		Type t = null;
		switch(s.curToken.kind){
			case nameToken:
			t=TypeName.parse(s);
			break;
			case arrayToken:
			t=ArrayType.parse(s);
			break;
			default:
			Main.error("Type: Not a valid token: " + s.curToken.kind);
			break;
		}
		leaveParser("type");
		return t;
	}
}