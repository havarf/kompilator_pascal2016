package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
import java.util.ArrayList;
import java.util.HashMap;


class Block extends PascalSyntax{
	ConstDeclPart cdp;
	VarDeclPart vdp;
	FuncDecl fd;
	ProcDecl pd;
	ArrayList<PascalDecl> fdpdArray = new ArrayList<PascalDecl>();
	StatmList sl;
	HashMap<String, PascalDecl> decls = new HashMap<String, PascalDecl>();
	Block outerScope;
	//int lvl;
	//int offset;
	String progName;
	Program prog;
	PascalDecl outer;

	public Block(int lNum){
		super(lNum);
	}

	@Override public String identify(){
		return "<block> on line " + lineNum;
	}

	@Override public void genCode(CodeFile f) {
		int enterValue=32;
		if(cdp != null){
			cdp.genCode(f);
		}
		if(vdp != null){
			enterValue = 32 + (4 * vdp.vdArray.size());
			int offset=-32;
			int lvl;
			for(int i =0; i<vdp.vdArray.size(); i++){
				offset-=4;
				vdp.vdArray.get(i).declOffset=offset;
				if(prog!=null){
					lvl=prog.declLevel;
				}
				else{
					lvl=outer.declLevel;
				}
				vdp.vdArray.get(i).declLevel=lvl;
			}
			vdp.genCode(f);
		}
		int lvl;
		int offset=4;
		for(int i = 0; i<fdpdArray.size(); i++){
			offset+=4;
			if(prog!=null){
					lvl=prog.declLevel;
				}
				else{
					lvl=outer.declLevel;
				}
			fdpdArray.get(i).declLevel=lvl+1;
			fdpdArray.get(i).declOffset=offset;
			fdpdArray.get(i).genCode(f);
		}
		if(prog!=null){
			progName=prog.labelName;
			f.genInstr("prog$"+ progName, "","","");
			f.genInstr("", "enter", "$"+enterValue+",$1", "Start of "+ prog.name);
		}else if(outer instanceof FuncDecl){
			FuncDecl outerF = (FuncDecl) outer;	
			outer.declOffset=-32;
			f.genInstr("func$"+ outerF.progProcFuncName, "", "", "");
			f.genInstr("","enter", "$"+enterValue+",$"+outerF.declLevel, "start of " + outerF.name);
		}
		else if(outer instanceof ProcDecl){
			ProcDecl outerP = (ProcDecl) outer;
			f.genInstr("proc$"+ outerP.progProcFuncName, "", "", "");
			f.genInstr("","enter", "$"+enterValue+",$"+outerP.declLevel, "start of " + outerP.name);
			
		}
		sl.genCode(f);
	}

	@Override void prettyPrint(){
		if(cdp != null){
			cdp.prettyPrint();
		}
		if(vdp != null){
			vdp.prettyPrint();
		}
		for(int i=0; i<fdpdArray.size(); i++){
			fdpdArray.get(i).prettyPrint();
		}
		Main.log.prettyPrintLn("begin"); Main.log.prettyIndent();
		sl.prettyPrint(); Main.log.prettyOutdent();
		Main.log.prettyPrint("end"); 

	}
	/////////////////////////////
	void addDecl(String id, PascalDecl d){
		if(decls.containsKey(id)){
			d.error(id + " declared twice in same block!");
		}
		decls.put(id, d);
	}
	@Override void check(Block curScope, Library lib){
		//outerScope=curScope;
		if(cdp != null){
			cdp.check(this, lib);
		}
		if(vdp != null){
			vdp.check(this, lib);
		}
		for(int i=0; i<fdpdArray.size(); i++){
			fdpdArray.get(i).check(this, lib);
		}
		sl.check(this, lib);
	}
	PascalDecl findDecl(String id, PascalSyntax where){
		PascalDecl d = decls.get(id);
		if(d != null){
			Main.log.noteBinding(id, where, d);
			return d;
		}

		if (outerScope != null){
			return outerScope.findDecl(id, where);
		}

		where.error("Name " + id + " is unknown!");
		return null;
	}
	/////////////////////////////

	static Block parse(Scanner s){
		enterParser("block");
		Block b = new Block(s.curLineNum());
		
		if(s.curToken.kind.equals(constToken)){
			b.cdp=ConstDeclPart.parse(s);
		}
		if(s.curToken.kind.equals(varToken)){
			b.vdp=VarDeclPart.parse(s);
		}
		while(true){
			if(s.curToken.kind.equals(functionToken)){
				b.fd=FuncDecl.parse(s);
				b.fdpdArray.add(b.fd);
			}
			else if(s.curToken.kind.equals(procedureToken)){
				b.pd=ProcDecl.parse(s);
				b.fdpdArray.add(b.pd);
			}
			else{
				break;
			}
		}
		s.skip(beginToken);
		b.sl=StatmList.parse(s);
		s.skip(endToken);
		leaveParser("block");
		return b;


	}

}