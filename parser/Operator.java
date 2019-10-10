package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

abstract class Operator extends PascalSyntax{
	Operator(int lNum){
		super(lNum);
	}

	static Operator parse(Scanner s){
		enterParser("operator");
		Operator o = null;
		switch (s.curToken.kind){
			case addToken:
			case subtractToken:
			o=PrefixOperator.parse(s);
			break;
			//case addToken:
			//case subtractToken:
			case orToken:
			o=TermOperator.parse(s);
			break;
			case equalToken:
			case notEqualToken:
			case lessToken:
			case greaterToken:
			case greaterEqualToken:
			case lessEqualToken:
			o=RelOperator.parse(s);
			break;
			case multiplyToken:
			case divToken:
			case modToken:
			case andToken:
			o=FactorOperator.parse(s);
			break;
			default:
			Main.error("Operator: no valid TokenKind: " + s.curToken.kind);
		}

		leaveParser("operator");
		return o;
	}
}