package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
/* <Expression> ::= <simple expr> <rel opr> <simple expr> */
class Expression extends PascalSyntax{
	SimpleExpr se; 
	RelOperator ro;	
	SimpleExpr se2; 
	types.Type type;
	Expression(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<expression> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		if(ro!=null){
			se.genCode(f);
			f.genInstr("", "pushl", "%eax", "");
			se2.genCode(f);
			f.genInstr("", "popl", "%ecx", "");
			f.genInstr("", "cmpl", "%eax,%ecx", "");
			f.genInstr("", "movl", "$0,%eax", "");
			switch(ro.type){
				case "=":
				f.genInstr("","sete","%al","Test =");
				break;
				case "<>":
				f.genInstr("","setne","%al","Test <>");
				break;
				case "<":
				f.genInstr("","setl","%al","Test <");
				break;
				case "<=":
				f.genInstr("","setle","%al","Test <=");
				break;
				case ">":
				f.genInstr("","setg","%al","Test >");
				break;
				case ">=":
				f.genInstr("","setge","%al","Test >=");
				break;
			}
		}
		else{
			se.genCode(f);
		}
	}
	
	@Override void prettyPrint(){
		se.prettyPrint();
		if(ro!=null){
			ro.prettyPrint();
			se2.prettyPrint();
		}
	}

	@Override void check(Block curScope, Library lib){
		se.check(curScope, lib);
		type=se.type;
		if(se2 != null){
			se2.check(curScope, lib);
			String oprName = ro.type;
			se.type.checkType(se2.type, oprName+" operands",
			 this, "Operands to " +oprName + " are of different type!");
			type=lib.booleanType;
		}		
	}

	static Expression parse(Scanner s){
		enterParser("expression");
		Expression e = new Expression(s.curLineNum());
		e.se=SimpleExpr.parse(s);

		if(s.curToken.kind.isRelOpr()){
			e.ro=RelOperator.parse(s);
			e.se2=SimpleExpr.parse(s);
		}
		leaveParser("expression");
		return e;
	}
}