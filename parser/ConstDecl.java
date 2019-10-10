package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class ConstDecl extends PascalDecl{
	Constant c;
	int iValue;

	ConstDecl(String id, int lNum){
		super(id, lNum);
	}

	@Override public String identify(){
		if(lineNum==-1){
			return "<const decl> " + name + " in the library";
		}
		return "<const decl> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "movl", "$"+iValue+",%eax", "	"+iValue);
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name + " = ");
		c.prettyPrint();
		Main.log.prettyPrintLn(";");
	}
	@Override void check(Block curScope, Library lib) {
		curScope.addDecl(name, this);
		c.check(curScope, lib);
		iValue=c.iValue;
		type=c.type;
	}
	

	static ConstDecl parse(Scanner s){
		enterParser("const decl");
		s.test(nameToken);
		ConstDecl cd = new ConstDecl(s.curToken.id, s.curLineNum());
		s.readNextToken();
		s.skip(equalToken);
		cd.c=Constant.parse(s);
		s.skip(semicolonToken);
		leaveParser("const decl");
		return cd;
	}
	
	public void checkWhetherAssignable(PascalSyntax where){
		where.error("You cannot assign to a constant");
	}
    public void checkWhetherFunction(PascalSyntax where){
    	where.error(name + " is a constant, not a function");
    }
    public void checkWhetherProcedure(PascalSyntax where){
    	where.error(name + " is a constant, not a procedure");
    }
    public void checkWhetherValue(PascalSyntax where){}
}