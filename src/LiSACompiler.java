import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import j ava.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
public class LiSACompiler {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		// get file name from user
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter The name of the file: ");
		String file = sc.nextLine();

		// 1 LiSA
		// 2 RD_Parser
		// 3 Assembler/StackMachine


		LiSA lisaobj = new LiSA(file);

		RD_Parser rd = new RD_Parser(lisaobj);
		Assembler A = new Assembler(lisaobj);
	}

}

class LiSA {
	public List<Symbol> SymbolTable = new ArrayList<Symbol>();
	private List<Token> TokenList = new ArrayList<Token>();

	private BufferedReader br;
	private int lineNumber=0;

	private StringTokenizer st;

	private String inputFile = "input.txt";
	private String outputFile = "out.txt";

	//Token Index
	private int TI = 0;

	public LiSA(String input) throws FileNotFoundException, IOException{
		inputFile = input;
		lexer();
		fillSymbolTable();
		System.out.println("Lexical Analaysis done...");
	}

	private void fillSymbolTable() throws FileNotFoundException, IOException {
		String lexeme;
		String type;

		lineNumber=0;


		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
		    String line;
		    String token;
		    while ((line = br.readLine()) != null) {
		        // process the line.
		    	st = new StringTokenizer(line);
		    	lineNumber++;

		    	//fill symbol table
		    	while(st.hasMoreTokens()){
		    		token=st.nextToken();

		    		switch(token){
		    			case "var":
		    				lexeme=st.nextToken();
		    				st.nextToken();
		    				type=st.nextToken();
				    		SymbolTable.add(new Symbol(lexeme,type,0));
		    			case "%":while(st.hasMoreTokens()){st.nextToken();}break;

		    		}//#brackets4Dayz
		    	}
		    }
		}
	}

	private void printSymbolTable() {
		System.out.println("Symbol table");
		System.out.print("Lexeme"+"\t\t");
		System.out.print("Type"+"\t\t");
		System.out.println("Value"+"\t\t");
		for(int i =0; i<SymbolTable.size();i++){
			System.out.print(SymbolTable.get(i).getLexeme() + "\t\t");
			System.out.print(SymbolTable.get(i).getType() + "\t\t");
			System.out.println(SymbolTable.get(i).getValue() + "\t\t");
		}
	}

	private void lexer() throws FileNotFoundException, IOException {
		//create output file
		Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
		//how to use output.write("something");

		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
		    String line;
		    String token;
		    while ((line = br.readLine()) != null) {
		        // process the line.
		    	st = new StringTokenizer(line);
		    	lineNumber++;

		    	//lexeme token pairs and fill symbol table
		    	while(st.hasMoreTokens()){
		    		token=st.nextToken();
		    		switch(token){
		    			//Symbols and operators
		    			case "(": TokenList.add(new Token("(","LP",lineNumber) );break;
		    			case ")": TokenList.add(new Token(")","RP",lineNumber) );break;
		    			case ":=": TokenList.add(new Token(":=","ASGN",lineNumber) ); break;
		    			case ";": TokenList.add(new Token(";","SC",lineNumber) ); break;
		    			case "*":TokenList.add(new Token("*","MULTIPLICATIVE",lineNumber) ); break;
		    			case "div": TokenList.add(new Token("div","MULTIPLICATIVE",lineNumber) ); break;
		    			case "mod": TokenList.add(new Token("mod","MULTIPLICATIVE",lineNumber) ); break;
		    			case "+":TokenList.add(new Token("+","ADDITIVE",lineNumber) ); break;
		    			case "-":TokenList.add(new Token("-","ADDITIVE",lineNumber) ); break;
		    			case "=":TokenList.add(new Token("=","COMPARE",lineNumber) ); break;
		    			case "!=":TokenList.add(new Token("!=","COMPARE",lineNumber) ); break;
		    			case "<":TokenList.add(new Token("<","COMPARE",lineNumber) ); break;
		    			case ">":TokenList.add(new Token(">","COMPARE",lineNumber) ); break;
		    			case "<=":TokenList.add(new Token("<=","COMPARE",lineNumber) ); break;
		    			case ">=":TokenList.add(new Token(">=","COMPARE",lineNumber) ); break;
		    			//keywords
		    			case "if":TokenList.add(new Token("if","IF",lineNumber) ); break;
		    			case "then":TokenList.add(new Token("then","THEN",lineNumber) ); break;
		    			case "else":TokenList.add(new Token("else","ELSE",lineNumber) ); break;
		    			case "begin":TokenList.add(new Token("begin","BEGIN",lineNumber) ); break;
		    			case "end":TokenList.add(new Token("end","END",lineNumber) ); break;
		    			case "while":TokenList.add(new Token("while","WHILE",lineNumber) ); break;
		    			case "do":TokenList.add(new Token("do","DO",lineNumber) ); break;
		    			case "program":TokenList.add(new Token("program","PROGRAM",lineNumber) ); break;
		    			case "var":TokenList.add(new Token("var","VAR",lineNumber) ); break;
		    			case "as":TokenList.add(new Token("as","AS",lineNumber) ); break;
		    			case "int":TokenList.add(new Token("int","INT",lineNumber) ); break;
		    			case "bool":TokenList.add(new Token("bool","BOOL",lineNumber) ); break;
		    			//built in procedures
		    			case "writeInt":TokenList.add(new Token("writeInt","WRITEINT",lineNumber) ); break;
		    			case "readInt":TokenList.add(new Token("readInt","READINT",lineNumber) ); break;

		    			case "false":TokenList.add(new Token("false","boolit",lineNumber) ); break;
		    			case "true":TokenList.add(new Token("true","boolit",lineNumber) ); break;
		    			case "%":while(st.hasMoreTokens()){st.nextToken();}break;

		    			default:
		    				// check if identifier: starts with capital letter consists of only capital letters and numbers [A-Z][A-Z0-9]*
		    				if(isIden(token)){
		    					TokenList.add(new Token(token,"IDEN",lineNumber) );
		    				}
		    				// or number (+|-)?[1-9][0-9]*|0
		    				else if(isNum(token)){
		    					TokenList.add(new Token(token,"num",lineNumber) );
		    				}
		    				else{
		    					error(token);
		    				}

		    				break;
		    		}
		    	}
		    }

		    //write to file
		    for(int i = 0; i < TokenList.size(); i++){
		    	output.write("<" + TokenList.get(i).lexeme + ", " + TokenList.get(i).token +">\n");

		    }

		    br.close();
		    output.close();
		    }

	}

	public static boolean isNum(String token) {
		char c;

		if(token.charAt(0) == '+' | token.charAt(0) == '-'){
			for(int i = 1; i < token.length();i++){

				c=token.charAt(i);

				if (!(isNumeric(c)) ){
					System.out.println("char" + c);
					return false;
				}
			}
		}
		else{
			for(int i = 0; i < token.length();i++){

				c=token.charAt(i);

				if (!(isNumeric(c)) ){
					return false;
				}
			}
		}
		for(int i = 1; i < token.length();i++){

			c=token.charAt(i);

			if (!(isNumeric(c)) ){
				System.out.println("char" + c);
				return false;
			}
		}

		return true;

	}

	public boolean isIden(String token) {
		char c=token.charAt(0);

		if(!(isAlpha(c))){
			return false;
		}

		for(int i = 1; i < token.length();i++){
			c=token.charAt(i);
			if(!(isAlpha(c)) && !( isNumeric(c) )){
				return false;
			}
		}
		return true;
	}

	public boolean isAlpha(char c)
	{
		if(c >= 'A' && c <= 'Z')
			return true;

		return false;
	}

	private static boolean isNumeric(char c) {
		if (c >= '0' && c <= '9')
			return true;

		return false;
	}

	public int getLineNumber(){
		return lineNumber;
	}

	public void error(String token){
		System.out.println("An error occurred on line " + getLineNumber());
		System.out.println("token: " + token);
		System.exit(0);
	}

	public int findIndex(String sym){
		//finds and returns the index of the location of symbol "sym"
		int index;
		for(index = 0; index<SymbolTable.size(); index++){
			if(sym.equals(SymbolTable.get(index).getLexeme() ) ){
				return index;
			}
		}
		return 999;
	}

	public String currentLexeme(){
		return TokenList.get(TI).lexeme;
	}

	public String currentToken(){
		return TokenList.get(TI).token;
	}

	public int currentLineNumber(){
		return TokenList.get(TI).lineNumber;
	}

	public String nextToken(){
		TI++;
		return TokenList.get(TI).lexeme;
	}

	public void advToken() {
		TI++;
	}

}

