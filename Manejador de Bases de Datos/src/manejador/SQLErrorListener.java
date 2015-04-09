package manejador;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.*;

public class SQLErrorListener extends BaseErrorListener{
	
	@Override	
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		
		List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		
		GUI.msgError += "rule stack: [" + stack + "]" + "\n";
		GUI.msgError += "line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg + "\n\n";
		
		String errorMsgConsole = "line " + line + ":" + charPositionInLine + " " + msg + "\n";
		errorMsgConsole += this.underlineError(recognizer, (Token)offendingSymbol, line, charPositionInLine);
		System.err.println(errorMsgConsole);
	}
	
	protected String underlineError(Recognizer recognizer, Token offendingToken, int line,int charPositionInLine){
		CommonTokenStream tokens = (CommonTokenStream)recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split("\n");
		String errorLine = lines[line - 1];
		
		String underlineError = errorLine + "\n";
		
		for (int i=0; i<charPositionInLine; i++)
			underlineError += " ";
		
		int start = offendingToken.getStartIndex();
		int stop = offendingToken.getStopIndex();
		if ( start>=0 && stop>=0 ) {
			for (int i=start; i<=stop; i++)
				underlineError += "^";
		}
		underlineError += "\n";
		return underlineError;
	}
	

}
