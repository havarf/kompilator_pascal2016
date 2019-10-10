package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class SimpleExpr extends PascalSyntax{
	PrefixOperator po;
	TermOperator to;
	Term t;
	ArrayList<Term> tArray = new ArrayList<Term>();
	ArrayList<TermOperator> toArray = new ArrayList<TermOperator>();
	types.Type type;

	SimpleExpr(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<simple expr> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		tArray.get(0).genCode(f);
		if(po!=null){
			if(po.type.equals("-")){
				f.genInstr("", "negl", "%eax", " - (prefix)");
			}
		}
		for(int i = 0; i <tArray.size(); i++){			
			if(i<toArray.size()){
				TermOperator teop = toArray.get(i);
				Term right = tArray.get(i+1);
				f.genInstr("", "pushl", "%eax", "");
				right.genCode(f);
				switch(teop.type){
					case "+":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("" , "addl", "%ecx,%eax", "	+");
					break;
					case "-":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "subl", "%ecx,%eax", "	-");
					break;
					case "or":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "orl", "%ecx,%eax", "	or");
					break;
				}
			}
		}
	}
	
	@Override void prettyPrint(){
		if(po!=null){
			po.prettyPrint();
		}
		for(int i =0; i <toArray.size(); i++){
			tArray.get(i).prettyPrint();
			toArray.get(i).prettyPrint();
		}
		tArray.get(toArray.size()).prettyPrint();
	}

	@Override void check(Block curScope, Library lib) {
		if(po!=null){
			po.check(curScope, lib);
			tArray.get(0).check(curScope, lib);
			tArray.get(0).type.checkType(lib.integerType, "prefix " + po.type+" operand", this,
				"constant not integer");
		}

		tArray.get(0).check(curScope, lib);
		for(int i = 0; i <tArray.size(); i++){
			Term left = tArray.get(i);
			type=left.type;
			if(i<toArray.size()){
				TermOperator teop = toArray.get(i);
				Term right = tArray.get(i+1);
				right.check(curScope, lib);
				teop.left=left;
				teop.right=right;
				teop.check(curScope, lib);
			}
		}
	}

	static SimpleExpr parse(Scanner s){
		enterParser("simple expr");
		SimpleExpr se = new SimpleExpr(s.curLineNum());

		if(s.curToken.kind.isPrefixOpr()){
			se.po=PrefixOperator.parse(s);
		}
		while(true){
			se.t=Term.parse(s);
			se.tArray.add(se.t);
			if(s.curToken.kind.isTermOpr()){
				se.to=TermOperator.parse(s);
				se.toArray.add(se.to);
			}
			else{
				break;
			}
		}
		leaveParser("simple expr");
		return se;
	}
	
}