//LiSA to Assembly
class RD_Parser {

	// Symbols

	private final String LPSym = "(";
	private final String RPSym = ")";
	private final String ASGNSym = ":=";
	private final String SCSym = ";";

	//new symbols

	// MULTIPLICATIVE operators
	private final String MULSym = "*";
	private final String DIVSym = "div";
	private final String MODSym = "mod";

	// additive operators
	private final String ADDSym = "+";
	private final String SUBSym = "-";

	// comparison operators
	private final String EQUALSSym = "=";
	private final String NOTEQUALSym = "!=";
	private final String LESSTHANSym = "<";
	private final String GREATERTHANSym = ">";
	private final String GREATERTHANEQUALTOSym = ">=";
	private final String LESSTHANEQUALTOSym = "<=";

	// keywords
	private final String BEGINSym = "begin";
	private final String ENDSym = "end";
	private final String IFSym = "if";
	private final String THENSym = "then";
	private final String ELSESym = "else";
	private final String WHILESym = "while";
	private final String DOSym = "do";
	private final String PROGRAMSym = "program";
	private final String VARSym = "var";
	private final String ASSym = "as";
	private final String INTSym = "int";
	private final String BOOLSym = "bool";

	// built-in procedures
	private final String WRITEINTSym = "writeInt";
	private final String READINTSym = "readInt";

