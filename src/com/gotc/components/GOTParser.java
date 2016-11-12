package com.gotc.components;

import com.gotc.nodes.RootNode;
import com.gotc.nodes.arithmetic.DivideOpNode;
import com.gotc.nodes.arithmetic.MinusOpNode;
import com.gotc.nodes.arithmetic.MultiplyOpNode;
import com.gotc.nodes.arithmetic.PlusOpNode;
import com.gotc.nodes.conditional.EqualToNode;
import com.gotc.nodes.conditional.LesserThanNode;
import com.gotc.nodes.logical.AndNode;
import com.gotc.nodes.logical.NotNode;
import com.gotc.nodes.logical.OrNode;
import com.gotc.nodes.method.MainMethodNode;
import com.gotc.nodes.method.MethodCallNode;
import com.gotc.nodes.method.MethodDeclarationNode;
import com.gotc.nodes.statements.*;
import com.gotc.util.Dialogues;
import com.gotc.util.Util;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 06-Nov-16.
 */
@SuppressWarnings("WeakerAccess")
@BuildParseTree
public class GOTParser extends GOTBaseParser implements Dialogues {

    final Rule EOL = Sequence(ZeroOrMore(FirstOf("\t", "\r", " ")), OneOrMore("\n"), ZeroOrMore(FirstOf("\t", "\r", " ", "\n")));
    final Rule WHITESPACE = FirstOf(OneOrMore(" "), OneOrMore("\t"));

    final String className;

    public GOTParser(String className) {
        this.className = className;
    }

    public Rule realRoot() {
        StringVar fileNameVar = new StringVar(className);
        return Sequence(Optional(EOL), BEGINCLASS, EOL,
                abstractMethod(), ENDCLASS, Optional(EOL), EOI,
                addAll(new RootNode(fileNameVar)));
    }

    Rule abstractMethod() {
        return ZeroOrMore(FirstOf(mainMethod(), method()));
    }

    Rule mainMethod() {
        return Sequence(Sequence(BEGINMAIN, pushSize), EOL, statements(),
                 ENDMAIN, EOL, addAll(new MainMethodNode()));
    }

    Rule method() {
        StringVar methodName = new StringVar();
        GOTArrayBuilder<StringVar> args = new GOTArrayBuilder<>();
        StringVar isNonVoid = new StringVar();
        return Sequence(Sequence(Sequence(DECLAREMETHOD, pushSize), WHITESPACE, variable(methodName), EOL,
                ZeroOrMore(Sequence(METHODARGUMENTS, WHITESPACE,
                        variable(args.addAndGet(new StringVar())), EOL)),
                Sequence(Optional(Sequence(NONVOIDMETHOD, EOL)),
                        isNonVoid.set(NONVOIDMETHOD.equals(match().trim()) ?
                                "true" : "false"))),
                statements(),
                ENDMETHODDECLARATION, EOL,
                addAll(new MethodDeclarationNode(methodName, Util.cast(args.get()), isNonVoid)));
    }

    Rule statements() {
        return Sequence(
                ZeroOrMore(FirstOf(printStatement(),
                        assignVariableStatement(),
                        logicalStatement(),
                        arithmeticStatement(),
                        conditionalStatement(),
                        ifStatement(), whileStatement(),
                        returnStatement(), methodCallStatement())),
                Optional(EOL));
    }

    Rule printStatement() {
        StringVar expr = new StringVar();
        return Sequence(PRINT, WHITESPACE,
                FirstOf(number(expr), string(expr)), EOL,
                push(new PrintNode(expr)));
    }

    Rule assignVariableStatement() {
        StringVar var = new StringVar();
        StringVar expr = new StringVar();
        return Sequence(ASSIGNVARIABLE, WHITESPACE, variable(var),
                WHITESPACE, expression(expr), EOL,
                push(new VariableAssignNode(var, expr)));
    }

    Rule logicalStatement() {
        StringVar andVar = new StringVar();
        StringVar andExpr = new StringVar();
        Rule and = Sequence(twoOperandStatement(AND, andVar, andExpr),
                push(new AndNode(andVar, andExpr)));
        StringVar orVar = new StringVar();
        StringVar orExpr = new StringVar();
        Rule or = Sequence(twoOperandStatement(OR, orVar, orExpr),
                push(new OrNode(orVar, orExpr)));
        StringVar notVar = new StringVar();
        Rule not = Sequence(NOT, WHITESPACE, variable(notVar),
                push(new NotNode(notVar)));
        return Sequence(OneOrMore(FirstOf(and, or, not)), Optional(EOL));
    }

