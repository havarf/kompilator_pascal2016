package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;


class ConstDeclPart extends PascalSyntax{
	ConstDecl cd;
	ArrayList<ConstDecl> cdArray = new ArrayList<ConstDecl>();

	ConstDeclPart(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<const decl part> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrintLn("const "); Main.log.prettyIndent();
		for(int i =0; i<cdArray.size(); i++){
			cdArray.get(i).prettyPrint();
		}
		Main.log.prettyOutdent();
	}
	@Override void check(Block curScope, Library lib) {
		for(int i =0; i < cdArray.size(); i++){
			cdArray.get(i).check(curScope, lib);
		}
	}

	static ConstDeclPart parse(Scanner s){
		enterParser("const decl part");

		ConstDeclPart cdp = new ConstDeclPart(s.curLineNum());
		s.skip(constToken);

		while(s.curToken.kind.equals(nameToken)){
			cdp.cd=ConstDecl.parse(s);
			cdp.cdArray.add(cdp.cd);
		}
		leaveParser("const decl part");
		return cdp;
	}
}