	//Holds next token
	private String lookAhead;

	//label number
	private int numLabel = 0;

	//create object for lexical class
	private LiSA lex;

	private Writer output;


	public RD_Parser(LiSA L) throws IOException{
		output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("assembly.txt")));
		lex=L;
		lookAhead = lex.currentLexeme();
		program();
		System.out.println("Parsing done...");
		output.close();
	}

	private void Match(String t) throws IOException{
		//compares Symbol with token stored in lookAhead, if they match advance lookAhead to next token
		//else there is an error

		if(lookAhead == t ){
			if(lookAhead.equals("end")){
				return;
			}

			nextToken1();
		}
		else{
			System.out.println("Received: " + lookAhead);
			System.out.println("Needed: " + t);
			System.out.println("in Line " + lex.currentLineNumber());

			System.out.println("The token does not match, exiting...");
			output.close();
			System.exit(0);
		}
	}

	private void nextToken1(){
		//System.out.println("\nOld token: " + lookAhead);
		lex.advToken();
		lookAhead = lex.currentLexeme();
		//System.out.println("New token: " + lookAhead + "\n");
	}

	//start
	//done
	private void program() throws IOException{
		output.write("PROGRAM\n");
		Match(PROGRAMSym);
		declarations();
		output.write("BEGIN\n");
		Match(BEGINSym);
		statementSequence();
		output.write("END\n");
		Match(ENDSym);
	}

	//done
	private void declarations() throws IOException{
		if(lookAhead == VARSym){
			Match(VARSym);
			ident();
			Match(ASSym);
			type();
			Match(SCSym);
			declarations();
		}
		//else empty

	}

	//done
	private void type() throws IOException{
		// is INT
		if( lookAhead == INTSym){
			Match(INTSym);
		}
		//else if BOOL
		else if (lookAhead == BOOLSym) {
			Match(BOOLSym);
		}
		//else error
		else{
			//error();
		}
	}

	//fix'd?
	private void statementSequence() throws IOException{
		if(lex.currentToken()=="IDEN" | lookAhead == IFSym | lookAhead == WHILESym | lookAhead == WRITEINTSym ){
			statement();
			Match(SCSym);
			statementSequence();
		}

	}

	//done
	private void statement() throws IOException{
		if(lookAhead == IFSym){
			ifStatement();
		}
		else if(lookAhead == WHILESym){
			whileStatement();
		}
		else if(lookAhead == WRITEINTSym){
			writeInt();
		}
		else{
			assignment();
		}

	}
	//done
	private void assignment() throws IOException{
		output.write("LVALUE ");
		ident();

		Match(ASGNSym);

		if(lookAhead == READINTSym){
			READINT();
		}
		else{
			expression();
		}

		output.write(":=\n");
	}
	//done
	private void ifStatement() throws IOException{
		String label;
		String label2;

		Match(IFSym);

		expression();

		Match(THENSym);

		label = genLabel();
		label2 = genLabel();

		output.write("GOFALSE " + label + "\n");

		statementSequence();

		output.write("GOTO " + label2 + "\n");
		output.write("LABEL " + label + "\n");
		elseClause();

		Match(ENDSym);
		nextToken1();

		output.write("LABEL " + label2 + "\n");


	}

	//done
	private void elseClause() throws IOException{
		if(lookAhead == ELSESym){
			Match(ELSESym);
			statementSequence();
		}
		//else empty
	}

	//done
	private void whileStatement() throws IOException{
		String label = genLabel();
		String label2 = genLabel();

		Match(WHILESym);
		output.write("LABEL " + label + "\n");

		expression();

		//GOFALSE or GOTRUE?
		output.write("GOFALSE " + label2 + "\n");


		Match(DOSym);
		statementSequence();

		output.write("GOTO " + label + "\n");

		Match(ENDSym);
		nextToken1();
		output.write("LABEL " + label2 + "\n");
	}
	// not done
	private void writeInt() throws IOException{
		Match(WRITEINTSym);
		// output to console and move cursor to next line
		expression();
		output.write("PRINT\n");
	}
	//done?
	private void expression() throws IOException{
		simpleExpression();

		if (lex.currentToken() == "COMPARE"){
			String temp = compare();
			expression();
			output.write(temp + "\n");

		}
	}
	//done?
	private void simpleExpression() throws IOException{
		term();
		if( lex.currentToken() == "ADDITIVE" ){
			String temp = additive();
			simpleExpression();
			output.write(temp + "\n");
		}
	}
	//done?
	private void term() throws IOException{
		factor();
		if( lex.currentToken() == "MULTIPLICATIVE" ){
			String temp = multiplicative();
			term();
			output.write(temp + "\n");
		}
	}
	//done?
	private void factor() throws IOException{
		if(lookAhead == LPSym){
			Match(LPSym);
			expression();
			Match(RPSym);
		}
		else{
			//idenify as ident or num or boolit
			if(lex.currentToken()=="IDEN"){
				output.write("RVALUE " + lex.currentLexeme() + "\n");
				nextToken1();
			}

			else if(lex.currentToken()=="num"){
				if(lex.currentLexeme().charAt(0) == '+'){
					output.write("PUSH " + lex.currentLexeme().substring(1) + "\n");
				}
				else{
					output.write("PUSH " + lex.currentLexeme() + "\n");
				}
				nextToken1();
			}

			else if(lex.currentToken()=="boolit"){
				output.write(lex.currentLexeme() + "\n");
				nextToken1();
			}
			else{
				//error();
			}
		}
	}

	//other
	private void READINT() throws IOException{
		//read an integer from console
		output.write("INPUT\n");
		nextToken1();
	}

	private void ident() throws IOException{
		//is current token an ident
		if (lex.currentToken() == "IDEN"){
			output.write(lex.currentLexeme() + "\n");
			nextToken1();
		}
		else{
			System.out.println("Expected IDEN received: " + lex.currentToken());
			System.exit(0);
		}
	}

	private void num() throws IOException{
		// is current token a number
		if(lex.currentToken() == "num"){

			output.write(lex.currentLexeme());
			nextToken1();
		}
		else{
			System.out.println("Expected num received: " + lex.currentToken());
			System.exit(0);
		}
	}

	private void boollit() throws IOException{
		// is current token a boollit
		if(lex.currentToken() == "boolit"){
			switch(lex.currentLexeme()){
				case "true": //output.write("true\n");break;
				case "false": //output.write("false\n");break;
			}

			nextToken1();
		}
		else{
			System.out.println("Expected boollit received: " + lex.currentToken());
			System.exit(0);
		}
	}

	private String compare() throws IOException{
		///is the current token a commpare operator
		if(lex.currentToken() == "COMPARE"){
			String temp = "";
			switch(lex.currentLexeme()){
				case "=": temp = "EQ"; break;
				case "!=":temp = "NE"; break;
				case "<": temp = "LT"; break;
				case ">": temp = "GT"; break;
				case "<=": temp = "LE"; break;
				case ">=": temp = "GE"; break;
			}


			nextToken1();
			return temp;
		}
		else{
			return "";
		}
	}

	private String additive() throws IOException{
		//is the current token an additive operator
		if(lex.currentToken() == "ADDITIVE"){
			String temp = "";
			switch(lex.currentLexeme()){
				case "+": temp = "ADD"; break;
				case "-": temp = "SUB"; break;
			}

			nextToken1();
			return temp;
		}
		else{
			return "";
		}
	}

	private String multiplicative() throws IOException{
		//is the current token a multiplicative operator?
		if(lex.currentToken() == "MULTIPLICATIVE"){
			String temp = "";
			switch(lex.currentLexeme()){
				case "*": temp ="MPY" ;break;
				case "div": temp = "DIV" ;break;
				case "mod": temp = "MOD" ; break;
			}


			nextToken1();
			return temp;
		}
		else{
			return "";
		}
	}

	private String genLabel() {
	 	numLabel++;
	 	return "Label" + numLabel;
	}
}

