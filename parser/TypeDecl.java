package parser;

import main.*;
import scanner.*;

class TypeDecl extends PascalDecl{
	
	TypeDecl(String id, int lNum){
		super(id, lNum);
		switch(id){
			case "integer":
			type=new types.IntType();
			break;
			case "boolean":
			type=new types.BoolType();
			break;
			case "char":
			type=new types.CharType();
			break;
			default:
			Main.panic("TypeDecl: not a valid string: " + name);
		}
	}

	@Override public String identify(){
		return "<type decl> " + name + " in the library";
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){}

	@Override void check(Block curScope, Library lib){}



	public void checkWhetherAssignable(PascalSyntax where){}
    public void checkWhetherFunction(PascalSyntax where){}
    public void checkWhetherProcedure(PascalSyntax where){}
    public void checkWhetherValue(PascalSyntax where){}

}