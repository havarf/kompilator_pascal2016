package	parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
/* <Assign-statm> ::= <variable> ':=' <expression> */
public class AssignStatm extends Statement{
	Variable var;
	Expression expr;
	

	AssignStatm(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<assign statm> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		expr.genCode(f);
		f.genInstr("", "movl", -4*var.ref.declLevel+"(%ebp),%edx", "");
		f.genInstr("", "movl", "%eax,"+var.ref.declOffset+"(%edx)", var.name+" :=");
	}
	@Override void prettyPrint(){
		var.prettyPrint();
		Main.log.prettyPrint(" := ");
		expr.prettyPrint();
	}
	@Override void check(Block curScope, Library lib){
		var.check(curScope, lib);
		var.ref.checkWhetherAssignable(this);
		expr.check(curScope, lib);
		if(var.type instanceof types.ArrayType){
			var.type.typeOf().checkType(expr.type, ":=", this,
				"Assign-test is not the same types");
		}else{
			var.type.checkType(expr.type, ":=", this,
        	  "Assign-test is not the same types");
		}
	}

	static AssignStatm parse(Scanner s){
		enterParser("assign statm");
		AssignStatm as=new AssignStatm(s.curLineNum());
		as.var=Variable.parse(s);
		s.skip(assignToken);
		as.expr=Expression.parse(s);
		leaveParser("assign statm");
		return as;
	}
}