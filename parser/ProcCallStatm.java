package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class ProcCallStatm extends Statement{
	private String name;
	Expression e;
	ArrayList<Expression> eArray = new ArrayList<Expression>();
	ProcDecl procRef;

	ProcCallStatm(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<proc call> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		if(procRef.lineNum==-1){
			for(int i=0; i<eArray.size(); i++){
				eArray.get(i).genCode(f);
				f.genInstr("", "pushl", "%eax", "Push next param");
				if(eArray.get(i).se.t.f.type instanceof types.BoolType){
					f.genInstr("", "call", "write_bool", "");
				}
				else if(eArray.get(i).se.t.f.type instanceof types.IntType){
					f.genInstr("", "call", "write_int", "");
				}
				else if(eArray.get(i).se.t.f.type instanceof types.CharType){
					f.genInstr("", "call", "write_char", "");
				}
				else{
					Main.panic("ProcCallStatm else");
				}
				f.genInstr("", "addl", "$4,%esp", "Pop param");
			}
		}else{
			for(int i = eArray.size()-1; i>=0; i--){
				eArray.get(i).genCode(f);
				f.genInstr("", "pushl", "%eax", " Push param #"+(i+1));
			}
			f.genInstr("", "call", "proc$"+procRef.progProcFuncName, "");
			if(eArray.size()!=0){
				f.genInstr("", "addl", "$"+4*eArray.size()+",%esp", "	Pop param ");
			}
			
		}
	}
	@Override void prettyPrint(){
		Main.log.prettyPrint(name);
		if(e!=null){
			Main.log.prettyPrint("(");
			for(int i=0; i<eArray.size(); i++){
				eArray.get(i).prettyPrint();
				if(i!=(eArray.size()-1)){
					Main.log.prettyPrint(", ");
				}
			}
			Main.log.prettyPrint(")");
		}
	}

	@Override void check(Block curScope, Library lib){
		PascalDecl d = curScope.findDecl(name, this);
		procRef = (ProcDecl)d; 
		procRef.checkWhetherProcedure(this); 
		
		for(int i = 0; i < eArray.size(); i++){
			if(procRef.lineNum!=-1){
				Expression tmp = eArray.get(i); 
				tmp.check(curScope, lib);
				try{
					if(eArray.size()<procRef.pdl.pdArray.size()){
						error("Too few parameters in call on " + procRef.name);
					}
					types.Type temp = procRef.pdl.pdArray.get(i).type;
					tmp.type.checkType(temp,"param #" + (i+1), this, 
						"wrong type of parameters");
				}catch(IndexOutOfBoundsException e){
					error("Too many parameters in call on " + procRef.name);
				}
			}else{
				eArray.get(i).check(curScope,lib);
			}
		} 
	}

	public static ProcCallStatm parse(Scanner s){
		enterParser("proc call");
		ProcCallStatm pcs = new ProcCallStatm(s.curLineNum());
		s.test(nameToken);
		pcs.name=s.curToken.id;
		s.readNextToken();
		if(s.curToken.kind.equals(leftParToken)){
			s.skip(leftParToken);
			pcs.e=Expression.parse(s);
			pcs.eArray.add(pcs.e);
			while(s.curToken.kind.equals(commaToken)){
				s.skip(commaToken);
				pcs.e=Expression.parse(s);
				pcs.eArray.add(pcs.e);
			}
			s.skip(rightParToken);
		}
		leaveParser("proc call");
		return pcs;
	}
}