//Assembly to bytecode
class Assembler {
	//symbol table
	private List<String> memory = new ArrayList<String>();


	private String instruction;// 32 bits long
	private String ignored = "00000000000"; // bits 32-21
	private  String opcode; // bits 20-16
	private  String operand;// bits 15-0

	private  String file = "assembly.txt";

	private  BufferedReader br;
	private  int lineNumber=0;
	private  int fromCode=0;

	public  StackMachine SM;

	private  StringTokenizer st;

	LiSA lex;

	public Assembler(LiSA lisaobj) throws FileNotFoundException, IOException {
		lex = lisaobj;

		fillSymbolTable();

		fillDataTable();

		System.out.println("Assembling done...");

		SM = new StackMachine(memory, lex.SymbolTable);

	}

	public void printDataTable() {
		System.out.println("Data table");

		for(int i =0; i<memory.size();i++){
			System.out.println(memory.get(i));
		}
	}

	public void printSymbolTable() {
		System.out.println("Symbol table");
		System.out.print("Lexeme"+"\t\t");
		System.out.print("Type"+"\t\t");
		System.out.println("Value"+"\t\t");
		for(int i =0; i<lex.SymbolTable.size();i++){
			System.out.print(lex.SymbolTable.get(i).getLexeme() + "\t\t");
			System.out.print(lex.SymbolTable.get(i).getType() + "\t\t");
			System.out.println(lex.SymbolTable.get(i).getValue() + "\t\t");
		}
	}

