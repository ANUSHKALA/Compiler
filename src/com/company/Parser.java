package com.company;

import java.io.*;



public class Parser {

    public PrintWriter pw;
    public Tokenizer tokenizer;

    public Parser(String name){
        try {
            String outputFile=name.replace(".jack",".xml");
            tokenizer = new Tokenizer(name);
            pw=new PrintWriter(outputFile);
        }
        catch (FileNotFoundException e) {
            System.out.print(e);
        }
    }

    public void tokenType(){

        tokenizer.advance();

        if(tokenizer.tokenType().equalsIgnoreCase("KEYWORD") && ((tokenizer.keyWord().equalsIgnoreCase("int")|| tokenizer.keyWord().equalsIgnoreCase("char") || tokenizer.keyWord().equalsIgnoreCase("boolean")))){
            System.out.println("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
        }
        else if(tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")){
            System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        }
        else if(tokenizer.tokenType().equalsIgnoreCase("KEYWORD")&&((tokenizer.keyWord().equalsIgnoreCase("constructor")||tokenizer.keyWord().equalsIgnoreCase("method")||tokenizer.keyWord().equalsIgnoreCase("function")))){
            System.out.println("<identifier>" + tokenizer.keyWord() + "</identifier>\n");
        }
        else{
            error("Type of token:"+tokenizer.tokenType()+" "+tokenizer.keyWord());
        }

    }

    public void compileClass() {
        if ((!tokenizer.tokenType().equalsIgnoreCase("KEYWORD") && tokenizer.keyWord().equalsIgnoreCase("class"))) {
            error(tokenizer.tokenType() + " class");
        }

        System.out.println("<class>\n");
        pw.print("<class>\n");

        System.out.println("<keyword>class</keyword>\n");
        pw.print("<keyword>class</keyword>\n");


        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")){
            System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        }
        else {
            System.out.println("");
            pw.print("");
            error("class2:" + tokenizer.tokenType());
        }
        requireSymbol('{');

        compileClassVarDec();
        compileSubroutine();

        requireSymbol('}');
        tokenizer.advance();

        if (tokenizer.hasMoreTokens()){

            System.out.println(Tokenizer.index +""+tokenizer.length);
//            pw.print(Tokenizer.index +""+tokenizer.length);

            pw.close();
            throw new IllegalStateException("Unexpected tokens");
        }

        System.out.println("</class>\n");
        pw.print("</class>\n");
        pw.close();
    }

    public void compileClassVarDec(){
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '}'){
            tokenizer.Back();
        }
        if (!tokenizer.tokenType().equalsIgnoreCase("KEYWORD")){
            error("classVarDec2:"+tokenizer.tokenType());
        }
        if (tokenizer.keyWord().equalsIgnoreCase("constructor") || tokenizer.keyWord().equalsIgnoreCase("function") || tokenizer.keyWord().equalsIgnoreCase("method")) {
            tokenizer.Back();
        }
        System.out.println("<classVarDec>\n");
        pw.print("<classVarDec>\n");

//        if (!tokenizer.keyWord().equalsIgnoreCase("STATIC") || !tokenizer.keyWord().equalsIgnoreCase("FIELD")) {
//            error("classVarDec2");
//        }

        System.out.println("<keyword>"+tokenizer.keyWord()+"</keyword>\n");
        pw.print("<keyword>"+tokenizer.keyWord()+"</keyword>\n");

        tokenType();

        do {
            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")) {
                System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            }

            tokenizer.advance();