    Rule arithmeticStatement() {
        StringVar plusVar = new StringVar();
        StringVar plusExpr = new StringVar();
        Rule plus = Sequence(twoOperandStatement(PLUSOPERATOR, plusVar, plusExpr),
                push(new PlusOpNode(plusVar, plusExpr)));
        StringVar minusVar = new StringVar();
        StringVar minusExpr = new StringVar();
        Rule minus = Sequence(twoOperandStatement(MINUSOPERATOR, minusVar, minusExpr),
                push(new MinusOpNode(minusVar, minusExpr)));
        StringVar multiplyVar = new StringVar();
        StringVar multiplyExpr = new StringVar();
        Rule multiply = Sequence(twoOperandStatement(MULTIPLICATIONOPERATOR, multiplyVar, multiplyExpr),
                push(new MultiplyOpNode(multiplyVar, multiplyExpr)));
        StringVar divideVar = new StringVar();
        StringVar divideExpr = new StringVar();
        Rule division = Sequence(twoOperandStatement(DIVISIONOPERATOR, divideVar, divideExpr),
                push(new DivideOpNode(divideVar, divideExpr)));
        return Sequence(OneOrMore(FirstOf(plus, minus, multiply, division)), Optional(EOL));
    }

    Rule conditionalStatement() {
        StringVar lesserThanVar = new StringVar();
        StringVar lesserThanExpr = new StringVar();
        Rule lesserThan = Sequence(twoOperandStatement(LESSERTHAN, lesserThanVar, lesserThanExpr),
                push(new LesserThanNode(lesserThanVar, lesserThanExpr)));
        StringVar equalToVar = new StringVar();
        StringVar equalToExpr = new StringVar();
        Rule equalsTo = Sequence(twoOperandStatement(EQUALTO, equalToVar, equalToExpr),
                push(new EqualToNode(equalToVar, equalToExpr)));
        return Sequence(OneOrMore(FirstOf(lesserThan, equalsTo)), Optional(EOL));
    }

    Rule ifStatement() {
        StringVar expression = new StringVar();
        IfElseNode node = new IfElseNode();
        return Sequence(Sequence(IF, pushSize), WHITESPACE, expression(expression),
                EOL, Optional(statements()),
                ZeroOrMore(Sequence(
                        Sequence(ELSE, pushSize), EOL, Optional(statements()), Optional(EOL)),
                        addAll(node.elseNode())),
                ENDIF, EOL, addAll(node.ifNode(expression)));
    }

    Rule whileStatement() {
        StringVar expression = new StringVar();
        return Sequence(
                Sequence(Sequence(WHILE, pushSize), WHITESPACE, expression(expression)),
                EOL, statements(),
                ENDWHILE, EOL, addAll(new WhileNode(expression)));
    }

    Rule returnStatement() {
        StringVar expression = new StringVar();
        return Sequence(RETURN, Optional(WHITESPACE, expression(expression)),
                EOL, push(new ReturnNode(expression)));
    }

    Rule methodCallStatement() {
        StringVar methodName = new StringVar();
        GOTArrayBuilder<StringVar> args = new GOTArrayBuilder<>();
        StringVar resultingVar = new StringVar();
        return Sequence(CALLMETHOD, WHITESPACE,
                variable(methodName),
                ZeroOrMore(Sequence(WHITESPACE,
                        expression(args.addAndGet(new StringVar())))), EOL,
                Optional(ASSIGNVARIABLEFROMMETHODCALL, WHITESPACE,
                        variable(resultingVar), EOL),
                push(new MethodCallNode(methodName, Util.cast(args.get()), resultingVar)));
    }

    @SuppressSubnodes
    Rule expression(StringVar expression) {
        return FirstOf(number(expression), bool(expression), variable(expression));
    }

    Rule twoOperandStatement(String operation, StringVar variable, StringVar expression) {
       return Sequence(operation, WHITESPACE, variable(variable), WHITESPACE,
               expression(expression), EOL);
    }

    Rule variable(StringVar match) {
        return Sequence(variableName(), match.set(match()));
    }

    Rule bool(StringVar match) {
        return Sequence(FirstOf(TRUE, FALSE), match.set(TRUE.equals(match()) ? "1" : "0"));
    }

    Rule variableName() {
        return Sequence(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z')),
                ZeroOrMore(FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'), CharRange('0', '9'))));
    }

    Rule number(StringVar match) {
        return FirstOf(
                Sequence(OneOrMore(CharRange('0', '9')), match.set(match())),
                Sequence(Sequence("-", OneOrMore(CharRange('0', '9'))), match.set(match())));
    }

    Rule string(StringVar match) {
        return Sequence(OneOrMore(NoneOf("\n\\\r\"")), match.set(match()));
    }

}
