package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class TypeName extends Type{
	private String name;
	TypeDecl ref;
	TypeName(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<type name> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name);
	}

	@Override void check(Block curScope, Library lib) {
		PascalDecl pd = curScope.findDecl(name, this);
		ref = (TypeDecl)pd;
		type = ref.type;		
	}
	
	static TypeName parse(Scanner s){
		enterParser("type name");
		TypeName tn = new TypeName(s.curLineNum());
		s.test(nameToken);
		tn.name=s.curToken.id;
		s.readNextToken();
		leaveParser("type name");
		return tn;
	}


}