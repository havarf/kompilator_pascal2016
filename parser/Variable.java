package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class Variable extends Factor{
	String name;
	Expression e;
	
	PascalDecl ref;
	VarDecl varRef;
	FuncDecl funcRef;
	ParamDecl parRef;
	ConstDecl consRef;


	Variable(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<variable> " + name + " on line " + lineNum;
	}
	@Override void genCode(CodeFile f){
		if(varRef!=null){
			//varDecl
			varRef.genCode(f);
		}
		if(funcRef!=null){
			//funcDecl
			funcRef.genCode(f);
		}
		if(parRef!=null){
			//paramDecl
			parRef.genCode(f);
		}
		if(consRef!=null){
			//constDecl
			consRef.genCode(f);
		}
		if(e!=null){
			e.genCode(f);
		}

	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name);
		if(e!=null){
			Main.log.prettyPrint("[");
			e.prettyPrint();
			Main.log.prettyPrint("]");
		}
	}

	@Override void check(Block curScope, Library lib) {
		PascalDecl pd = curScope.findDecl(name, this);
		type = pd.type;
		if(pd instanceof VarDecl){
			varRef = (VarDecl) pd;
			ref=varRef;
			
		}
		else if(pd instanceof FuncDecl){
			funcRef = (FuncDecl) pd;
			ref=funcRef;
		}
		else if(pd instanceof ParamDecl){
			parRef = (ParamDecl) pd;
			ref=parRef;
		}
		else if(pd instanceof ConstDecl){
			consRef = (ConstDecl) pd;
			ref=consRef;
			iValue=consRef.iValue;
		}else{
			Main.panic("Variable: instanceof not valid");
		}
		
		if(e!=null){
			e.check(curScope, lib);
			e.type.checkType(lib.integerType, "array index", this, 
				"array index not the same");
		}
	}
	
	public static Variable parse(Scanner s){
		enterParser("variable");
		Variable v = new Variable(s.curLineNum());
		s.test(nameToken);
		v.name=s.curToken.id;
		s.readNextToken();
		if(s.curToken.kind.equals(leftBracketToken)){
			s.skip(leftBracketToken);
			v.e=Expression.parse(s);
			s.skip(rightBracketToken);
		}
		leaveParser("variable");
		return v;
	}

}