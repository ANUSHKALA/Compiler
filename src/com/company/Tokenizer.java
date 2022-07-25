package com.company;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Tokenizer {

    public static int index=0;
    public int length;
    public String Tokens[]=new String[1000];
    public static Pattern tokenPatterns;
    private ArrayList<String> symbols;
    private static ArrayList<String> keywords;
    public Tokenizer(String filename){

        this.initializeKeywordsMap();
        this.initializeSymbolsSet();
        regIn();

        try{
            File inputFile = new File("src/Square.jack");
            Scanner sc = new Scanner(inputFile);
            while(sc.hasNext()){
                String line = sc.nextLine().strip();
                if(!line.equals("")){
                    if(line.contains("//") && line.charAt(0)!='/'){
                        line = line.substring(0,line.indexOf("//")).strip();
                    }
                    if(line.contains("//") && line.charAt(0)=='/'){
                        continue;
                    }
//                    assert false;
                    Matcher match = tokenPatterns.matcher(line);
                    while (match.find())
                        Tokens[index++]=match.group();
                }
            }
            System.out.println(Arrays.toString(Tokens));
            sc.close();
            length = index;
            index = 0;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void regIn(){
        String keyWordReg = "";

        for (String seg: keywords)
            keyWordReg += seg + "|";

        String regSymbol = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
        String regInt = "[0-9]+";
        String regStr = "\"[^\"\n]*\"";
        String regId = "[\\w_]+";

        tokenPatterns = Pattern.compile(keyWordReg + regSymbol + "|" + regInt + "|" + regStr + "|" + regId);

    }

    public boolean hasMoreTokens(){
        return index<this.length;
    }

    public void advance(){
        index++;
    }

    public void initializeSymbolsSet(){
        symbols=new ArrayList<String>();
        symbols.add(",");
        symbols.add("{");
        symbols.add("}");
        symbols.add(")");
        symbols.add("(");
        symbols.add("[");
        symbols.add("]");
        symbols.add(".");
        symbols.add(";");
        symbols.add("+");
        symbols.add("-");
        symbols.add("*");
        symbols.add("/");
        symbols.add("&");
        symbols.add("|");
        symbols.add("<");
        symbols.add(">");
        symbols.add("=");
        symbols.add("~");
    }

    public void initializeKeywordsMap()
    {
        keywords=new ArrayList<String>();
        keywords.add("class");
        keywords.add("constructor");
        keywords.add("function");
        keywords.add("method");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("let");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
    }

    public String tokenType(){
        String token = Tokens[index];
        System.out.println(token);
        try{
            Integer.parseInt(token);
            return "INT_CONST";
        }
        catch(NumberFormatException e){
            if(keywords.contains(token.toLowerCase()))
                return "KEYWORD";
            else if(symbols.contains(token.toLowerCase()))
                return "SYMBOL";
            else if(token.charAt(0)==token.charAt(token.length()-1) && '"'==token.charAt(0))
                return "STR_CONST";
            else if(! "0123456789".contains(token.charAt(0)+""))
                return "IDENTIFIER";
            return "";
        }
    }

    public String keyWord(){
        if(this.tokenType().equalsIgnoreCase("KEYWORD")){
//            System.out.println("<KEYWORD>"+Tokens[index]+"</KEYWORD>");

            return Tokens[index];
        }
        return "";
    }

    public char symbol(){
        if(this.tokenType().equalsIgnoreCase("SYMBOL")){

            return Tokens[index].charAt(0);
        }
        return '\b';
    }

    public String identifier(){
        if(this.tokenType().equalsIgnoreCase("IDENTIFIER"))
        {
            return Tokens[index];
        }
        return "";
    }

    public int intVal(){
        if(this.tokenType().equalsIgnoreCase("INT_CONST")){
            try{
                return Integer.parseInt(Tokens[index]);
            }
            catch(NumberFormatException e){
                return 0;
            }
        }
        return -1;
    }

    public String stringVal(){
        if(this.tokenType()=="STR_CONST")
        {
            return Tokens[index].substring(1,Tokens[index].length()-1);
        }
        return "";
    }

    public void Back(){
        if(index>=0)
            index--;
    }

    public boolean isOp(){
        if("+-*/&|<>=".contains(Tokens[index]))
            return true;
        return false;
    }

}
