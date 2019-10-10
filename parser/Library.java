package parser; 

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import types.*;

public class Library extends Block{
	//legger inn i decls
	public types.IntType integerType;
	public types.CharType charType;
	public types.ArrayType arrayType;
	public types.BoolType booleanType; 

	public Library(){
		super(-1); 
		addDecl("write", new ProcDecl("write", -1));
		addDecl("integer", new TypeDecl("integer", -1));
		addDecl("boolean", new TypeDecl("boolean", -1));
		addDecl("char", new TypeDecl("char", -1));
		
		integerType = (types.IntType)decls.get("integer").type;
		charType = (types.CharType)decls.get("char").type;
		booleanType = (types.BoolType)decls.get("boolean").type;

		ConstDecl f = new ConstDecl("false", -1);
		f.type=booleanType;
		f.iValue=0;
		ConstDecl t = new ConstDecl("true", -1);
		t.type=booleanType;
		t.iValue=1;
		addDecl("false", f);
		addDecl("true", t);
		ConstDecl eol= new ConstDecl("eol", -1);
		eol.type=charType;
		eol.iValue=10;
		addDecl("eol", eol);
	}
	@Override public void genCode(CodeFile f) {}
}