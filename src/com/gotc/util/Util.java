package com.gotc.util;

import org.parboiled.Context;
import org.parboiled.errors.BasicParseError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.errors.ParsingException;
import org.parboiled.support.StringVar;

import java.io.*;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class Util {

    public static String parseFile(File inputFile) throws IOException {
        StringBuilder contentWriter = new StringBuilder();
        FileReader input = new FileReader(inputFile);
        BufferedReader bufRead = new BufferedReader(input);
        String singleLine;
        while ((singleLine = bufRead.readLine()) != null) {
            contentWriter.append(singleLine);
            contentWriter.append('\n');
        }
        return contentWriter.deleteCharAt(contentWriter.length() - 1).toString();
    }

    public static String getBaseName(String filename) {
        int index = filename.lastIndexOf(File.pathSeparator);
        String name = filename.substring(index + 1);
        return removeExtension(name);
    }

    private static String removeExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? filename : filename.substring(0, index);
    }

    public static String capitalize(String str) {
        return String.valueOf(Character.toTitleCase(str.charAt(0))) +
                str.substring(1);
    }

	public static boolean isNumber(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		int sz = str.length();
		int startPos = str.charAt(0) == '-' ? 1 : 0;
		for (int i = startPos; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static void constructError(Context<Object> context, String message) {
        throw new ParserRuntimeException(message + " at " + context.getCurrentIndex());
    }

    public static void constructError(String message, String... args) {
        throw new ParsingException(String.format(message, args));
    }

    public static String buildMethodSignature(int size, boolean nonVoidMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        while (size-- > 0) {
            sb.append('I');
        }
        sb.append(')');
        sb.append(nonVoidMethod ? 'I' : 'V');
        return sb.toString();
    }

    public static StringVar[] cast(Object[] objs) {
        StringVar[] vars = new StringVar[objs.length];
        for (int i = 0 ; i < vars.length ; i++) {
            vars[i] = (StringVar) objs[i];
        }
        return vars;
    }
}
