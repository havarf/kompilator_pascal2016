package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;

class FuncCall extends Factor{
	String name;
	Expression e;
	ArrayList<Expression> eArray = new ArrayList<Expression>();
	FuncDecl funcRef;

	FuncCall(int lNum){
		super(lNum);
	}
	@Override public String identify(){
		return "<func call> on line " + lineNum;
	}
	@Override void genCode(CodeFile f) {
		for(int i = (eArray.size()-1); i>=0; i--){
			eArray.get(i).genCode(f);
			f.genInstr("", "pushl", "%eax", "Push param #" + (i+1));
		}
		f.genInstr("", "call", "func$"+funcRef.progProcFuncName, "");
		if(eArray.size()!=0){
			f.genInstr("", "addl", "$"+4*eArray.size()+",%esp", "Pop Param");
		}
	}
	
	@Override void prettyPrint(){
		Main.log.prettyPrint(name);
		if(e!=null){
			Main.log.prettyPrint("(");
			for(int i =0; i<eArray.size(); i++){
				eArray.get(i).prettyPrint();
				if(i!=(eArray.size()-1)){
					Main.log.prettyPrint(", ");
				}
			}
			Main.log.prettyPrint(")");
		}
	}

	@Override void check(Block curScope, Library lib) {
		PascalDecl pd = curScope.findDecl(name, this);
		funcRef=(FuncDecl) pd;
		funcRef.checkWhetherFunction(this);
		type=pd.type;
		
		for(int i = 0; i<eArray.size(); i++){
			Expression tmp = eArray.get(i);
			tmp.check(curScope, lib);
			try{
				if(eArray.size()<funcRef.pdl.pdArray.size()){
						error("Too few parameters in call on " + funcRef.name);
					}
				types.Type temp = funcRef.pdl.pdArray.get(i).type;
				tmp.type.checkType(temp, "param #" + (i+1), this, 
					"wrong type of parameters");
			}catch(IndexOutOfBoundsException e){
				error("Too many parameters in call on " + funcRef.name);
			}
		}
	}

	static FuncCall parse(Scanner s){
		enterParser("func call");
		FuncCall fc = new FuncCall(s.curLineNum());
		s.test(nameToken);
		fc.name=s.curToken.id;
		s.readNextToken();
		
		if(s.curToken.kind.equals(leftParToken)){
			s.skip(leftParToken);
		
			while (true){
				fc.e=Expression.parse(s);
				fc.eArray.add(fc.e);
				if(s.curToken.kind.equals(commaToken)){
					s.skip(commaToken);
				}
				else if(s.curToken.kind.equals(rightParToken)){
					s.skip(rightParToken);
					break;
				}else{
					Main.error("FuncCall: no valid token: " + s.curToken.kind);
				}
			}
		}
		leaveParser("func call");
		return fc;
	}
}