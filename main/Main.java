package main;

import parser.*;
import scanner.Scanner;
import static scanner.TokenKind.*;

import java.io.*;

public class Main {
    public static final String version = "2016-08-22";

    // Del 3: 
    public static parser.Library library;
    public static LogFile log = new LogFile();

    private static String sourceFileName, baseFileName;
    private static boolean testChecker = false, 
	testParser = false, testScanner = false;
    private static String OS;


    public static void main(String arg[]) {
	OS = System.getProperty("os.name");
	System.out.println("This is the Ifi Pascal2016 compiler (" +
	    version + ") running on " + OS);

	int exitStatus = 0;
	try {
	    readArgs(arg);
	    log.init(baseFileName + ".log");

	    Scanner s = new Scanner(sourceFileName);
	    if (testScanner) 
		doTestScanner(s);
	    // Del 2: 
	    else if (testParser)
	         doTestParser(s);
	    // Del 3:
	    else if (testChecker)
	         doTestChecker(s);
	    // Del 4:
	    else
	        doRunRealCompiler(s);
	} catch (PascalError e) {
	    System.out.println();
	    System.err.println(e.getMessage());
	    exitStatus = 1;
	} finally {
	    log.finish();
	}
	System.exit(exitStatus);
    }


    public static boolean useUnderscore() {
	// Should global names start with an '_'? Not with Linux/Unix.
	return ! OS.matches(".*n.*x.*");
    }


    private static void readArgs(String arg[]) {
	for (int i = 0;  i < arg.length;  i++) {
	    String a = arg[i];

	    if (a.equals("-logB")) {
		log.doLogBinding = true;
	    } else if (a.equals("-logP")) {
		log.doLogParser = true;
	    } else if (a.equals("-logS")) {
		log.doLogScanner = true;
	    } else if (a.equals("-logT")) {
		log.doLogTypeChecks = true;
	    } else if (a.equals("-logY")) {
		log.doLogPrettyPrint = true;
	    } else if (a.equals("-testchecker")) {
		testChecker = log.doLogBinding = log.doLogTypeChecks = true;
	    } else if (a.equals("-testparser")) {
		testParser = log.doLogParser = log.doLogPrettyPrint = true; 
	    } else if (a.equals("-testscanner")) {
		testScanner = log.doLogScanner = true; 
	    } else if (a.startsWith("-")) {
		warning("Warning: Unknown option " + a + " ignored.");
	    } else if (sourceFileName != null) {
		usage();
	    } else {
		sourceFileName = a;
	    }
	}
	if (sourceFileName == null) usage();
	
	baseFileName = sourceFileName;
	if (baseFileName.length()>4 && baseFileName.endsWith(".pas"))
	    baseFileName = baseFileName.substring(0,baseFileName.length()-4);
    }


    private static void doTestScanner(Scanner s) {
	while (s.nextToken.kind != eofToken)
	    s.readNextToken();
    }


    //Del 2:
    private static void doTestParser(Scanner s) {
	Program prog = Program.parse(s);
	if (s.curToken.kind != eofToken) 
	    error("Scanner error: Garbage after the program!");

	prog.prettyPrint();
    }
    


    // Del 3:
    private static void doTestChecker(Scanner s) {
	Program prog = Program.parse(s);
	if (s.curToken.kind != eofToken) 
	    error("Scanner error: Garbage after the program!");
	if (log.doLogPrettyPrint)
	    prog.prettyPrint();
	
	library = new Library();
	prog.check(library, library);
    }
    


    // Del 4:
    private static void doRunRealCompiler(Scanner s) {
	System.out.print("Parsing...");
	Program prog = Program.parse(s);
	if (s.curToken.kind != eofToken) 
	    error("Scanner error: Garbage after the program!");

	if (log.doLogPrettyPrint)
	    prog.prettyPrint();
	
	System.out.print(" checking...");
	library = new Library();
	prog.check(library, library);

	System.out.print(" generating code...");
	CodeFile code = new CodeFile(baseFileName+".s");
	library.genCode(code);  prog.genCode(code);
	code.finish();
	System.out.println("OK");

	assembleCode();
    }
    


    private static void assembleCode() {
	String pName = baseFileName;
	String sName = baseFileName + ".s";

	String cmd[] = new String[8];
	cmd[0] = "gcc";  cmd[1] = "-m32";
	cmd[2] = "-o";   cmd[3] = pName;
	cmd[4] = sName;  
	cmd[5] = "-L.";  cmd[6] = "-L/hom/inf2100";  cmd[7] = "-lpas2016";  

	System.out.print("Running");
	for (String s: cmd) {
	    if (s.contains(" "))
		System.out.print(" '" + s + "'");
	    else
		System.out.print(" " + s);
	}
	System.out.println();

	try {
	    String line;
	    Process p = Runtime.getRuntime().exec(cmd);

	    // Print any output from the assembly process:
	    BufferedReader out = new BufferedReader
		(new InputStreamReader(p.getInputStream()));
	    BufferedReader err = new BufferedReader
		(new InputStreamReader(p.getErrorStream()));

	    while ((line = out.readLine()) != null) {
		System.out.println(line);
	    }
	    while ((line = err.readLine()) != null) {
		System.out.println(line);
	    }
	    out.close();  err.close();
	    p.waitFor();
	} catch (Exception err) {
	    error("Assembly errors detected.");
	}
    }


    // Error message utilities:

    public static void error(String message) {
	log.noteError(message);
	throw new PascalError(message);
    }
	
    public static void error(int lineNum, String message) {
	error("Error in " +
	      (lineNum<0 ? "last line" : "line "+lineNum) + 
	      ": " + message);
    }

    private static void usage() {
	error("Usage: java -jar pascal2016.jar " +
	    "[-log{B|P|S|T|Y}] [-test{checker|parser|scanner}] file");
    }

    public static void panic(String where) {
	error("PANIC! Programming error in " + where);
    }

    public static void warning(String message) {
	log.noteError(message);
	System.err.println(message);
    }
}
