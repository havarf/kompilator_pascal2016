package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class VarDecl extends PascalDecl{
	Type t;

	VarDecl(String id, int lNum){
		super(id, lNum);
	}

	@Override public String identify(){
		return "<var decl> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "movl", -4*declLevel+"(%ebp),%edx", "");
		f.genInstr("", "movl", declOffset+"(%edx),%eax", "	"+name);
		
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name +": ");
		t.prettyPrint();
		Main.log.prettyPrintLn(";");
	}

	@Override void check(Block curScope, Library lib) {
		curScope.addDecl(name, this);
		t.check(curScope, lib);
		type=t.type;
	}

	public static VarDecl parse(Scanner s){
		enterParser("var decl");
		s.test(nameToken);
		VarDecl vd = new VarDecl(s.curToken.id, s.curLineNum());
		s.readNextToken();
		s.skip(colonToken);
		vd.t=Type.parse(s);
		s.skip(semicolonToken);
		leaveParser("var decl");
		return vd;

	}
	public void checkWhetherAssignable(PascalSyntax where){
		//denne er grei?
	}
    public void checkWhetherFunction(PascalSyntax where){
    	where.error(name + " is not a function");
    }
    public void checkWhetherProcedure(PascalSyntax where){
    	where.error(name + " is not a procedure");
    }
    public void checkWhetherValue(PascalSyntax where){
    	//denne er grei?
    }
}