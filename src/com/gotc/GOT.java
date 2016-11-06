package com.gotc;

import com.gotc.util.Util;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.File;

/**
 * Created by srikaram on 05-Nov-16.
 */
public class GOT {

    public static void main(String[] args) {
        try {
            File inputFile = new File("testfile.gotc");
            String content = Util.parseFile(inputFile);
            GOTParser parser = Parboiled.createParser(GOTParser.class);
            ParsingResult<?> result = new ReportingParseRunner<>(parser.realRoot()).run(content);
            System.out.println(result.hasErrors());
            if (result.hasErrors()) {
                for (ParseError error : result.parseErrors) {
                    System.out.println(content.charAt(error.getStartIndex()) + " " + content.charAt(error.getEndIndex()));
                }
            }
//            Object value = result.parseTreeRoot.getValue();
//            System.out.println(value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