            if (!tokenizer.tokenType().equalsIgnoreCase("SYMBOL") || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')){
                error("classVarDec3" + tokenizer.symbol());
            }
            if (tokenizer.symbol() == ',') {
                System.out.println("<symbol>,</symbol>\n");
                pw.print("<symbol>,</symbol>\n");
            }
            else {
                System.out.println("<symbol>;</symbol>\n");
                pw.print("<symbol>;</symbol>\n");

                break;
            }
        }while(true);

        System.out.println("</classVarDec>\n");
        pw.print("</classVarDec>\n");

        compileClassVarDec();

    }

    public void compileSubroutine() {
        tokenizer.advance();

        if (!tokenizer.tokenType().equalsIgnoreCase("KEYWORD") || !(tokenizer.keyWord().equalsIgnoreCase("constructor") || tokenizer.keyWord().equalsIgnoreCase("function") || tokenizer.keyWord().equalsIgnoreCase("method"))) {
            error("SubRoutine1=>" + tokenizer.tokenType() + "=>" + tokenizer.keyWord());
        }

        System.out.println("<subroutineDec>\n");
        pw.print("<subroutineDec>\n");
        System.out.println("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
        pw.print("<keyword>" + tokenizer.keyWord() + "</keyword>\n");


        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("KEYWORD") && tokenizer.keyWord().equalsIgnoreCase("void")){
            System.out.println("<keyword>void</keyword>\n");
            pw.print("<keyword>void</keyword>\n");
        }
        else
        {
            tokenizer.Back();
            tokenType();
        }

        tokenizer.advance();
        if (!tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER"))
            error("SubRoutine2");

        System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        requireSymbol('(');

        System.out.println("<parameterList>\n");
        pw.print("<parameterList>\n");
        compileParameterList();
        System.out.println("</parameterList>\n");
        pw.print("</parameterList>\n");

        requireSymbol(')');

        compileSubroutine();
        System.out.println("</subroutineDec>\n");
        pw.print("</subroutineDec>\n");
        compileSubroutine();
    }

    public void compileParameterList()
    {
        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == ')')
        {
            tokenizer.Back();
            return;
        }

        tokenizer.Back();


        do
        {
            tokenType();

            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")) {
                System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            }
            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && (tokenizer.symbol() == ',' || tokenizer.symbol() == ')'))
            {
                if (tokenizer.symbol() == ',') {
                    System.out.println("<symbol>,</symbol>\n");
                    pw.print("<symbol>,</symbol>\n");
                }
                else
                {
                    tokenizer.Back();
                    break;
                }
            }
        }while(true);
    }

    public void compileSubroutineBody()
    {
        System.out.println("<subroutineBody>\n");

        requireSymbol('{');

        compileVarDec();
        System.out.println("<statements>\n");
        pw.print("<statements>\n");
        compileStatement();
        System.out.println("</statements>\n");
        pw.print("</statements>\n");

        requireSymbol('}');

        System.out.println("</subroutineBody>\n");
        pw.print("</subroutineBody>\n");
    }

    public void compileVarDec()
    {
        tokenizer.advance();

        if (!tokenizer.tokenType().equalsIgnoreCase("KEYWORD") || !tokenizer.keyWord().equalsIgnoreCase("VAR"))
        {
            tokenizer.Back();
            return;
        }

        System.out.println("<varDec>\n");
        System.out.println("<keyword>var</keyword>\n");
        pw.print("<varDec>\n");
        pw.print("<keyword>var</keyword>\n");

        tokenType();

        do {
            tokenizer.advance();

            if (!tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")) {
                error("VarDec)");
            }
            System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && (tokenizer.symbol() == ',' || tokenizer.symbol() == ';')) {
                if (tokenizer.symbol() == ',') {
                    System.out.println("<symbol>,</symbol>\n");
                    pw.print("<symbol>,</symbol>\n");
                }
                else{
                    System.out.println("<symbol>;</symbol>\n");
                    pw.print("<symbol>;</symbol>\n");
                    break;
                }
            }

        }while(true);

        System.out.println("</varDec>\n");
        pw.print("</varDec>\n");
        compileVarDec();
    }

    public void compileStatement()
    {
        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '}')
        {
            tokenizer.Back();
            return;
        }
        if (tokenizer.tokenType().equalsIgnoreCase("KEYWORD")) {

            if(tokenizer.keyWord().equalsIgnoreCase("let")) {
                compileLet();
            }
            else if(tokenizer.keyWord().equalsIgnoreCase("if")) {
                compileIf();
            }
            else if(tokenizer.keyWord().equalsIgnoreCase("while")) {
                compileWhile();
            }
            else if(tokenizer.keyWord().equalsIgnoreCase("do")) {
                compileDo();
            }
            else if(tokenizer.keyWord().equalsIgnoreCase("return")) {
                compileReturn();
            }
            else {
                error("statement" + ":" + tokenizer.keyWord());
            }
        }

        compileStatement();
    }

    public void compileSubroutineCall()
    {
        tokenizer.advance();
        if (!tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")) {
            error("subroutinecall1");
        }
        System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '(') {

            System.out.println("<symbol>(</symbol>\n");
            System.out.println("<expressionList>\n");
            pw.print("<symbol>(</symbol>\n");
            pw.print("<expressionList>\n");
            compileExpressionList();
            System.out.println("</expressionList>\n");
            pw.print("</expressionList>\n");
            requireSymbol(')');
        }
        else if(tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '.') {

            System.out.println("<symbol>.</symbol>\n");
            pw.print("<symbol>.</symbol>\n");
            tokenizer.advance();
            if (!tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER"))
                error("subroutinecall2");
            System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            requireSymbol('(');
            System.out.println("<expressionList>\n");
            pw.print("<expressionList>\n");
            compileExpressionList();
            System.out.println("</expressionList>\n");
            pw.print("</expressionList>\n");
            requireSymbol(')');
        }
        else {
            error("subroutinecall3");
        }
    }

    public void compileLet()
    {
        System.out.println("<letStatement>\n");
        System.out.println("<keyword>let</keyword>\n");
        pw.print("<letStatement>\n");
        pw.print("<keyword>let</keyword>\n");

        tokenizer.advance();
        if (!tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER")) {
            error("let1");
        }
        System.out.println("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        pw.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && (tokenizer.symbol() == '[' || tokenizer.symbol() == '='))
        {
            boolean expExist = false;

            if (tokenizer.symbol() == '['){

                expExist = true;
                System.out.println("<symbol>[</symbol>\n");
                pw.write("<symbol>[</symbol>\n");
                compileExpression();

                tokenizer.advance();
                if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == ']') {
                    System.out.println("<symbol>]</symbol>\n");
                    pw.print("<symbol>]</symbol>\n");
                }
                else {
                    error("[");
                }
            }

            if (expExist)
                tokenizer.advance();
            System.out.println("<symbol>=</symbol>\n");
            pw.print("<symbol>=</symbol>\n");
            compileExpression();

            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()==';') {
                System.out.println("<symbol>;</symbol>\n");
                pw.print("<symbol>;</symbol>\n");
            }
            System.out.println("</letStatement>\n");
            pw.print("</letStatement>\n");
        }
    }

    public void compileIf()
    {
        System.out.println("<ifStatement>\n");
        System.out.println("<keyword>if</keyword>\n");
        pw.print("<ifStatement>\n");
        pw.print("<keyword>if</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='(') {
            System.out.println("<symbol>(</symbol>\n");
            pw.print("<symbol>(</symbol>\n");
        }
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()==')') {
            System.out.println("<symbol>)</symbol>\n");
            pw.print("<symbol>)</symbol>\n");
        }

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='{') {
            System.out.println("<symbol>{</symbol>\n");
            pw.print("<symbol>{</symbol>\n");
        }

        System.out.println("<statements>\n");
        pw.print("<statements>\n");
        compileStatement();
        System.out.println("</statements>\n");
        pw.print("</statements>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='}') {
            System.out.println("<symbol>}</symbol>\n");
            pw.print("<symbol>}</symbol>\n");
        }

        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("KEYWORD") && tokenizer.keyWord().equalsIgnoreCase("else")) {
            {
                System.out.println("<keyword>else</keyword>\n");
                pw.print("<keyword>else</keyword>\n");

                tokenizer.advance();
                if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '{') {
                    System.out.println("<symbol>{</symbol>\n");
                    pw.print("<symbol>{</symbol>\n");
                }

                System.out.println("<statements>\n");
                pw.print("<statements>\n");
                compileStatement();
                System.out.println("</statements>\n");
                pw.print("</statements>\n");

                tokenizer.advance();
                if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '}') {
                    System.out.println("<symbol>}</symbol>\n");
                    pw.print("<symbol>}</symbol>\n");
                }
            }
        }
        else
            tokenizer.Back();
        System.out.println("</ifStatement>\n");
        pw.print("</ifStatement>\n");

    }

    public void compileWhile()
    {
        System.out.println("<whileStatement>\n");
        System.out.println("<keyword>while</keyword>\n");
        pw.print("<whileStatement>\n");
        pw.print("<keyword>while</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='(') {
            System.out.println("<symbol>(</symbol>\n");
            pw.print("<symbol>(</symbol>\n");
        }
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()==')') {
            System.out.println("<symbol>)</symbol>\n");
            pw.print("<symbol>)</symbol>\n");
        }
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='{') {
            System.out.println("<symbol>{</symbol>\n");
            pw.print("<symbol>{</symbol>\n");
        }

        System.out.println("<statements>\n");
        pw.print("<statements>\n");
        compileStatement();
        System.out.println("</statements>\n");
        pw.print("</statements>\n");

        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()=='}') {
            System.out.println("<symbol>}</symbol>\n");
            pw.print("<symbol>}</symbol>\n");
        }

        System.out.println("</whileStatement>\n");
        pw.print("</whileStatement>\n");
    }

    public void compileDo()
    {
        System.out.println("<doStatement>\n");
        System.out.println("<keyword>do</keyword>\n");
        pw.print("<doStatement>\n");
        pw.print("<keyword>do</keyword>\n");
        compileSubroutineCall();
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()==';') {
            System.out.println("<symbol>;</symbol>\n");
            pw.print("<symbol>;</symbol>\n");
        }
        System.out.println("</doStatement>\n");
        pw.print("</doStatement>\n");
    }


    public void compileReturn()
    {
        System.out.println("<returnStatement>\n");
        System.out.println("<keyword>return</keyword>\n");
        pw.print("<returnStatement>\n");
        pw.print("<keyword>return</keyword>\n");

        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == ';')
        {
            System.out.println("<symbol>;</symbol>\n");
            System.out.println("</returnStatement>\n");
            pw.print("<symbol>;</symbol>\n");
            pw.print("</returnStatement>\n");
            return;
        }
        tokenizer.Back();
        compileExpression();
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol()==';') {
            System.out.println("<symbol>;</symbol>\n");
            pw.print("<symbol>;</symbol>\n");
        }
        System.out.println("</returnStatement>\n");
        pw.print("</returnStatement>\n");
    }

    public void compileExpression()
    {
        System.out.println("<expression>\n");
        pw.print("<expression>\n");

        compileTerm();

        do {
            tokenizer.advance();

            if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.isOp()){
                if (tokenizer.symbol() == '>') {
                    System.out.println("<symbol>&gt;</symbol>\n");
                    pw.print("<symbol>&gt;</symbol>\n");
                }
                else if (tokenizer.symbol() == '<') {
                    System.out.println("<symbol>&lt;</symbol>\n");
                    pw.print("<symbol>&lt;</symbol>\n");
                }
                else if (tokenizer.symbol() == '&') {
                    System.out.println("<symbol>&amp;</symbol>\n");
                    pw.print("<symbol>&amp;</symbol>\n");
                }
                else {
                    System.out.println("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                    pw.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                }
                compileTerm();
            }
            else
            {
                tokenizer.Back();
                break;
            }

        }while (true);

        System.out.println("</expression>\n");
        pw.print("</expression>\n");
    }

    public void compileTerm()
    {
        System.out.println("<term>\n");
        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("IDENTIFIER"))
        {
            String tempId = tokenizer.identifier();

            tokenizer.advance();
            if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '[')
            {
                System.out.println("<identifier>" + tempId + "</identifier>\n");
                pw.print("<identifier>" + tempId + "</identifier>\n");
                System.out.println("<symbol>[</symbol>\n");
                pw.print("<symbol>[</symbol>\n");
                compileExpression();
                requireSymbol(']');
            }
            else if(tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.'))
            {
                tokenizer.Back();tokenizer.Back();
                compileSubroutineCall();
            }else
            {
                System.out.println("<identifier>" + tempId + "</identifier>\n");
                pw.print("<identifier>" + tempId + "</identifier>\n");
                tokenizer.Back();
            }
        }
        else
        {
            if (tokenizer.tokenType().equalsIgnoreCase("INT_CONST")) {
                System.out.println("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");
                pw.print("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");
            }
            else if (tokenizer.tokenType().equalsIgnoreCase("STR_CONST")) {
                System.out.println("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");
                pw.print("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");
            }
            else if(tokenizer.tokenType().equalsIgnoreCase("KEYWORD") &&
                    (tokenizer.keyWord().equalsIgnoreCase("true")||
                            tokenizer.keyWord().equalsIgnoreCase("false")||
                            tokenizer.keyWord().equalsIgnoreCase("null")||
                            tokenizer.keyWord().equalsIgnoreCase("this"))) {
                System.out.println("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
                pw.print("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
            }
            else if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == '(')
            {
                System.out.println("<symbol>(</symbol>\n");
                pw.print("<symbol>(</symbol>\n");
                compileExpression();
                requireSymbol(')');
            }
            else if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')){
                System.out.println("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                pw.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                compileTerm();
            }
            else
                error("term");
        }
        System.out.println("</term>\n");
        pw.print("</term>\n");
    }


    public void compileExpressionList()
    {
        tokenizer.advance();

        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == ')')
            tokenizer.Back();
        else
        {
            tokenizer.Back();
            compileExpression();
            do{
                tokenizer.advance();
                if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == ',')
                {
                    System.out.println("<symbol>,</symbol>\n");
                    pw.print("<symbol>,</symbol>\n");
                    compileExpression();
                }
                else
                {
                    tokenizer.Back();
                    break;
                }
            }while (true);
        }
    }


    public void error(String n)
    {
        pw.close();
        throw new IllegalStateException("An error has occurred: "+n);
    }
    public void requireSymbol(char symbol) {
        tokenizer.advance();
        if (tokenizer.tokenType().equalsIgnoreCase("SYMBOL") && tokenizer.symbol() == symbol) {
            System.out.println("<symbol>" + symbol + "</symbol>\n");
            pw.print("<symbol>" + symbol + "</symbol>\n");
        }
        else
            error("'" + symbol + "'" + "=> "+tokenizer.symbol()+tokenizer.tokenType());
    }

}
