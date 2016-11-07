package com.gotc;

import com.gotc.actions.GOTAction;
import com.gotc.actions.NumberAction;
import com.gotc.actions.PrintAction;
import com.gotc.actions.VariableAssignAction;
import com.gotc.actions.arithmetic.DivideOperatorAction;
import com.gotc.actions.arithmetic.MinusOperatorAction;
import com.gotc.actions.arithmetic.MultiplyOperatorAction;
import com.gotc.actions.arithmetic.PlusOperatorAction;
import com.gotc.actions.conditional.*;
import com.gotc.actions.logical.AndAction;
import com.gotc.actions.logical.NotAction;
import com.gotc.actions.logical.OrAction;
import com.gotc.actions.method.MethodAction;
import com.gotc.actions.method.MainMethodAction;
import com.gotc.actions.method.MethodCallAction;
import com.gotc.actions.method.ReturnAction;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import static com.gotc.util.Constants.*;

/**
 * Created by srikaram on 06-Nov-16.
 */
@SuppressWarnings("WeakerAccess")
@BuildParseTree
public class GOTParser extends BaseParser<Object> {

    final Rule EOL = Optional(Sequence(ZeroOrMore(FirstOf("\t", "\r", " ")), OneOrMore("\n"), ZeroOrMore(FirstOf("\t", "\r", " ", "\n"))));
    final Rule WHITESPACE = FirstOf(OneOrMore(" "), OneOrMore("\t"));

    final String fileName;

    public GOTParser(String fileName) {
        this.fileName = fileName;
    }

    public Rule realRoot() {
        GOTAction action = new GOTAction(fileName);
        return Sequence(action, Sequence(EOL, BEGINPROGRAM, EOL,
                abstractMethod(), EOL, ENDPROGRAM, EOL, EOI), action);
    }

    Rule abstractMethod() {
        return Sequence(EOL, ZeroOrMore(FirstOf(mainMethod(), method())), EOL);
    }

    Rule mainMethod() {
        MainMethodAction action = new MainMethodAction();
        return Sequence(EOL, Sequence(BEGINMAIN, action), statements(),
                 ENDMAIN, EOL, action);
    }

    Rule method() {
        MethodAction action = new MethodAction();
        return Sequence(Sequence(EOL, Sequence(DECLAREMETHOD, action), WHITESPACE, variable(), EOL,
                ZeroOrMore(Sequence(METHODARGUMENTS, WHITESPACE, variable()), EOL), EOL,
                Sequence(Optional(NONVOIDMETHOD), push(NONVOIDMETHOD.equals(match()))), EOL, statements(), EOL,
                ENDMETHODDECLARATION, EOL), action);
    }

    Rule statements() {
        return Sequence(EOL,
                ZeroOrMore(FirstOf(printStatement(), assignVariableStatement(),
                        logicalStatement(), arithmeticStatement(), conditionalStatement(),
                        ifStatement(), whileStatement(), returnStatement(), methodCallStatement())),
                EOL);
    }

    Rule printStatement() {
        return Sequence(EOL, PRINT, WHITESPACE,
                FirstOf(numberNoAction(), string()), EOL, new PrintAction());
    }

    Rule assignVariableStatement() {
        return Sequence(EOL, ASSIGNVARIABLE, WHITESPACE, variableName(), push(match()),
                WHITESPACE, expression(), EOL,
                new VariableAssignAction());
    }

    Rule logicalStatement() {
        Rule and = twoOperandStatement(AND, new AndAction());
        Rule or = twoOperandStatement(OR, new OrAction());
        Rule not = Sequence(Sequence(NOT, WHITESPACE, variable()), new NotAction());
        return Sequence(EOL, OneOrMore(FirstOf(and, or, not)), EOL);
    }

    Rule arithmeticStatement() {
        Rule plus = twoOperandStatement(PLUSOPERATOR, new PlusOperatorAction());
        Rule minus = twoOperandStatement(MINUSOPERATOR, new MinusOperatorAction());
        Rule multiply = twoOperandStatement(MULTIPLICATIONOPERATOR, new MultiplyOperatorAction());
        Rule division = twoOperandStatement(DIVISIONOPERATOR, new DivideOperatorAction());
        return Sequence(EOL, OneOrMore(FirstOf(plus, minus, multiply, division)), EOL);
    }

    Rule conditionalStatement() {
        Rule lesserThan = twoOperandStatement(LESSERTHAN, new LesserThanAction());
        Rule equalsTo = twoOperandStatement(EQUALTO, new EqualToAction());
        return Sequence(EOL, OneOrMore(FirstOf(lesserThan, equalsTo)), EOL);
    }

    Rule ifStatement() {
        return Sequence(Sequence(EOL, IF, WHITESPACE,
                expression(),
                EOL, statements(), EOL,
                ZeroOrMore(Sequence(Sequence(ELSE, EOL, statements(), EOL), new ElseAction())),
                ENDIF, EOL), new IfAction());
    }

    Rule whileStatement() {
        return Sequence(Sequence(EOL, WHILE, WHITESPACE,
                expression(),
                EOL, statements(), EOL,
                ENDWHILE, EOL), new WhileAction());
    }

    Rule returnStatement() {
        return Sequence(EOL, RETURN, Optional(WHITESPACE, expression()),
                EOL, new ReturnAction());
    }

    Rule methodCallStatement() {
        return Sequence(EOL, CALLMETHOD, WHITESPACE, variable(),
                ZeroOrMore(Sequence(WHITESPACE, expression())), EOL,
                Optional(ASSIGNVARIABLEFROMMETHODCALL, WHITESPACE, variable()), new MethodCallAction());
    }

    Rule expression() {
        return FirstOf(number(), variable(), bool());
    }

    Rule twoOperandStatement(String operation, Action action) {
       return Sequence(Sequence(operation, WHITESPACE, variable(), WHITESPACE,
               expression()), action);
    }

    Rule variable() {
        return Sequence(variableName(), push(match()));
    }

    Rule bool() {
        return FirstOf(Sequence(TRUE, push(1)), Sequence(FALSE, push(0)));
    }

    Rule variableName() {
        return Sequence(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z')),
                ZeroOrMore(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'), CharRange('0', '9'))));
    }

    Rule numberNoAction() {
        return FirstOf(
                Sequence(OneOrMore(CharRange('0', '9')), push(match())),
                Sequence(Sequence("-", OneOrMore(CharRange('0', '9'))), push(match())));
    }

    Rule number() {
        return FirstOf(
                Sequence(OneOrMore(CharRange('0', '9')), push(match()), new NumberAction()),
                Sequence(Sequence("-", OneOrMore(CharRange('0', '9'))), push(match()), new NumberAction()));
    }

    Rule string() {
        return Sequence(OneOrMore(NoneOf("\n\\\r\"")), push(match()));
    }

}