	private void fillSymbolTable() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    String token;
		    int value;
		    while ((line = br.readLine()) != null) {
		        // process the line.
		    	st = new StringTokenizer(line);
		    	lineNumber++;
		    		token=st.nextToken();

		    		switch(token){
		    			case "LABEL":
		    				fromCode++;
		    				token=st.nextToken();
		    				//add to Symbol table
		    				lex.SymbolTable.add(new Symbol(token,"Code",fromCode));
		    				break;
		    			case "PUSH":fromCode++; break;
		    			case "RVALUE":fromCode++; break;
		    			case "LVALUE":fromCode++; break;
		    			case "POP":fromCode++; break;
		    			case ":=":fromCode++; break;
		    			case "COPY":fromCode++; break;
		    			case "ADD":fromCode++; break;
		    			case "SUB":fromCode++; break;
		    			case "MPY":fromCode++; break;
		    			case "DIV":fromCode++; break;
		    			case "MOD":fromCode++; break;
		    			case "OR":fromCode++; break;
		    			case "AND":fromCode++; break;
		    			case "GOTO":fromCode++; break;
		    			case "GOFALSE":fromCode++; break;
		    			case "GOTRUE":fromCode++; break;
		    			case "PRINT":fromCode++; break;
		    			case "HALT":fromCode++; break;
		    			case "INPUT":fromCode++; break;
			   			case "EQ":fromCode++;  break;
			   			case "NE":fromCode++;  break;
			   			case "LT":fromCode++;  break;
			   			case "LE":fromCode++;  break;
			   			case "GT":fromCode++;  break;
			   			case "GE":fromCode++;  break;
		    			default:

		    		}
		    	}
		    br.close();
		    }

	}

	private void fillDataTable() throws FileNotFoundException, IOException  {

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    String token;
		    int temp;

		    br.readLine();

		    boolean bool=true;

		    do{

		    	line = br.readLine();
		    	st = new StringTokenizer(line);
		    	token=st.nextToken();

		    	if(token.equals("BEGIN")){
		    		bool=false;
		    	}

		    }while(bool);

		    while ((line = br.readLine()) != null) {
		        // process the line.
		    	st = new StringTokenizer(line);
		    	lineNumber++;
		    	token=st.nextToken();

		    	switch(token){

		    		case "END":
		    			fromCode++;
		    			setOpcode(0);
		    			setOperand("");
		    			break;
		    			/*
		    		case "PUSH":
		    			fromCode++;
		    			setOpcode(1);
		    			token=st.nextToken();
		    			temp= Integer.parseInt(token);
		    			setOperand(Integer.toBinaryString(temp));
		    			break;
		    			*/
		    		case "PUSH":
		    			fromCode++;
		    			setOpcode(1);
		    			token=st.nextToken();
		    			temp= Integer.parseInt(token);
		    			setOperand(Integer.toBinaryString(temp));
		    			break;
		    		case "RVALUE":
		   				fromCode++;
		   				setOpcode(2);
		    			token = st.nextToken();
		    			temp = lex.findIndex(token);
		    			if (temp != 999){
		    				setOperand(Integer.toBinaryString(temp));
		    			}
		    			else{
		    				System.out.println("Variable: " + token + " isn't initialized");
		    				System.exit(0);
		    			}

		   				break;
		   			case "LVALUE":
		   				fromCode++;
		   				setOpcode(3);
		   				token = st.nextToken();
		   				temp = lex.findIndex(token);
		    			if (temp != 999){
		    				setOperand(Integer.toBinaryString(temp));
		    			}
		    			else{
		    				System.out.println("Variable: " + token + " isn't initialized");
		    				System.exit(0);
		    			}
		   				break;
		   			case "POP":
		   				fromCode++;
		  				setOpcode(4);
		   				setOperand("");
		   				break;
		    		case ":=":
		    			fromCode++;
		    			setOpcode(5);
		    			setOperand("");
		   				break;
		   			case "COPY":
		   				fromCode++;
		   				setOpcode(6);
		   				setOperand("");
		   				break;
		   			case "ADD":
		   				fromCode++;
		    			setOpcode(7);
		   				setOperand("");
		   				break;
		   			case "SUB":
		   				fromCode++;
		   				setOpcode(8);
		   				setOperand("");
		   				break;
		   			case "MPY":
		   				fromCode++;
		   				setOpcode(9);
		   				setOperand("");
		   				break;
		   			case "DIV":
		   				fromCode++;
		  				setOpcode(10);
		   				setOperand("");
		   				break;
		   			case "MOD":
		   				fromCode++;
		   				setOpcode(11);
		   				setOperand("");
		   				break;
		   			case "OR":
		   				fromCode++;
		   				setOpcode(12);
		   				setOperand("");
		   				break;
		   			case "AND":
		   				fromCode++;
		   				setOpcode(13);
		  				setOperand("");
		   				break;
		   			case "LABEL":
		   				fromCode++;
		   				setOpcode(14);
		   				token=st.nextToken();
		   				temp = lex.findIndex(token);
		   				setOperand(Integer.toBinaryString(temp));
		   				break;
		   			case "GOTO":
		   				fromCode++;
		   				setOpcode(15);
		   				token=st.nextToken();
		   				temp = lex.findIndex(token);
		   				setOperand(Integer.toBinaryString( lex.SymbolTable.get(temp).getValue() ));
		   				break;
		   			case "GOFALSE":
		   				fromCode++;
		   				setOpcode(16);
		   				token=st.nextToken();
		   				temp = lex.findIndex(token);
		   				setOperand(Integer.toBinaryString( lex.SymbolTable.get(temp).getValue() ));
		   				break;
		   			case "GOTRUE":
		   				fromCode++;
		   				setOpcode(17);
		   				token=st.nextToken();
		   				temp = lex.findIndex(token);
		   				setOperand(Integer.toBinaryString( lex.SymbolTable.get(temp).getValue() ));
		   				break;
		   			case "PRINT":
		   				fromCode++;
		   				setOpcode(18);
		   				break;

		   			//new****************************************************************************************************************************************
		   			case "EQ": fromCode++; setOpcode(19); break;
		   			case "NE": fromCode++; setOpcode(20);  break;
		   			case "LT": fromCode++; setOpcode(21);  break;
		   			case "LE": fromCode++; setOpcode(22);  break;
		   			case "GT": fromCode++; setOpcode(23);  break;
		   			case "GE": fromCode++; setOpcode(24);  break;
		   			case "INPUT": fromCode++; setOpcode(25); break;

		   			default:
		   				break;
		   		}
		   		setInstruction();
		   		memory.add(instruction);
		   	}
		    br.close();
		    }
	}

	private void setOpcode(int op){
		/*
		 * Sets the opcode for the instruction string
		 * the opcode is located in bits 20-16 (5 bits)
		 */

		String bin = Integer.toBinaryString(op);

		while(bin.length()<5){
			bin = "0"+bin;
		}
		opcode=bin;

	}

	private void setOperand(String op){
		//bits 15-0 (16 bits)


		while(op.length()<32){
			op = "0"+op;
		}
		//System.out.println(op);
		operand=op;
	}

	public void setInstruction(){
		instruction = ignored+opcode+operand;
	}

	public int getLineNumber(){
		return lineNumber;
	}

	public void error(){
		System.out.println("An error occurred on line " + getLineNumber());
		System.out.println("token: " + st);
		System.exit(0);
	}

}

