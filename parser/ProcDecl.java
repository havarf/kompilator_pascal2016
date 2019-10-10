package parser;
 
import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class ProcDecl extends PascalDecl{
	ParamDeclList pdl;
	Block b;
	//int lvl;

	ProcDecl(String id, int lNum){
		super(id, lNum);
	}	
	@Override public String identify(){
		if(lineNum==-1){
			return "<proc decl> " + name + " in the library";
		}
		return "<proc decl> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		int of=8;
		if(pdl!=null){
			for(int i=0; i < pdl.pdArray.size(); i++){
				pdl.pdArray.get(i).declOffset=of;
				pdl.pdArray.get(i).declLevel=declLevel;
				of+=4;
			}
		}
		String labelN = f.getLabel(name);
		progProcFuncName=labelN;
		b.outer=this;
		b.genCode(f);
		f.genInstr("", "leave", "", "End of " + name);
		f.genInstr("", "ret", "", "");
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrintLn();
		Main.log.prettyPrint("procedure " + name);
		if(pdl!=null){
			pdl.prettyPrint();
		}

		Main.log.prettyPrintLn(";");
		b.prettyPrint();
		Main.log.prettyPrintLn("; {"+name+"}");
		Main.log.prettyPrintLn();

	}
	@Override void check(Block curScope, Library lib) {
		curScope.addDecl(name, this);
		b.outerScope=curScope;
		if(pdl!=null){
			pdl.check(b, lib);
		}
		b.check(curScope, lib);
	}
	

	public static ProcDecl parse(Scanner s){
		enterParser("proc decl");
		s.skip(procedureToken);
		s.test(nameToken);
		ProcDecl pd = new ProcDecl(s.curToken.id, s.curLineNum());
		s.readNextToken();
		if(s.curToken.kind.equals(leftParToken)){
			pd.pdl=ParamDeclList.parse(s);
		}
		s.skip(semicolonToken);
		pd.b=Block.parse(s);
		s.skip(semicolonToken);
		leaveParser("proc decl");
		return pd;

	}
	public void checkWhetherAssignable(PascalSyntax where){
		where.error(name + "is a procedurem and cannot be assigned");
	}
    public void checkWhetherFunction(PascalSyntax where){
    	where.error(name + " is a procedure, not a function");
    }
    public void checkWhetherProcedure(PascalSyntax where){
    	//tom
    }
    public void checkWhetherValue(PascalSyntax where){
    	//usikker
    }
}