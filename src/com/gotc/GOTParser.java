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
import com.gotc.actions.method.*;
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

    final Rule EOL = Sequence(ZeroOrMore(FirstOf("\t", "\r", " ")), OneOrMore("\n"), ZeroOrMore(FirstOf("\t", "\r", " ", "\n")));
    final Rule WHITESPACE = FirstOf(OneOrMore(" "), OneOrMore("\t"));

    final String fileName;

    public GOTParser(String fileName) {
        this.fileName = fileName;
    }

    public Rule realRoot() {
        GOTAction action = new GOTAction(fileName);
        return Sequence(action, Sequence(BEGINPROGRAM, EOL,
                abstractMethod(), ENDPROGRAM, EOI), action);
    }

    Rule abstractMethod() {
        return ZeroOrMore(FirstOf(mainMethod(), method(), methodPrototype()));
    }

    Rule mainMethod() {
        MainMethodAction action = new MainMethodAction();
        return Sequence(Sequence(BEGINMAIN, action), EOL, statements(),
                 ENDMAIN, EOL, action);
    }

    Rule method() {
        MethodAction action = new MethodAction();
        return Sequence(Sequence(Sequence(DECLAREMETHOD, WHITESPACE, variable(), EOL,
                ZeroOrMore(Sequence(METHODARGUMENTS, WHITESPACE, variable(), EOL)),
                Sequence(Optional(Sequence(NONVOIDMETHOD, EOL)),
                        push(NONVOIDMETHOD.equals(match().trim()))), action), 
                statements(), 
                ENDMETHODDECLARATION, EOL), action);
    }

    Rule methodPrototype() {
        return Sequence(Sequence(METHODPROTOTYPE, WHITESPACE,
                variable(), WHITESPACE, number(),EOL,
                ZeroOrMore(Sequence(METHODARGUMENTS, WHITESPACE, variable(), EOL)),
                Sequence(Optional(Sequence(NONVOIDMETHOD, EOL)), push(NONVOIDMETHOD.equals(match().trim())))),
                new MethodPrototypeAction());
    }

    Rule statements() {
        return Sequence(
                ZeroOrMore(FirstOf(printStatement(), assignVariableStatement(),
                        logicalStatement(), arithmeticStatement(), conditionalStatement(),
                        ifStatement(), whileStatement(), returnStatement(), methodCallStatement())),
                Optional(EOL));
    }

    Rule printStatement() {
        return Sequence(PRINT, WHITESPACE,
                FirstOf(numberNoAction(), string()), EOL, new PrintAction());
    }

    Rule assignVariableStatement() {
        return Sequence(ASSIGNVARIABLE, WHITESPACE, variable(),
                WHITESPACE, expression(), EOL,
                new VariableAssignAction());
    }

    Rule logicalStatement() {
        Rule and = twoOperandStatement(AND, new AndAction());
        Rule or = twoOperandStatement(OR, new OrAction());
        Rule not = Sequence(Sequence(NOT, WHITESPACE, variable()), new NotAction());
        return Sequence(OneOrMore(FirstOf(and, or, not)), Optional(EOL));
    }

    Rule arithmeticStatement() {
        Rule plus = twoOperandStatement(PLUSOPERATOR, new PlusOperatorAction());
        Rule minus = twoOperandStatement(MINUSOPERATOR, new MinusOperatorAction());
        Rule multiply = twoOperandStatement(MULTIPLICATIONOPERATOR, new MultiplyOperatorAction());
        Rule division = twoOperandStatement(DIVISIONOPERATOR, new DivideOperatorAction());
        return Sequence(OneOrMore(FirstOf(plus, minus, multiply, division)), Optional(EOL));
    }

    Rule conditionalStatement() {
        Rule lesserThan = twoOperandStatement(LESSERTHAN, new LesserThanAction());
        Rule equalsTo = twoOperandStatement(EQUALTO, new EqualToAction());
        return Sequence(OneOrMore(FirstOf(lesserThan, equalsTo)), Optional(EOL));
    }

    Rule ifStatement() {
        IfElseAction action = new IfElseAction();
        return Sequence(Sequence(
                Sequence(IF, WHITESPACE, expression(), action.ifAction()),
                EOL, statements(), 
                ZeroOrMore(Sequence(Sequence(
                        Sequence(ELSE, action.elseAction()), EOL, statements(), Optional(EOL)),
                        action.elseAction())),
                ENDIF, EOL), action.ifAction());
    }

    Rule whileStatement() {
        WhileAction action = new WhileAction();
        return Sequence(Sequence(
                Sequence(WHILE, WHITESPACE, expression(), action),
                EOL, statements(), 
                ENDWHILE, EOL), action);
    }

    Rule returnStatement() {
        return Sequence(RETURN, Optional(WHITESPACE, expression()),
                EOL, new ReturnAction());
    }

    Rule methodCallStatement() {
        MethodCallAction action = new MethodCallAction();
        return Sequence(CALLMETHOD, WHITESPACE, variable(),
                ZeroOrMore(Sequence(WHITESPACE, expression())), Sequence(EOL, action),
                Optional(ASSIGNVARIABLEFROMMETHODCALL, WHITESPACE, variable(), EOL),
                action);
    }

    Rule expression() {
        return FirstOf(number(), bool(), variable());
    }

    Rule twoOperandStatement(String operation, Action action) {
       return Sequence(Sequence(operation, WHITESPACE, variable(), WHITESPACE,
               expression(), EOL), action);
    }

    Rule variable() {
        return Sequence(variableName(), push(match()));
    }

    Rule bool() {
        return Sequence(FirstOf(TRUE, FALSE), push(TRUE.equals(match()) ? "1" : "0"));
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