//use the byte code
class StackMachine {

	private  List<String> memory = new ArrayList<String>();
	private  int  memory_counter=0;

	private  List<Symbol> SymbolTable = new ArrayList<Symbol>();

	private  Stack<Variable> stack = new Stack<Variable>();

	private  int opcode;
	private  int operand;
	private  String Instruction;

	private  boolean done = true;

	//for debugging print statements
	boolean print = false;

	public  void setMemory(List<String> l){
		memory=l;
	}

	public  void setSymbol(List<Symbol> l){
		SymbolTable = l;
	}

	public StackMachine(List<String> m,List<Symbol> s){
		memory = m;
		SymbolTable = s;

		start();
		System.out.println("Done!");
	}

	private  void start(){
		while(done){

			Instruction = memory.get(memory_counter);

			//System.out.println("Memory counter: " + memory_counter);
			//System.out.println(Instruction);

			doOp(Instruction);
			memory_counter++;
		}
	}

	public  void doOp(String instruction){
		//get opcode
		// 00000000000 00000 0000000000000000
		//             |||||
		opcode=Integer.parseInt(instruction.substring(11, 16),2);


		//get operand
		// 00000000000 00000 0000000000000000
		//                   ||||||||||||||||
		long l = Long.parseLong(instruction.substring(16), 2);

		operand= (int) l;

		//System.out.println("opcode" + opcode);
		//System.out.println("operand" + operand);

		switch (opcode){
			case(0):HALT();break;
			case(1):PUSH(operand);break;
			case(2):RVALUE(operand);break;
			case(3):LVALUE(operand);break;
			case(4):POP();break;
			case(5):EQUALS();break;
			case(6):COPY();break;
			case(7):ADD();break;
			case(8):SUB();break;
			case(9):MPY();break;
			case(10):DIV();break;
			case(11):MOD();break;
			case(12):OR();break;
			case(13):AND();break;
			case(14):LABEL(operand);break;
			case(15):GOTO(operand);break;
			case(16):GOFALSE(operand);break;
			case(17):GOTRUE(operand);break;
			case(18):PRINT(operand);break;
			case(19):EQ(); break;
			case(20):NE(); break;
			case(21):LT(); break;
			case(22):LE(); break;
			case(23):GT(); break;
			case(24):GE(); break;
			case(25):INPUT(); break;
		}
	}

