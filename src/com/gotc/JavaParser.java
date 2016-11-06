package com.gotc;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class JavaParser extends BaseParser<Object> {

    //-------------------------------------------------------------------------
    //  Compilation Unit
    //-------------------------------------------------------------------------

    public Rule CompilationUnit() {
        return Sequence(
                Spacing(),
                Optional(PackageDeclaration()),
                ZeroOrMore(ImportDeclaration()),
                ZeroOrMore(TypeDeclaration()),
                EOI
        );
    }

    private Rule PackageDeclaration() {
        return Sequence(ZeroOrMore(Annotation()), Sequence(PACKAGE, QualifiedIdentifier(), SEMI));
    }

    private Rule ImportDeclaration() {
        return Sequence(
                IMPORT,
                Optional(STATIC),
                QualifiedIdentifier(),
                Optional(DOT, STAR),
                SEMI
        );
    }

    private Rule TypeDeclaration() {
        return FirstOf(
                Sequence(
                        ZeroOrMore(Modifier()),
                        FirstOf(
                                ClassDeclaration(),
                                EnumDeclaration(),
                                InterfaceDeclaration(),
                                AnnotationTypeDeclaration()
                        )
                ),
                SEMI
        );
    }

    //-------------------------------------------------------------------------
    //  Class Declaration
    //-------------------------------------------------------------------------

    private Rule ClassDeclaration() {
        return Sequence(
                CLASS,
                Identifier(),
                Optional(TypeParameters()),
                Optional(EXTENDS, ClassType()),
                Optional(IMPLEMENTS, ClassTypeList()),
                ClassBody()
        );
    }

    private Rule ClassBody() {
        return Sequence(LWING, ZeroOrMore(ClassBodyDeclaration()), RWING);
    }

    private Rule ClassBodyDeclaration() {
        return FirstOf(
                SEMI,
                Sequence(Optional(STATIC), Block()),
                Sequence(ZeroOrMore(Modifier()), MemberDecl())
        );
    }

    private Rule MemberDecl() {
        return FirstOf(
                Sequence(TypeParameters(), GenericMethodOrConstructorRest()),
                Sequence(Type(), Identifier(), MethodDeclaratorRest()),
                Sequence(Type(), VariableDeclarators(), SEMI),
                Sequence(VOID, Identifier(), VoidMethodDeclaratorRest()),
                Sequence(Identifier(), ConstructorDeclaratorRest()),
                InterfaceDeclaration(),
                ClassDeclaration(),
                EnumDeclaration(),
                AnnotationTypeDeclaration()
        );
    }

    private Rule GenericMethodOrConstructorRest() {
        return FirstOf(
                Sequence(FirstOf(Type(), VOID), Identifier(), MethodDeclaratorRest()),
                Sequence(Identifier(), ConstructorDeclaratorRest())
        );
    }

    private Rule MethodDeclaratorRest() {
        return Sequence(
                FormalParameters(),
                ZeroOrMore(Dim()),
                Optional(THROWS, ClassTypeList()),
                FirstOf(MethodBody(), SEMI)
        );
    }

    private Rule VoidMethodDeclaratorRest() {
        return Sequence(
                FormalParameters(),
                Optional(THROWS, ClassTypeList()),
                FirstOf(MethodBody(), SEMI)
        );
    }

    private Rule ConstructorDeclaratorRest() {
        return Sequence(FormalParameters(), Optional(THROWS, ClassTypeList()), MethodBody());
    }

    private Rule MethodBody() {
        return Block();
    }

    //-------------------------------------------------------------------------
    //  Interface Declaration
    //-------------------------------------------------------------------------

    private Rule InterfaceDeclaration() {
        return Sequence(
                INTERFACE,
                Identifier(),
                Optional(TypeParameters()),
                Optional(EXTENDS, ClassTypeList()),
                InterfaceBody()
        );
    }

    private Rule InterfaceBody() {
        return Sequence(LWING, ZeroOrMore(InterfaceBodyDeclaration()), RWING);
    }

    private Rule InterfaceBodyDeclaration() {
        return FirstOf(
                Sequence(ZeroOrMore(Modifier()), InterfaceMemberDecl()),
                SEMI
        );
    }

    private Rule InterfaceMemberDecl() {
        return FirstOf(
                InterfaceMethodOrFieldDecl(),
                InterfaceGenericMethodDecl(),
                Sequence(VOID, Identifier(), VoidInterfaceMethodDeclaratorsRest()),
                InterfaceDeclaration(),
                AnnotationTypeDeclaration(),
                ClassDeclaration(),
                EnumDeclaration()
        );
    }

    private Rule InterfaceMethodOrFieldDecl() {
        return Sequence(Sequence(Type(), Identifier()), InterfaceMethodOrFieldRest());
    }

    private Rule InterfaceMethodOrFieldRest() {
        return FirstOf(
                Sequence(ConstantDeclaratorsRest(), SEMI),
                InterfaceMethodDeclaratorRest()
        );
    }

    private Rule InterfaceMethodDeclaratorRest() {
        return Sequence(
                FormalParameters(),
                ZeroOrMore(Dim()),
                Optional(THROWS, ClassTypeList()),
                SEMI
        );
    }

    private Rule InterfaceGenericMethodDecl() {
        return Sequence(TypeParameters(), FirstOf(Type(), VOID), Identifier(), InterfaceMethodDeclaratorRest());
    }

    private Rule VoidInterfaceMethodDeclaratorsRest() {
        return Sequence(FormalParameters(), Optional(THROWS, ClassTypeList()), SEMI);
    }

    private Rule ConstantDeclaratorsRest() {
        return Sequence(ConstantDeclaratorRest(), ZeroOrMore(COMMA, ConstantDeclarator()));
    }

    private Rule ConstantDeclarator() {
        return Sequence(Identifier(), ConstantDeclaratorRest());
    }

    private Rule ConstantDeclaratorRest() {
        return Sequence(ZeroOrMore(Dim()), EQU, VariableInitializer());
    }

    //-------------------------------------------------------------------------
    //  Enum Declaration
    //-------------------------------------------------------------------------

    private Rule EnumDeclaration() {
        return Sequence(
                ENUM,
                Identifier(),
                Optional(IMPLEMENTS, ClassTypeList()),
                EnumBody()
        );
    }

    private Rule EnumBody() {
        return Sequence(
                LWING,
                Optional(EnumConstants()),
                Optional(COMMA),
                Optional(EnumBodyDeclarations()),
                RWING
        );
    }

    private Rule EnumConstants() {
        return Sequence(EnumConstant(), ZeroOrMore(COMMA, EnumConstant()));
    }

    private Rule EnumConstant() {
        return Sequence(
                ZeroOrMore(Annotation()),
                Identifier(),
                Optional(Arguments()),
                Optional(ClassBody())
        );
    }

    private Rule EnumBodyDeclarations() {
        return Sequence(SEMI, ZeroOrMore(ClassBodyDeclaration()));
    }

    //-------------------------------------------------------------------------
    //  Variable Declarations
    //-------------------------------------------------------------------------

    private Rule LocalVariableDeclarationStatement() {
        return Sequence(ZeroOrMore(FirstOf(FINAL, Annotation())), Type(), VariableDeclarators(), SEMI);
    }

    private Rule VariableDeclarators() {
        return Sequence(VariableDeclarator(), ZeroOrMore(COMMA, VariableDeclarator()));
    }

    private Rule VariableDeclarator() {
        return Sequence(Identifier(), ZeroOrMore(Dim()), Optional(EQU, VariableInitializer()));
    }

    //-------------------------------------------------------------------------
    //  Formal Parameters
    //-------------------------------------------------------------------------

    private Rule FormalParameters() {
        return Sequence(LPAR, Optional(FormalParameterDecls()), RPAR);
    }

    private Rule FormalParameter() {
        return Sequence(ZeroOrMore(FirstOf(FINAL, Annotation())), Type(), VariableDeclaratorId());
    }

    private Rule FormalParameterDecls() {
        return Sequence(ZeroOrMore(FirstOf(FINAL, Annotation())), Type(), FormalParameterDeclsRest());
    }

    private Rule FormalParameterDeclsRest() {
        return FirstOf(
                Sequence(VariableDeclaratorId(), Optional(COMMA, FormalParameterDecls())),
                Sequence(ELLIPSIS, VariableDeclaratorId())
        );
    }

    private Rule VariableDeclaratorId() {
        return Sequence(Identifier(), ZeroOrMore(Dim()));
    }

    //-------------------------------------------------------------------------
    //  Statements
    //-------------------------------------------------------------------------

    private Rule Block() {
        return Sequence(LWING, BlockStatements(), RWING);
    }

    private Rule BlockStatements() {
        return ZeroOrMore(BlockStatement());
    }

    private Rule BlockStatement() {
        return FirstOf(
                LocalVariableDeclarationStatement(),
                Sequence(ZeroOrMore(Modifier()), FirstOf(ClassDeclaration(), EnumDeclaration())),
                Statement()
        );
    }

    private Rule Statement() {
        return FirstOf(
                Block(),
                Sequence(ASSERT, Expression(), Optional(COLON, Expression()), SEMI),
                Sequence(IF, ParExpression(), Statement(), Optional(ELSE, Statement())),
                Sequence(FOR, LPAR, Optional(ForInit()), SEMI, Optional(Expression()), SEMI, Optional(ForUpdate()),
                        RPAR, Statement()),
                Sequence(FOR, LPAR, FormalParameter(), COLON, Expression(), RPAR, Statement()),
                Sequence(WHILE, ParExpression(), Statement()),
                Sequence(DO, Statement(), WHILE, ParExpression(), SEMI),
                Sequence(TRY, Block(),
                        FirstOf(Sequence(OneOrMore(Catch_()), Optional(Finally_())), Finally_())),
                Sequence(SWITCH, ParExpression(), LWING, SwitchBlockStatementGroups(), RWING),
                Sequence(SYNCHRONIZED, ParExpression(), Block()),
                Sequence(RETURN, Optional(Expression()), SEMI),
                Sequence(THROW, Expression(), SEMI),
                Sequence(BREAK, Optional(Identifier()), SEMI),
                Sequence(CONTINUE, Optional(Identifier()), SEMI),
                Sequence(Sequence(Identifier(), COLON), Statement()),
                Sequence(StatementExpression(), SEMI),
                SEMI
        );
    }

    private Rule Catch_() {
        return Sequence(CATCH, LPAR, FormalParameter(), RPAR, Block());
    }

    private Rule Finally_() {
        return Sequence(FINALLY, Block());
    }

    private Rule SwitchBlockStatementGroups() {
        return ZeroOrMore(SwitchBlockStatementGroup());
    }

    private Rule SwitchBlockStatementGroup() {
        return Sequence(SwitchLabel(), BlockStatements());
    }

    private Rule SwitchLabel() {
        return FirstOf(
                Sequence(CASE, ConstantExpression(), COLON),
                Sequence(CASE, EnumConstantName(), COLON),
                Sequence(DEFAULT, COLON)
        );
    }

    private Rule ForInit() {
        return FirstOf(
                Sequence(ZeroOrMore(FirstOf(FINAL, Annotation())), Type(), VariableDeclarators()),
                Sequence(StatementExpression(), ZeroOrMore(COMMA, StatementExpression()))
        );
    }

    private Rule ForUpdate() {
        return Sequence(StatementExpression(), ZeroOrMore(COMMA, StatementExpression()));
    }

    private Rule EnumConstantName() {
        return Identifier();
    }

    //-------------------------------------------------------------------------
    //  Expressions
    //-------------------------------------------------------------------------

    // The following is more generous than the definition in section 14.8,
    // which allows only specific forms of Expression.

    private Rule StatementExpression() {
        return Expression();
    }

    private Rule ConstantExpression() {
        return Expression();
    }

    // The following definition is part of the modification in JLS Chapter 18
    // to minimize look ahead. In JLS Chapter 15.27, Expression is defined
    // as AssignmentExpression, which is effectively defined as
    // (LeftHandSide AssignmentOperator)* ConditionalExpression.
    // The following is obtained by allowing ANY ConditionalExpression
    // as LeftHandSide, which results in accepting statements like 5 = a.

    private Rule Expression() {
        return Sequence(
                ConditionalExpression(),
                ZeroOrMore(AssignmentOperator(), ConditionalExpression())
        );
    }

    private Rule AssignmentOperator() {
        return FirstOf(EQU, PLUSEQU, MINUSEQU, STAREQU, DIVEQU, ANDEQU, OREQU, HATEQU, MODEQU, SLEQU, SREQU, BSREQU);
    }

    private Rule ConditionalExpression() {
        return Sequence(
                ConditionalOrExpression(),
                ZeroOrMore(QUERY, Expression(), COLON, ConditionalOrExpression())
        );
    }

    private Rule ConditionalOrExpression() {
        return Sequence(
                ConditionalAndExpression(),
                ZeroOrMore(OROR, ConditionalAndExpression())
        );
    }

    private Rule ConditionalAndExpression() {
        return Sequence(
                InclusiveOrExpression(),
                ZeroOrMore(ANDAND, InclusiveOrExpression())
        );
    }

    private Rule InclusiveOrExpression() {
        return Sequence(
                ExclusiveOrExpression(),
                ZeroOrMore(OR, ExclusiveOrExpression())
        );
    }

    private Rule ExclusiveOrExpression() {
        return Sequence(
                AndExpression(),
                ZeroOrMore(HAT, AndExpression())
        );
    }

    private Rule AndExpression() {
        return Sequence(
                EqualityExpression(),
                ZeroOrMore(AND, EqualityExpression())
        );
    }

    private Rule EqualityExpression() {
        return Sequence(
                RelationalExpression(),
                ZeroOrMore(FirstOf(EQUAL, NOTEQUAL), RelationalExpression())
        );
    }

    private Rule RelationalExpression() {
        return Sequence(
                ShiftExpression(),
                ZeroOrMore(
                        FirstOf(
                                Sequence(FirstOf(LE, GE, LT, GT), ShiftExpression()),
                                Sequence(INSTANCEOF, ReferenceType())
                        )
                )
        );
    }

    private Rule ShiftExpression() {
        return Sequence(
                AdditiveExpression(),
                ZeroOrMore(FirstOf(SL, SR, BSR), AdditiveExpression())
        );
    }

    private Rule AdditiveExpression() {
        return Sequence(
                MultiplicativeExpression(),
                ZeroOrMore(FirstOf(PLUS, MINUS), MultiplicativeExpression())
        );
    }

    private Rule MultiplicativeExpression() {
        return Sequence(
                UnaryExpression(),
                ZeroOrMore(FirstOf(STAR, DIV, MOD), UnaryExpression())
        );
    }

    private Rule UnaryExpression() {
        return FirstOf(
                Sequence(PrefixOp(), UnaryExpression()),
                Sequence(LPAR, Type(), RPAR, UnaryExpression()),
                Sequence(Primary(), ZeroOrMore(Selector()), ZeroOrMore(PostFixOp()))
        );
    }

    private Rule Primary() {
        return FirstOf(
                ParExpression(),
                Sequence(
                        NonWildcardTypeArguments(),
                        FirstOf(ExplicitGenericInvocationSuffix(), Sequence(THIS, Arguments()))
                ),
                Sequence(THIS, Optional(Arguments())),
                Sequence(SUPER, SuperSuffix()),
                Literal(),
                Sequence(NEW, Creator()),
                Sequence(QualifiedIdentifier(), Optional(IdentifierSuffix())),
                Sequence(BasicType(), ZeroOrMore(Dim()), DOT, CLASS),
                Sequence(VOID, DOT, CLASS)
        );
    }

    private Rule IdentifierSuffix() {
        return FirstOf(
                Sequence(LBRK,
                        FirstOf(
                                Sequence(RBRK, ZeroOrMore(Dim()), DOT, CLASS),
                                Sequence(Expression(), RBRK)
                        )
                ),
                Arguments(),
                Sequence(
                        DOT,
                        FirstOf(
                                CLASS,
                                ExplicitGenericInvocation(),
                                THIS,
                                Sequence(SUPER, Arguments()),
                                Sequence(NEW, Optional(NonWildcardTypeArguments()), InnerCreator())
                        )
                )
        );
    }

    private Rule ExplicitGenericInvocation() {
        return Sequence(NonWildcardTypeArguments(), ExplicitGenericInvocationSuffix());
    }

    private Rule NonWildcardTypeArguments() {
        return Sequence(LPOINT, ReferenceType(), ZeroOrMore(COMMA, ReferenceType()), RPOINT);
    }

    private Rule ExplicitGenericInvocationSuffix() {
        return FirstOf(
                Sequence(SUPER, SuperSuffix()),
                Sequence(Identifier(), Arguments())
        );
    }

    private Rule PrefixOp() {
        return FirstOf(INC, DEC, BANG, TILDA, PLUS, MINUS);
    }

    private Rule PostFixOp() {
        return FirstOf(INC, DEC);
    }

    private Rule Selector() {
        return FirstOf(
                Sequence(DOT, Identifier(), Optional(Arguments())),
                Sequence(DOT, ExplicitGenericInvocation()),
                Sequence(DOT, THIS),
                Sequence(DOT, SUPER, SuperSuffix()),
                Sequence(DOT, NEW, Optional(NonWildcardTypeArguments()), InnerCreator()),
                DimExpr()
        );
    }

    private Rule SuperSuffix() {
        return FirstOf(Arguments(), Sequence(DOT, Identifier(), Optional(Arguments())));
    }

    @MemoMismatches
    private Rule BasicType() {
        return Sequence(
                FirstOf("byte", "short", "char", "int", "long", "float", "double", "boolean"),
                TestNot(LetterOrDigit()),
                Spacing()
        );
    }

    private Rule Arguments() {
        return Sequence(
                LPAR,
                Optional(Expression(), ZeroOrMore(COMMA, Expression())),
                RPAR
        );
    }

    private Rule Creator() {
        return FirstOf(
                Sequence(Optional(NonWildcardTypeArguments()), CreatedName(), ClassCreatorRest()),
                Sequence(Optional(NonWildcardTypeArguments()), FirstOf(ClassType(), BasicType()), ArrayCreatorRest())
        );
    }

    private Rule CreatedName() {
        return Sequence(
                Identifier(), Optional(NonWildcardTypeArguments()),
                ZeroOrMore(DOT, Identifier(), Optional(NonWildcardTypeArguments()))
        );
    }

    private Rule InnerCreator() {
        return Sequence(Identifier(), ClassCreatorRest());
    }

    // The following is more generous than JLS 15.10. According to that definition,
    // BasicType must be followed by at least one DimExpr or by ArrayInitializer.
    private Rule ArrayCreatorRest() {
        return Sequence(
                LBRK,
                FirstOf(
                        Sequence(RBRK, ZeroOrMore(Dim()), ArrayInitializer()),
                        Sequence(Expression(), RBRK, ZeroOrMore(DimExpr()), ZeroOrMore(Dim()))
                )
        );
    }

    private Rule ClassCreatorRest() {
        return Sequence(Arguments(), Optional(ClassBody()));
    }

    private Rule ArrayInitializer() {
        return Sequence(
                LWING,
                Optional(
                        VariableInitializer(),
                        ZeroOrMore(COMMA, VariableInitializer())
                ),
                Optional(COMMA),
                RWING
        );
    }

    private Rule VariableInitializer() {
        return FirstOf(ArrayInitializer(), Expression());
    }

    private Rule ParExpression() {
        return Sequence(LPAR, Expression(), RPAR);
    }

    private Rule QualifiedIdentifier() {
        return Sequence(Identifier(), ZeroOrMore(DOT, Identifier()));
    }

    private Rule Dim() {
        return Sequence(LBRK, RBRK);
    }

    private Rule DimExpr() {
        return Sequence(LBRK, Expression(), RBRK);
    }

    //-------------------------------------------------------------------------
    //  Types and Modifiers
    //-------------------------------------------------------------------------

    private Rule Type() {
        return Sequence(FirstOf(BasicType(), ClassType()), ZeroOrMore(Dim()));
    }

    private Rule ReferenceType() {
        return FirstOf(
                Sequence(BasicType(), OneOrMore(Dim())),
                Sequence(ClassType(), ZeroOrMore(Dim()))
        );
    }

    private Rule ClassType() {
        return Sequence(
                Identifier(), Optional(TypeArguments()),
                ZeroOrMore(DOT, Identifier(), Optional(TypeArguments()))
        );
    }

    private Rule ClassTypeList() {
        return Sequence(ClassType(), ZeroOrMore(COMMA, ClassType()));
    }

    private Rule TypeArguments() {
        return Sequence(LPOINT, TypeArgument(), ZeroOrMore(COMMA, TypeArgument()), RPOINT);
    }

    private Rule TypeArgument() {
        return FirstOf(
                ReferenceType(),
                Sequence(QUERY, Optional(FirstOf(EXTENDS, SUPER), ReferenceType()))
        );
    }

    private Rule TypeParameters() {
        return Sequence(LPOINT, TypeParameter(), ZeroOrMore(COMMA, TypeParameter()), RPOINT);
    }

    private Rule TypeParameter() {
        return Sequence(Identifier(), Optional(EXTENDS, Bound()));
    }

    private Rule Bound() {
        return Sequence(ClassType(), ZeroOrMore(AND, ClassType()));
    }

    // the following common definition of Modifier is part of the modification
    // in JLS Chapter 18 to minimize look ahead. The main body of JLS has
    // different lists of modifiers for different language elements.
    private Rule Modifier() {
        return FirstOf(
                Annotation(),
                Sequence(
                        FirstOf("public", "protected", "private", "static", "abstract", "final", "native",
                                "synchronized", "transient", "volatile", "strictfp"),
                        TestNot(LetterOrDigit()),
                        Spacing()
                )
        );
    }

    //-------------------------------------------------------------------------
    //  Annotations
    //-------------------------------------------------------------------------

    private Rule AnnotationTypeDeclaration() {
        return Sequence(AT, INTERFACE, Identifier(), AnnotationTypeBody());
    }

    private Rule AnnotationTypeBody() {
        return Sequence(LWING, ZeroOrMore(AnnotationTypeElementDeclaration()), RWING);
    }

    private Rule AnnotationTypeElementDeclaration() {
        return FirstOf(
                Sequence(ZeroOrMore(Modifier()), AnnotationTypeElementRest()),
                SEMI
        );
    }

    private Rule AnnotationTypeElementRest() {
        return FirstOf(
                Sequence(Type(), AnnotationMethodOrConstantRest(), SEMI),
                ClassDeclaration(),
                EnumDeclaration(),
                InterfaceDeclaration(),
                AnnotationTypeDeclaration()
        );
    }

    private Rule AnnotationMethodOrConstantRest() {
        return FirstOf(AnnotationMethodRest(), AnnotationConstantRest());
    }

    private Rule AnnotationMethodRest() {
        return Sequence(Identifier(), LPAR, RPAR, Optional(DefaultValue()));
    }

    private Rule AnnotationConstantRest() {
        return VariableDeclarators();
    }

    private Rule DefaultValue() {
        return Sequence(DEFAULT, ElementValue());
    }

    @MemoMismatches
    private Rule Annotation() {
        return Sequence(AT, QualifiedIdentifier(), Optional(AnnotationRest()));
    }

    private Rule AnnotationRest() {
        return FirstOf(NormalAnnotationRest(), SingleElementAnnotationRest());
    }

    private Rule NormalAnnotationRest() {
        return Sequence(LPAR, Optional(ElementValuePairs()), RPAR);
    }

    private Rule ElementValuePairs() {
        return Sequence(ElementValuePair(), ZeroOrMore(COMMA, ElementValuePair()));
    }

    private Rule ElementValuePair() {
        return Sequence(Identifier(), EQU, ElementValue());
    }

    private Rule ElementValue() {
        return FirstOf(ConditionalExpression(), Annotation(), ElementValueArrayInitializer());
    }

    private Rule ElementValueArrayInitializer() {
        return Sequence(LWING, Optional(ElementValues()), Optional(COMMA), RWING);
    }

    private Rule ElementValues() {
        return Sequence(ElementValue(), ZeroOrMore(COMMA, ElementValue()));
    }

    private Rule SingleElementAnnotationRest() {
        return Sequence(LPAR, ElementValue(), RPAR);
    }

    //-------------------------------------------------------------------------
    //  JLS 3.6-7  Spacing
    //-------------------------------------------------------------------------

    @SuppressNode
    private Rule Spacing() {
        return ZeroOrMore(FirstOf(

                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

                // traditional comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),

                // end of line comment
                Sequence(
                        "//",
                        ZeroOrMore(TestNot(AnyOf("\r\n")), ANY),
                        FirstOf("\r\n", '\r', '\n', EOI)
                )
        ));
    }

    //-------------------------------------------------------------------------
    //  JLS 3.8  Identifiers
    //-------------------------------------------------------------------------

    @SuppressSubnodes
    @MemoMismatches
    private Rule Identifier() {
        return Sequence(TestNot(Keyword()), Letter(), ZeroOrMore(LetterOrDigit()), Spacing());
    }

    // JLS defines letters and digits as Unicode characters recognized
    // as such by special Java procedures.

    private Rule Letter() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()), LetterOrDigit());
    }

    @MemoMismatches
    private Rule LetterOrDigit() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()) , LetterOrDigit());
    }

    //-------------------------------------------------------------------------
    //  JLS 3.9  Keywords
    //-------------------------------------------------------------------------

    @MemoMismatches
    private Rule Keyword() {
        return Sequence(
                FirstOf("assert", "break", "case", "catch", "class", "const", "continue", "default", "do", "else",
                        "enum", "extends", "finally", "final", "for", "goto", "if", "implements", "import", "interface",
                        "instanceof", "new", "package", "return", "static", "super", "switch", "synchronized", "this",
                        "throws", "throw", "try", "void", "while"),
                TestNot(LetterOrDigit())
        );
    }

    private final Rule ASSERT = Keyword("assert");
    private final Rule BREAK = Keyword("break");
    private final Rule CASE = Keyword("case");
    private final Rule CATCH = Keyword("catch");
    private final Rule CLASS = Keyword("class");
    private final Rule CONTINUE = Keyword("continue");
    private final Rule DEFAULT = Keyword("default");
    private final Rule DO = Keyword("do");
    private final Rule ELSE = Keyword("else");
    private final Rule ENUM = Keyword("enum");
    private final Rule EXTENDS = Keyword("extends");
    private final Rule FINALLY = Keyword("finally");
    private final Rule FINAL = Keyword("final");
    private final Rule FOR = Keyword("for");
    private final Rule IF = Keyword("if");
    private final Rule IMPLEMENTS = Keyword("implements");
    private final Rule IMPORT = Keyword("import");
    private final Rule INTERFACE = Keyword("interface");
    private final Rule INSTANCEOF = Keyword("instanceof");
    private final Rule NEW = Keyword("new");
    private final Rule PACKAGE = Keyword("package");
    private final Rule RETURN = Keyword("return");
    private final Rule STATIC = Keyword("static");
    private final Rule SUPER = Keyword("super");
    private final Rule SWITCH = Keyword("switch");
    private final Rule SYNCHRONIZED = Keyword("synchronized");
    private final Rule THIS = Keyword("this");
    private final Rule THROWS = Keyword("throws");
    private final Rule THROW = Keyword("throw");
    private final Rule TRY = Keyword("try");
    private final Rule VOID = Keyword("void");
    private final Rule WHILE = Keyword("while");

    @SuppressNode
    @DontLabel
    private Rule Keyword(String keyword) {
        return Terminal(keyword, LetterOrDigit());
    }

    //-------------------------------------------------------------------------
    //  JLS 3.10  Literals
    //-------------------------------------------------------------------------

    private Rule Literal() {
        return Sequence(
                FirstOf(
                        FloatLiteral(),
                        IntegerLiteral(),
                        CharLiteral(),
                        StringLiteral(),
                        Sequence("true", TestNot(LetterOrDigit())),
                        Sequence("false", TestNot(LetterOrDigit())),
                        Sequence("null", TestNot(LetterOrDigit()))
                ),
                Spacing()
        );
    }

    @SuppressSubnodes
    private Rule IntegerLiteral() {
        return Sequence(FirstOf(HexNumeral(), OctalNumeral(), DecimalNumeral()), Optional(AnyOf("lL")));
    }

    @SuppressSubnodes
    private Rule DecimalNumeral() {
        return FirstOf('0', Sequence(CharRange('1', '9'), ZeroOrMore(Digit())));
    }

    @SuppressSubnodes

    @MemoMismatches
    private Rule HexNumeral() {
        return Sequence('0', IgnoreCase('x'), OneOrMore(HexDigit()));
    }

    private Rule HexDigit() {
        return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
    }

    @SuppressSubnodes
    private Rule OctalNumeral() {
        return Sequence('0', OneOrMore(CharRange('0', '7')));
    }

    private Rule FloatLiteral() {
        return FirstOf(HexFloat(), DecimalFloat());
    }

    @SuppressSubnodes
    private Rule DecimalFloat() {
        return FirstOf(
                Sequence(OneOrMore(Digit()), '.', ZeroOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence('.', OneOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Exponent(), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Optional(Exponent()), AnyOf("fFdD"))
        );
    }

    private Rule Exponent() {
        return Sequence(AnyOf("eE"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    private Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressSubnodes
    private Rule HexFloat() {
        return Sequence(HexSignificant(), BinaryExponent(), Optional(AnyOf("fFdD")));
    }

    private Rule HexSignificant() {
        return FirstOf(
                Sequence(FirstOf("0x", "0X"), ZeroOrMore(HexDigit()), '.', OneOrMore(HexDigit())),
                Sequence(HexNumeral(), Optional('.'))
        );
    }

    private Rule BinaryExponent() {
        return Sequence(AnyOf("pP"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    private Rule CharLiteral() {
        return Sequence(
                '\'',
                FirstOf(Escape(), Sequence(TestNot(AnyOf("'\\")), ANY)).suppressSubnodes(),
                '\''
        );
    }

    private Rule StringLiteral() {
        return Sequence(
                '"',
                ZeroOrMore(
                        FirstOf(
                                Escape(),
                                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                '"'
        );
    }

    private Rule Escape() {
        return Sequence('\\', FirstOf(AnyOf("btnfr\"\'\\"), OctalEscape(), UnicodeEscape()));
    }

    private Rule OctalEscape() {
        return FirstOf(
                Sequence(CharRange('0', '3'), CharRange('0', '7'), CharRange('0', '7')),
                Sequence(CharRange('0', '7'), CharRange('0', '7')),
                CharRange('0', '7')
        );
    }

    private Rule UnicodeEscape() {
        return Sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    //-------------------------------------------------------------------------
    //  JLS 3.11-12  Separators, Operators
    //-------------------------------------------------------------------------

    final private Rule AT = Terminal("@");
    final private Rule AND = Terminal("&", AnyOf("=&"));
    final private Rule ANDAND = Terminal("&&");
    final private Rule ANDEQU = Terminal("&=");
    final private Rule BANG = Terminal("!", Ch('='));
    final private Rule BSR = Terminal(">>>", Ch('='));
    final private Rule BSREQU = Terminal(">>>=");
    final private Rule COLON = Terminal(":");
    final private Rule COMMA = Terminal(",");
    final private Rule DEC = Terminal("--");
    final private Rule DIV = Terminal("/", Ch('='));
    final private Rule DIVEQU = Terminal("/=");
    final private Rule DOT = Terminal(".");
    final private Rule ELLIPSIS = Terminal("...");
    final private Rule EQU = Terminal("=", Ch('='));
    final private Rule EQUAL = Terminal("==");
    final private Rule GE = Terminal(">=");
    final private Rule GT = Terminal(">", AnyOf("=>"));
    final private Rule HAT = Terminal("^", Ch('='));
    final private Rule HATEQU = Terminal("^=");
    final private Rule INC = Terminal("++");
    final private Rule LBRK = Terminal("[");
    final private Rule LE = Terminal("<=");
    final private Rule LPAR = Terminal("(");
    final private Rule LPOINT = Terminal("<");
    final private Rule LT = Terminal("<", AnyOf("=<"));
    final private Rule LWING = Terminal("{");
    final private Rule MINUS = Terminal("-", AnyOf("=-"));
    final private Rule MINUSEQU = Terminal("-=");
    final private Rule MOD = Terminal("%", Ch('='));
    final private Rule MODEQU = Terminal("%=");
    final private Rule NOTEQUAL = Terminal("!=");
    final private Rule OR = Terminal("|", AnyOf("=|"));
    final private Rule OREQU = Terminal("|=");
    final private Rule OROR = Terminal("||");
    final private Rule PLUS = Terminal("+", AnyOf("=+"));
    final private Rule PLUSEQU = Terminal("+=");
    final private Rule QUERY = Terminal("?");
    final private Rule RBRK = Terminal("]");
    final private Rule RPAR = Terminal(")");
    final private Rule RPOINT = Terminal(">");
    final private Rule RWING = Terminal("}");
    final private Rule SEMI = Terminal(";");
    final private Rule SL = Terminal("<<", Ch('='));
    final private Rule SLEQU = Terminal("<<=");
    final private Rule SR = Terminal(">>", AnyOf("=>"));
    final private Rule SREQU = Terminal(">>=");
    final private Rule STAR = Terminal("*", Ch('='));
    final private Rule STAREQU = Terminal("*=");
    final private Rule TILDA = Terminal("~");

    //-------------------------------------------------------------------------
    //  helper methods
    //-------------------------------------------------------------------------

    @Override
    protected Rule fromCharLiteral(char c) {
        // turn of creation of parse tree nodes for single characters
        return super.fromCharLiteral(c).suppressNode();
    }

    @SuppressNode
    @DontLabel
    private Rule Terminal(String string) {
        return Sequence(string, Spacing()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    private Rule Terminal(String string, Rule mustNotFollow) {
        return Sequence(string, TestNot(mustNotFollow), Spacing()).label('\'' + string + '\'');
    }
}
