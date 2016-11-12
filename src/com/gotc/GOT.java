package com.gotc;

import com.gotc.components.GOTParser;
import com.gotc.nodes.GOTNode;
import com.gotc.util.Constants;
import com.gotc.util.Util;
import org.parboiled.Parboiled;
import org.parboiled.common.FileUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.File;

/**
 * Created by srikaram on 05-Nov-16.
 */
public class GOT {

    public static void main(String[] args) {
        try {
            String filename = "testfile.gotc";
            File inputFile = new File(filename);
            String content = FileUtils.readAllText(inputFile);
            String className = Util.getBaseName(filename);
            GOTParser parser = Parboiled.createParser(GOTParser.class, className);
            ParsingResult<?> result = new ReportingParseRunner<>(parser.realRoot()).run(content);
            System.out.println(result.hasErrors());
            if (result.hasErrors()) {
                RuntimeException exception = new RuntimeException(Constants.PARSEERROR);
                System.out.println(exception + "\n" + ErrorUtils.printParseErrors(result));
            } else {
                ((GOTNode) result.parseTreeRoot.getValue()).build();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
