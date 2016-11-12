package com.gotc.components;

import com.gotc.nodes.GOTNode;
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
import com.gotc.util.Constants;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.ArrayBuilder;
import org.parboiled.support.StringVar;

/**
 * Created by srikaram on 06-Nov-16.
 */
@SuppressWarnings("WeakerAccess")
@BuildParseTree
public class GOTParser extends GOTBaseParser implements Constants {

    final Rule EOL = Sequence(ZeroOrMore(FirstOf("\t", "\r", " ")), OneOrMore("\n"), ZeroOrMore(FirstOf("\t", "\r", " ", "\n")));
    final Rule WHITESPACE = FirstOf(OneOrMore(" "), OneOrMore("\t"));

    final String className;

    public GOTParser(String className) {
        this.className = className;
    }

    public Rule realRoot() {
        StringVar fileNameVar = new StringVar(className);
        return Sequence(Optional(EOL), BEGINPROGRAM, EOL,
                abstractMethod(), ENDPROGRAM, Optional(EOL), EOI,
                addAll(new RootNode(fileNameVar)));
    }

    Rule abstractMethod() {
        return ZeroOrMore(FirstOf(mainMethod(), method()));
    }

    Rule mainMethod() {
        return Sequence(BEGINMAIN, EOL, statements(),
                 ENDMAIN, EOL, addAll(new MainMethodNode()));
    }

    Rule method() {
        StringVar methodName = new StringVar();
        GOTArrayBuilder<StringVar> args = new GOTArrayBuilder<>();
        StringVar isNonVoid = new StringVar();
        return Sequence(Sequence(DECLAREMETHOD, WHITESPACE, variable(methodName), EOL,
                ZeroOrMore(Sequence(METHODARGUMENTS, WHITESPACE,
                        variable(args.addAndGet(new StringVar())), EOL)),
                Sequence(Optional(Sequence(NONVOIDMETHOD, EOL)),
                        isNonVoid.set(NONVOIDMETHOD.equals(match().trim()) ?
                                "true" : "false"))),
                statements(),
                ENDMETHODDECLARATION, EOL, addAll(new MethodDeclarationNode(methodName, args.get(), isNonVoid)));
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
                addAll(new PrintNode(expr)));
    }

    Rule assignVariableStatement() {
        StringVar var = new StringVar();
        StringVar expr = new StringVar();
        return Sequence(ASSIGNVARIABLE, WHITESPACE, variable(var),
                WHITESPACE, expression(expr), EOL,
                addAll(new VariableAssignNode(var, expr)));
    }

    Rule logicalStatement() {
        StringVar andVar = new StringVar();
        StringVar andExpr = new StringVar();
        Rule and = twoOperandStatement(AND, andVar, andExpr,
                new AndNode(andVar, andExpr));
        StringVar orVar = new StringVar();
        StringVar orExpr = new StringVar();
        Rule or = twoOperandStatement(OR, orVar, orExpr,
                new OrNode(orVar, orExpr));
        StringVar notVar = new StringVar();
        Rule not = Sequence(NOT, WHITESPACE, variable(notVar),
                addAll(new NotNode(notVar)));
        return Sequence(OneOrMore(FirstOf(and, or, not)), Optional(EOL));
    }

    Rule arithmeticStatement() {
        StringVar plusVar = new StringVar();
        StringVar plusExpr = new StringVar();
        Rule plus = twoOperandStatement(PLUSOPERATOR, plusVar, plusExpr,
                new PlusOpNode(plusVar, plusExpr));
        StringVar minusVar = new StringVar();
        StringVar minusExpr = new StringVar();
        Rule minus = twoOperandStatement(MINUSOPERATOR, minusVar, minusExpr,
                new MinusOpNode(minusVar, minusExpr));
        StringVar multiplyVar = new StringVar();
        StringVar multiplyExpr = new StringVar();
        Rule multiply = twoOperandStatement(MULTIPLICATIONOPERATOR, multiplyVar, multiplyExpr,
                new MultiplyOpNode(multiplyVar, multiplyExpr));
        StringVar divideVar = new StringVar();
        StringVar divideExpr = new StringVar();
        Rule division = twoOperandStatement(DIVISIONOPERATOR, divideVar, divideExpr,
                new DivideOpNode(divideVar, divideExpr));
        return Sequence(OneOrMore(FirstOf(plus, minus, multiply, division)), Optional(EOL));
    }

    Rule conditionalStatement() {
        StringVar lesserThanVar = new StringVar();
        StringVar lesserThanExpr = new StringVar();
        Rule lesserThan = twoOperandStatement(LESSERTHAN, lesserThanVar, lesserThanExpr,
                new LesserThanNode(lesserThanVar, lesserThanExpr));
        StringVar equalToVar = new StringVar();
        StringVar equalToExpr = new StringVar();
        Rule equalsTo = twoOperandStatement(EQUALTO, equalToVar, equalToExpr,
                new EqualToNode(equalToVar, equalToExpr));
        return Sequence(OneOrMore(FirstOf(lesserThan, equalsTo)), Optional(EOL));
    }

    Rule ifStatement() {
        StringVar expression = new StringVar();
        IfElseNode node = new IfElseNode();
        GOTArrayBuilder<GOTNode> nodes = new GOTArrayBuilder<>();
        return Sequence(Sequence(IF, create), WHITESPACE, expression(expression),
                EOL, Optional(statements()),
                ZeroOrMore(Sequence(
                        Sequence(ELSE, create), EOL, Optional(statements()), Optional(EOL)),
                        addAll(node.elseNode())),
                ENDIF, EOL, addAll(node.ifNode(expression)));
    }

    Rule whileStatement() {
        StringVar expression = new StringVar();
        return Sequence(
                Sequence(Sequence(WHILE, create), WHITESPACE, expression(expression)),
                EOL, statements(),
                ENDWHILE, EOL, addAll(new WhileNode(expression)));
    }

    Rule returnStatement() {
        StringVar expression = new StringVar();
        return Sequence(RETURN, Optional(WHITESPACE, expression(expression)),
                EOL, addAll(new ReturnNode(expression)));
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
                addAll(new MethodCallNode(methodName, args.get(), resultingVar)));
    }

    @SuppressSubnodes
    Rule expression(StringVar expression) {
        return FirstOf(number(expression), bool(expression), variable(expression));
    }

    Rule twoOperandStatement(String operation, StringVar variable, StringVar expression, GOTNode node) {
       return Sequence(operation, WHITESPACE, variable(variable), WHITESPACE,
               expression(expression), EOL, addAll(node));
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
