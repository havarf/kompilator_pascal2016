package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

abstract class Factor extends PascalSyntax{
	types.Type type;
	int iValue;
	char cValue;
	boolean bValue;
	Factor(int lNum){
		super(lNum);
	}	
	static Factor parse(Scanner s){
		enterParser("factor");
		Factor f = null;
		switch(s.curToken.kind){
			case notToken:
			f = Negation.parse(s);
			break;
			case leftParToken:
			f = InnerExpr.parse(s);
			break;
			case intValToken:
			case charValToken:
			f=UnsignedConstant.parse(s);
			break;
			case nameToken:
			switch(s.nextToken.kind){
				case leftBracketToken:
				f=Variable.parse(s);
				break;
				case leftParToken:
				f=FuncCall.parse(s);
				break;
				default:
				f=Variable.parse(s);
				break;
			}
			break;
			default:
			Main.error("Error on line: " + s.curLineNum() + " : Factor: not a valid token: " + s.curToken.kind);
			break;
		}
		leaveParser("factor");
		return f;
	}
}
