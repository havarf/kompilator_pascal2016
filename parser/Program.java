//hentet i sin helhet fra foilene
package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;
/* <Program> ::= 'program' <name> ';' <block> '.'*/
public class Program extends PascalDecl{
	Block progBlock;
	String labelName;

	Program(String id, int lNum){
		super(id, lNum);
	}
	@Override public String identify(){
		return "<program> "+ name + " on line " + lineNum;
	}
	@Override public void genCode(CodeFile f) {
		labelName = f.getLabel(name);
		declLevel=1;
		f.genInstr("", ".globl", "main", "");
		f.genInstr("main", "", "", "");
		f.genInstr("", "call", "prog$"+labelName, "Start program");
		f.genInstr("", "movl", "$0,%eax", "Set status 0 and");
		f.genInstr("", "ret", "", "terminate the program");
		progBlock.prog=this;
		progBlock.genCode(f);
		f.genInstr("", "leave", "", "End of " + name);
		f.genInstr("", "ret", "", "");

	}

	@Override public void prettyPrint(){
		Main.log.prettyPrintLn("program " + name + ";");
		progBlock.prettyPrint();
		Main.log.prettyPrint(".");
	}
	@Override public void check(Block curScope, Library lib) {
		progBlock.outerScope=curScope;
		progBlock.check(curScope, lib);

	}

	public static Program parse(Scanner s){
		enterParser("program");
		s.skip(programToken);
		s.test(nameToken);
		Program p = new Program(s.curToken.id, s.curLineNum());
		s.readNextToken();
		s.skip(semicolonToken);
		p.progBlock = Block.parse(s);	
		s.skip(dotToken);
		leaveParser("program");
		return p;
	}




	public void checkWhetherAssignable(PascalSyntax where){}
    public void checkWhetherFunction(PascalSyntax where){}
    public void checkWhetherProcedure(PascalSyntax where){}
    public void checkWhetherValue(PascalSyntax where){}
}