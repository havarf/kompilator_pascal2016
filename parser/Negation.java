package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class Negation extends Factor{
	Factor f;

	Negation(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<negation> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		this.f.genCode(f);
		f.genInstr("", "xorl", "$0,%eax", "not");
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("not ");
		f.prettyPrint();
	}

	@Override void check(Block curScope, Library lib) {
		f.check(curScope, lib);
		f.type.checkType(lib.booleanType, "'not' operand", this, 
			"negation not boolean");
		type=lib.booleanType;

	}

	public static Negation parse(Scanner s){
		enterParser("negation");
		Negation n = new Negation(s.curLineNum());
		s.skip(notToken);
		n.f=Factor.parse(s);
		leaveParser("negation");
		return n;
	}
}