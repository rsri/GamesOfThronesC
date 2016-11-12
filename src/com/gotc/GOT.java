package com.gotc;

import com.gotc.components.GOTParser;
import com.gotc.components.Runner;
import com.gotc.nodes.GOTNode;
import com.gotc.util.Dialogues;
import com.gotc.util.Util;
import com.sun.org.apache.regexp.internal.RE;
import org.parboiled.Parboiled;
import org.parboiled.common.FileUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.File;
import java.util.Arrays;

/**
 * Created by srikaram on 05-Nov-16.
 */
public class GOT {

    private static final String COMPILE_ONLY = "-c";
    private static final String RUN_ONLY = "-r";
    private static final String COMPILE_AND_RUN = "-cr";

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                RuntimeException exception = new RuntimeException(Dialogues.PARSEERROR);
                System.err.println(exception + "\nUsage: GOT [-c|-r|-cr] [FileToSourceCode|FileToClassFile]");
                return;
            }
            String filename = args[1];
            File inputFile = new File(filename);
            if (!inputFile.exists()) {
                RuntimeException exception = new RuntimeException(Dialogues.PARSEERROR);
                System.out.println(exception + String.format("\nFile %s doesn't exist.", inputFile.getAbsolutePath()));
                return;
            }
            String className = Util.getBaseName(filename);
            switch (args[0].toLowerCase()) {
                case COMPILE_ONLY:
                    compile(inputFile);
                    return;
                case RUN_ONLY:
                    Runner.run(className);
                    return;
                case COMPILE_AND_RUN:
                    compile(inputFile);
                    Runner.run(className);
                    return;
                default:
                    RuntimeException exception = new RuntimeException(Dialogues.PARSEERROR);
                    System.err.println(exception + "\nUsage: GOT [-c|-r|-cr] [FileToSourceCode|FileToClassFile]");
            }
        } catch (Throwable e) {
            throw new RuntimeException(Dialogues.PARSEERROR, e);
        }
    }

    private static void compile(File inputFile) {
        String content = FileUtils.readAllText(inputFile);
        String className = Util.getBaseName(inputFile.getName());
        GOTParser parser = Parboiled.createParser(GOTParser.class, className);
        ParsingResult<?> result = new ReportingParseRunner<>(parser.realRoot()).run(content);
        if (result.hasErrors()) {
            RuntimeException exception = new RuntimeException(Dialogues.PARSEERROR);
            System.out.println(exception + "\n" + ErrorUtils.printParseErrors(result));
        } else {
            ((GOTNode) result.parseTreeRoot.getValue()).build();
        }
    }
}
