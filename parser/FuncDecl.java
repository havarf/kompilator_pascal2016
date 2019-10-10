package parser; 

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class FuncDecl extends ProcDecl{
	ParamDeclList pdl;
	TypeName tn;
	Block b; 
	

	FuncDecl(String id, int lNum){
		super(id, lNum);
	}
	@Override public String identify(){
		return "<func decl> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		int of=8;
		if(pdl!=null){
			for(int i=0; i<pdl.pdArray.size(); i++){
				pdl.pdArray.get(i).declOffset=of;
				pdl.pdArray.get(i).declLevel=declLevel;
				of+=4;
			}
		}
		String labelN = f.getLabel(name);
		progProcFuncName=labelN;
		b.outer=this;
		b.genCode(f);
		f.genInstr("", "movl", "-32(%ebp),%eax", "Fetch return value");
		f.genInstr("", "leave", "", "End of " + name);
		f.genInstr("", "ret", "", "");
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("function " + name);
		if(pdl!=null){
			pdl.prettyPrint();
		}
		Main.log.prettyPrint(": ");
		tn.prettyPrint();
		Main.log.prettyPrintLn("; ");
		b.prettyPrint();
		Main.log.prettyPrintLn("; {" + name +  "}");
		Main.log.prettyPrintLn();

	}

	@Override void check(Block curScope, Library lib) {
		curScope.addDecl(name, this);
		b.outerScope=curScope;		

		if(pdl!=null){
			pdl.check(b, lib);
		}
		tn.check(b, lib);
		type=tn.type;
		b.check(curScope, lib);
	}
	
	public static FuncDecl parse(Scanner s){
		enterParser("func decl");
		s.skip(functionToken);
		s.test(nameToken);
		FuncDecl fd = new FuncDecl(s.curToken.id, s.curLineNum());
		s.readNextToken();
		if(s.curToken.kind.equals(leftParToken)){
			fd.pdl=ParamDeclList.parse(s);
		}
		s.skip(colonToken);
		fd.tn=TypeName.parse(s);
		s.skip(semicolonToken);
		fd.b= Block.parse(s);
		s.skip(semicolonToken);
		leaveParser("func decl");
		return fd;
	}

	public void checkWhetherAssignable(PascalSyntax where){}
    public void checkWhetherFunction(PascalSyntax where){}
    public void checkWhetherProcedure(PascalSyntax where){
    	where.error(name+ " is a function, not a procedure");
    }
    public void checkWhetherValue(PascalSyntax where){}
}