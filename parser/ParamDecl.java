package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class ParamDecl extends PascalDecl{
	TypeName tn;
	int number;

	ParamDecl(String id, int lNum){
		super(id, lNum);
	}
	@Override public String identify(){
		return "<param decl> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "movl", -4*declLevel+"(%ebp),%edx ", "");
		f.genInstr("", "movl", declOffset+"(%edx),%eax", "	" + name);
	}						
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name+": ");
		tn.prettyPrint();

	}
	@Override void check(Block curScope, Library lib) {
		curScope.addDecl(name, this);
		tn.check(curScope, lib);
		type=tn.type;
	}

	public static ParamDecl parse(Scanner s){
		enterParser("param decl");
		s.test(nameToken);
		ParamDecl pd = new ParamDecl(s.curToken.id, s.curLineNum());
		s.readNextToken();
		s.skip(colonToken);
		pd.tn=TypeName.parse(s);
		leaveParser("param decl");
		return pd;
	}
	public void checkWhetherAssignable(PascalSyntax where){}
    public void checkWhetherFunction(PascalSyntax where){}
    public void checkWhetherProcedure(PascalSyntax where){}
    public void checkWhetherValue(PascalSyntax where){}
}