	private  void INPUT() {
		Scanner sc = new Scanner(System.in);
		boolean done = false;
		String in = "";

		while(!done){
			System.out.print("INPUT:  ");
			in = sc.nextLine().trim();

			if(LiSA.isNum(in)){
				done=true;
			}
			else{
				System.out.println("That is not a number try again.");
			}
		}


		stack.push(new Variable("NA", in ));
		sc.close();

	}

	private  void GE() {
		if(print){
			System.out.println("GE");
		}
		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b >= a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	private  void GT() {
		if(print){
			System.out.println("GT");
		}

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b > a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	private  void LE() {
		if(print){
			System.out.println("LE");
		}
		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b <= a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	private  void LT() {
		if(print){
			System.out.println("LT");
		}
		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b < a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	private  void NE() {
		if(print){
			System.out.println("NE");
		}
		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b != a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	private  void EQ() {
		if(print){
			System.out.println("EQ");
		}
		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		if (b == a){
			stack.push(new Variable("true", "NA"));
		}
		else{
			stack.push(new Variable("false", "NA"));
		}
	}

	//instructions
	public  void HALT(){
		if(print){
			System.out.println("HALT");
		}

		done=false;
	}

	public  void PUSH(int op){
		if(print){
			System.out.println("PUSH " + op);
		}


		stack.push(new Variable("NA", op + ""));
	}

	public  void RVALUE(int op){
		if(print){
			System.out.println("RVALUE " + op);
		}
		stack.push(new Variable("NA", "" + SymbolTable.get(op).getValue()));
	}

	public  void LVALUE(int op){
		if(print){
			System.out.println("LVALUE " + op);
		}

		stack.push(new Variable("NA", op+""));
	}

	public  void POP(){

		stack.pop();
		if(print){
			System.out.println("POP");
		}
	}

	public  void EQUALS(){

		String A = stack.pop().getValue();

		if(A=="NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int rvalue = Integer.parseInt(A);

		SymbolTable.get( Integer.parseInt( stack.pop().getValue() ) ).setValue(rvalue);

		if(print){
			System.out.println("EQUALS");
		}
	}

	public  void COPY(){

		String A = stack.pop().getValue();
		if(A=="NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int copy = Integer.parseInt(A);

		stack.push(new Variable("NA", copy+""));
		stack.push(new Variable("NA", copy+""));

		if(print){
			System.out.println("COPY");
		}
	}

	public  void ADD(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result= a+b;

		if(print){
			System.out.println("ADD");
			System.out.println(b + " + " + a);
		}


		stack.push(new Variable("NA", result+""));
	}

	public  void SUB(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result = b-a;

		if(print){
			System.out.println("SUB");
			System.out.println(b + " - " + a);
		}


		stack.push(new Variable("NA", result+""));
	}

	public  void MPY(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result = a*b;

		if(print){
			System.out.println("MPY");
			System.out.println(b + " * " + a);
		}


		stack.push(new Variable("NA", result+""));
	}

	public  void DIV(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result = b/a;

		if(print){
			System.out.println("DIV");
			System.out.println(b + " / " + a);
		}


		stack.push(new Variable("NA", result+""));
	}

	public  void MOD(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result = a%b;

		if(print){
			System.out.println("MOD");
			System.out.println(b + " % " + a);
		}


		stack.push(new Variable("NA", result+""));
	}

	public  void OR(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);

		int result= a|b;

		if(print){
			System.out.println("OR");
		}
		stack.push(new Variable("NA", result+""));
	}

	public  void AND(){

		String A = stack.pop().getValue();
		String B = stack.pop().getValue();

		if(A=="NA" | B == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
		int a = Integer.parseInt(A);
		int b = Integer.parseInt(B);
		int result=a&b;

		if(print){
			System.out.println("AND");
		}
		stack.push(new Variable("NA", result+""));
	}

	public  void LABEL(int op){
		//does nothing
		if(print){
			System.out.println("LABEL");
		}


	}

	public  void GOTO(int op){
		if(print){
			System.out.println("GOTO");
		}

		memory_counter = op-1;
	}

	public  void GOFALSE(int op){


		String temp = stack.pop().getbool();

		if(print){
			System.out.println("GOFALSE");
			System.out.println("GOFALSE value:                            " + temp);
		}



		if(temp == "false"){
			memory_counter = op-1;
		}
		if(temp == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
	}
	//1 = true
	//0 = false
	public  void GOTRUE(int op){
		if(print){
			System.out.println("GOTRUE");
		}

		String temp = stack.pop().getbool();

		if(temp == "true"){
			memory_counter = op-1;
		}
		if(temp == "NA"){
			System.out.println("Error: An error has occurred GG WP, (wrong type) exiting...");
			System.exit(0);
		}
	}

	public  void PRINT(int address){
		System.out.println("PRINT " + SymbolTable.get(address).getValue());
	}
}

class Symbol{
	private String lexeme;
	private String type;
	private int value;

	public Symbol(String lexeme, String type, int value) {
		super();
		this.lexeme = lexeme;
		this.type = type;
		this.value = value;
	}

	public String getLexeme() {
		return lexeme;
	}

	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}

class Variable{
	private String bool;
	private String value;

	public Variable(String bol, String val){
		this.bool = bol;
		this.value = val;
	}

	public void setbool(String s){
		this.bool = s;
	}

	public String getbool(){
		return this.bool;
	}

	public void setValue(String v){
		this.value = v;
	}

	public String getValue(){
		return this.value;
	}
}

class Token {
	String lexeme;
	String token;
	int lineNumber;

	public Token(String lexeme, String token, int line) {
		super();
		this.lexeme = lexeme;
		this.token = token;
		this.lineNumber = line;
	}
	public String getLexeme() {
		return lexeme;
	}
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int line) {
		this.lineNumber = line;
	}

}

