package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

/* <if-statm> ::= 'if' <expression> 'then' <statement> 'else' <statement> */
class IfStatm extends Statement{
	//if
	Expression expr;
	//then
	Statement toDo;
	//else
	Statement elseTodo;

	IfStatm(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<if-statm> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		f.genInstr("", "", "", "start if-statement");
		String testLabel = f.getLocalLabel();
		expr.genCode(f);

		f.genInstr("", "cmpl", "$0,%eax", "");
		f.genInstr("", "je", testLabel, "");

		toDo.genCode(f);
		if(elseTodo != null){
			String elselabel = f.getLocalLabel();
			f.genInstr("", "jmp", elselabel, "");
			f.genInstr(testLabel, "", "", "");

			elseTodo.genCode(f);
			f.genInstr(elselabel, "", "", "end if-statement");
		}
		else{
			f.genInstr(testLabel, "", "", "end if-statement");
		}

	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint("if ");
		expr.prettyPrint();
		Main.log.prettyPrintLn(" then"); Main.log.prettyIndent();
		toDo.prettyPrint();
		Main.log.prettyOutdent();
		if(elseTodo!=null){
			Main.log.prettyPrintLn();
			Main.log.prettyPrintLn("else"); Main.log.prettyIndent();
			elseTodo.prettyPrint(); Main.log.prettyOutdent();
		}
	}

	@Override void check(Block curScope, Library lib) {
		expr.check(curScope, lib);
		if(expr.type instanceof types.ArrayType){
			expr.type.typeOf().checkType(lib.booleanType, "if-test", this,
           		"if-test is not Boolean");
		}
		else{
			expr.type.checkType(lib.booleanType, "if-test", this,
           		"if-test is not Boolean");
		}
		toDo.check(curScope, lib);
		if(elseTodo!=null){
			elseTodo.check(curScope, lib);
		}
	}


	static IfStatm parse(Scanner s){
		enterParser("if-statm");

		IfStatm is=new IfStatm(s.curLineNum());
		s.skip(ifToken);

		is.expr=Expression.parse(s);
		s.skip(thenToken);
		is.toDo=Statement.parse(s);
		//else?
		if(s.curToken.kind.equals(elseToken)){
			//else!
			s.skip(elseToken);
			is.elseTodo=Statement.parse(s);
		}
		leaveParser("if-statm");
		return is;
	}
	
}