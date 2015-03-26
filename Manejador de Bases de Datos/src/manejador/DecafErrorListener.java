package manejador;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.*;

public class DecafErrorListener extends BaseErrorListener{
	public static String errorMsg;
	
	@Override	
	public void syntaxError(Recognizer<?, ?> recognizer,
	Object offendingSymbol,
	int line, int charPositionInLine,
	String msg,
	RecognitionException e) {
		List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		errorMsg = "rule stack: " + stack + "\nline " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg + "\n\n";			
		
		//System.err.println("Has been found errors...");
		System.err.println("rule stack: " + stack);
		System.err.println("line " + line + ":" + charPositionInLine + " at " + offendingSymbol+": " + msg);		
			
	}	
	

}
