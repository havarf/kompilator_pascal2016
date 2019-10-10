package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class VarDeclPart extends PascalSyntax{
	VarDecl vd;
	ArrayList<VarDecl> vdArray = new ArrayList<VarDecl>();

	VarDeclPart(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<var decl part> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrintLn("var "); Main.log.prettyIndent();
		for(int i=0; i < vdArray.size(); i++){
			vdArray.get(i).prettyPrint();
		}
		Main.log.prettyOutdent();
	}

	@Override void check(Block curScope, Library lib) {
		for(int i=0; i < vdArray.size(); i++){
			vdArray.get(i).check(curScope, lib);
		}
	}

	public static VarDeclPart parse(Scanner s){
		enterParser("var decl part");

		VarDeclPart vdp = new VarDeclPart(s.curLineNum());
		s.skip(varToken);

		while(s.curToken.kind.equals(nameToken)){
			vdp.vd=VarDecl.parse(s);
			vdp.vdArray.add(vdp.vd);
		}
		leaveParser("var decl part");
		return vdp;

	}
}