package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class ParamDeclList extends PascalSyntax{
	ParamDecl pd;
	ArrayList<ParamDecl> pdArray = new ArrayList<ParamDecl>();

	ParamDeclList(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<param decl list> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(" (");
		for(int i=0; i<pdArray.size(); i++){
			pdArray.get(i).prettyPrint();
			if(i!=(pdArray.size()-1)){
				Main.log.prettyPrint("; ");
			}
		}
		Main.log.prettyPrint(")");
	}
	@Override void check(Block curScope, Library lib) {
		for(int i=0; i<pdArray.size(); i++){
			pdArray.get(i).check(curScope,lib);
		}
	}

	static ParamDeclList parse(Scanner s){
		enterParser("param decl list");
		ParamDeclList pdl = new ParamDeclList(s.curLineNum());
		s.skip(leftParToken);
		while(true){
			pdl.pd=ParamDecl.parse(s);
			pdl.pdArray.add(pdl.pd);
			if(s.curToken.kind.equals(semicolonToken)){
				s.skip(semicolonToken);
			}
			else{
				break;
			}
		}
		s.skip(rightParToken);
		leaveParser("param decl list");
		return pdl;
	}

}