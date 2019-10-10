package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class Term extends PascalSyntax{
	Factor f;
	ArrayList<Factor> fArray = new ArrayList<Factor>();
	FactorOperator fo;
	ArrayList<FactorOperator> foArray = new ArrayList<FactorOperator>();
	types.Type type;

	Term(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<term> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
			fArray.get(0).genCode(f);
		for(int i= 0; i <fArray.size(); i++){
			if(i<foArray.size()){
				FactorOperator fao = foArray.get(i);
				Factor r = fArray.get(i+1);
				f.genInstr("", "pushl", "%eax", "");
				r.genCode(f);
				switch(fao.type){
					case "*":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "imull", "%ecx,%eax", "	*");
					break;
					case "div":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "cdq", "", "");
					f.genInstr("", "idivl", "%ecx", "	/");
					break;
					case "mod":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "cdq", "", "");
					 f.genInstr("", "idivl", "%ecx", "");
					f.genInstr("", "movl", "%edx,%eax", "	mod");   
					break;
					case "and":
					f.genInstr("", "movl", "%eax,%ecx", "");
					f.genInstr("", "popl", "%eax", "");
					f.genInstr("", "andl", "%ecx,%eax", "	and");
					break;
				}
			}
		}
	}
	
	@Override void prettyPrint(){
		for(int i=0; i<foArray.size();i++){
			fArray.get(i).prettyPrint();
			foArray.get(i).prettyPrint();
		}
		fArray.get(foArray.size()).prettyPrint();
	}

	@Override void check(Block curScope, Library lib) {
		fArray.get(0).check(curScope, lib);
		for(int i=0; i <fArray.size();i++){
			Factor l = fArray.get(i);
			type=l.type;
			if(i<foArray.size()){
				Factor r = fArray.get(i+1);
				r.check(curScope, lib);
				FactorOperator fao = foArray.get(i);
				fao.left=l;
				fao.right=r;
				fao.check(curScope, lib);
				//i++;

			}
		}
	}

	static Term parse(Scanner s){
		enterParser("term");
		Term t= new Term(s.curLineNum());
		while(true){
			t.f=Factor.parse(s);
			t.fArray.add(t.f);
			if(s.curToken.kind.isFactorOpr()){
				t.fo=FactorOperator.parse(s);
				t.foArray.add(t.fo);
			}
			else{
				break;
			}
		}
		leaveParser("term");
		return t;
	}
}