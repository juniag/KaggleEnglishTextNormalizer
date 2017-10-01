package com.company;

import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Main {

    //class variables
    public static HashMap<String, String> verbatim = new HashMap<String, String>();
    public static HashMap<String, String> fractionSymbols = new HashMap<String, String>();
    public static HashMap<String, String> measureAbbr = new HashMap<String, String>();
    public static HashMap<String, String> money = new HashMap<String, String>();
    public static HashMap<String, String> time = new HashMap<String, String>();

    public static void main(String[] args) {
//        try{
//            createLookups("Verbatim.csv", "Verbatim");
//            createLookups("Fraction.csv", "Fraction");
//            createLookups("Measure.csv", "Measure");
//            createLookups("Money.csv", "Money");
//            createLookups("Time.csv", "Time");
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }

    }

    public static void readTrainCsv(String csvFile) throws Exception {
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        LinkedList<Pair<String, String>> l = new LinkedList<Pair<String, String>>();
        HashMap<String, Pair<String, String>> h = new HashMap<String, Pair<String, String>>();
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] lines = line.split(csvSplitBy);
                if(lines.length > 5){
                    String four = lines[lines.length-1];
                    int ind = lines.length - 1;
                    if(!(four.length() > 1 && four.startsWith("\"") && four.endsWith("\""))) {
                        ind--;
                        while (!lines[ind].startsWith("\"")) {
                            four = lines[ind] + "," + four;
                            ind--;
                        }
                        four = lines[ind] + "," + four;
                    }

                    String three = lines[3];
                    for(int tind = 4; tind < ind; tind++){
                        three = three + "," + lines[tind];
                    }

                    lines[3] = three;
                    lines[4] = four;
                }

                try{
                    readAndCreateHashmaps(lines[2].substring(1, lines[2].length()-1),
                            lines[3].substring(1, lines[3].length()-1),
                            lines[4].substring(1, lines[4].length()-1));
                }
                catch(Exception ex){
                    System.out.println(ex);
                    System.out.println(lines[2]);
                    System.out.println(lines[3]);
                    System.out.println(lines[4]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        writeHashMapToCsv("Verbatim.csv", verbatim);
        writeHashMapToCsv("Fraction.csv", fractionSymbols);
        writeHashMapToCsv("Measure.csv", measureAbbr);
        writeHashMapToCsv("Money.csv", money);
        writeHashMapToCsv("Time.csv", time);
    }

    public static void writeHashMapToCsv(String file, HashMap<String, String> map) throws Exception {
        String eol = System.getProperty("line.separator");
        try (Writer writer = new FileWriter(file)) {
            for (HashMap.Entry<String, String> entry : map.entrySet()) {
                writer.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(eol);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void readAndCreateHashmaps(String type, String input, String output){
        System.out.println(type);
        if(type.equals("VERBATIM")){
            verbatim.put(input.trim(), output.trim());
            return;
        }

        if(type.equals("FRACTION")){
            String[] splitOutput = output.split("and");
            fractionSymbols.put(input.substring(input.length() - 1).trim(),
                    splitOutput[splitOutput.length-1].trim());
            return;
        }

        if(type.equals("MEASURE")){
            char[] ca = input.toCharArray();
            boolean firstIndAbbr = false;
            int i = 0;
            while(!firstIndAbbr && i < ca.length){
                char c = ca[i];
                String sc = c + "";
                if(Character.isDigit(c) || Character.isWhitespace(c)
                        || sc.equals(".") || sc.equals(",")){
                    i++;
                }
                else{
                    firstIndAbbr = true;
                }
            }

            String sym = "";
            while(i < ca.length){
                sym += ca[i];
                i++;
            }

            measureAbbr.put(sym.trim(), output.trim());
            return;
        }

        if(type.equals("MONEY")){
            String[] splitSym = input.split("[0-9]|[,]|[.]");
            for(String s: splitSym) {
                if(!s.equals("")){
                    money.put(s.trim(), output.trim());
                    return;
                }
            }
        }

        if(type.equals("TIME")){
            String[] splitSym = input.split("[0-9]|[:]");
            for(String s: splitSym) {
                if(!s.equals("")){
                    time.put(s.trim(), output.trim());
                    return;
                }
            }
        }
    }

    public static void createLookups(String filename, String mapping){
        System.out.println(mapping);
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        LinkedList<Pair<String, String>> l = new LinkedList<Pair<String, String>>();
        HashMap<String, Pair<String, String>> h = new HashMap<String, Pair<String, String>>();
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] lines = line.split(csvSplitBy);
                if(mapping.equals("Verbatim")){
                    verbatim.put(lines[0], lines[1]);
                }
                else if(mapping.equals("Fraction")){
                    fractionSymbols.put(lines[0], lines[1]);
                }
                else if(mapping.equals("Measure")){
                    measureAbbr.put(lines[0], lines[1]);
                }
                else if(mapping.equals("Money")){
                    money.put(lines[0], lines[1]);
                }
                else{
                    time.put(lines[0], lines[1]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String identifyAndNormalize(String before){
        before = before.trim().substring(1,before.length()-1);
        if(before.matches("\\p{Punct}*")){
            return before;
        }

        if(verbatim.containsKey(before)){
            return verbatim.get(before);
        }

        if(before.endsWith("\'s")){
            return identifyAndNormalize(before.substring(0,before.length()-2)) + "'s";
        }

        if(before.matches("[a-z]*") || before.matches("[A-Z][a-z]*")){
            if(before.contains("a") || before.contains("e") || before.contains("i")
                    || before.contains("o") || before.contains("u") || before.contains("y")){
                return before;
            }
            else{
                return spreadAcronym(before);
            }

        }

        if(before.matches("[A-Z]*")){
            if(before.matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")){
                return readRomanNumeral(before);
            }
            else{
                return spreadAcronym(before);
            }
        }

        if(before.matches("[A-Z]*[s]]")){
            return spreadAcronym(before.substring(0, before.length()-2)) + "'s";
        }

        if(before.matches("[0-9]*")){
            if(before.length() == 4){
                return readYear(before);
            }
            else{
                return readNumber(before);
            }
        }

        if(before.matches("[0-9,]*[0-9]")){
            return readNumber(before.replaceAll(",", ""));
        }

        if(before.matches("[0-9]*.[0-9]*")){
            return readDecimal(before);
        }

        // Telephone numbers are a combination of numbers and whitespace or dashes (called sils) or have the
        // first set of numbers in parentheses followed by more numbers in the previously stated pattern.
        // The groups of numbers are parsed to always have the word sil between them
        // There are also some cases where the last group of characters are letters. The way these are dealt with
        // switch between splitting a letter like an acronym or keeping them together like a plain word (more common)
        if(before.matches("[(]?\\s*[0-9|A-Z]*\\s*[)]?\\s*([0-9|A-Z]*\\s*[-]*\\s*)*[0-9|A-Z]*")){
            return readTelephone(before);
        }

        // Measure seems really hard
        // Basically, its numbers followed by abbreviations or symbols that we need to understand
        // We should probably train these too

        if(before.contains(".com") || before.contains(".org") || before.contains(".net") || before.contains(".edu")
                || before.contains(".gov") || before.contains("www.") || before.contains("http")
                || before.contains(".co."))
        {
            return readWebAddress(before);
        }

        if(before.startsWith("#")){
            return "hash tag" + identifyAndNormalize(before.substring(1));
        }

        // Address is an awkward identifier and awkward fix.
        // Going to have to look into it more

        // Cardinal and Digit covers the same list of things. Need to look into differentiators

        // Date is a year but we also have a full dates in many forms.
        // We need to figure out how to identify - I'm thinking regex group

        // Money can be trained and then divided between the symbols and numbers

        // Time either has ":" or has "am", "pm", or GMT/UTC/etc
        // Should we train?

        // Ordinal falls under roman numerals or 1st/2nd/3rd/4th...

        // Fraction is pretty easy. It has a /
        // We need to watch out for the symbols that are fractions (like 1/2, 3/4, etc.)

        return null;
    }

    public static String readYear(String before) {
        int num = Integer.parseInt(before);
        if(num >= 2000 && num < 2010){
            return readNumber(before);
        }
        else{
            return readNumber(before.substring(0, 2)) + readNumber(before.substring(2));
        }
    }

    public static String readRomanNumeral(String before) {
        int decimal = 0;

        String romanNumeral = before.toUpperCase();
        for(int x = 0;x<romanNumeral.length();x++)
        {
            char convertToDecimal = before.charAt(x);

            switch (convertToDecimal)
            {
                case 'M':
                    decimal += 1000;
                    break;

                case 'D':
                    decimal += 500;
                    break;

                case 'C':
                    decimal += 100;
                    break;

                case 'L':
                    decimal += 50;
                    break;

                case 'X':
                    decimal += 10;
                    break;

                case 'V':
                    decimal += 5;
                    break;

                case 'I':
                    decimal += 1;
                    break;
            }
        }
        if (romanNumeral.contains("IV"))
        {
            decimal-=2;
        }
        if (romanNumeral.contains("IX"))
        {
            decimal-=2;
        }
        if (romanNumeral.contains("XL"))
        {
            decimal-=10;
        }
        if (romanNumeral.contains("XC"))
        {
            decimal-=10;
        }
        if (romanNumeral.contains("CD"))
        {
            decimal-=100;
        }
        if (romanNumeral.contains("CM"))
        {
            decimal-=100;
        }
        return readNumber(Integer.toString(decimal));
    }

    public static String spreadAcronym(String input){
        String output = "";
        input = input.toLowerCase();
        for(char c: input.toCharArray()){
            output += c + " ";
        }
        return output.trim();
    }

    public static String readNumber(String input){
        if (input == "0"){
            return "o";
        }
        //1-19 pronunciation
        String output = "";

        String[] unique = {"","one","two","three","four","five","six","seven","eight","nine","ten",
                "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen"};
        String[] tens = {"ten","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};
        //pronunciation of digits beyond tens
        String[] others = {"ones","tens","hundred","thousand","thousand","thousand","million",
                "million","million","billion","billion","billion","trillion","trillion","trillion"};

        int number = Integer.parseInt(input);
        if (number < 100)
        {
            if (number < 20){
                output = output + unique[number];
                return output;
            }
            else{
                int tenDigit = (int) Math.floor(number/10);
                output = output + tens[tenDigit-1] + " ";

                int oneDigit = number%10;
                output = output + unique[oneDigit];

                return output;
            }
        }
        else {
            //convert to number to string, delete first char, convert to number
            int realDigit = input.length();
            int speakingDigit = realDigit % 3;
            //System.out.println("the speakingDigit: " + speakingDigit);
            if(speakingDigit == 1){
                output = output + unique[Integer.parseInt(input.substring(0,1))];
                output = output + " " + others[realDigit-1] + " ";
                input = input.substring(1);
                //System.out.println(output);
            }
            //check if it should be pronounced like a 1-19 number
            else if(speakingDigit == 0){
                output = output + unique[Integer.parseInt(input.substring(0,1))] + " hundred ";
                input = input.substring(1);
                //System.out.println(output);
            }
            else if(Integer.parseInt(input.substring(0,speakingDigit)) < 20){
                output = output + unique[Integer.parseInt(input.substring(0,speakingDigit))];
                input = input.substring(1);
                //System.out.println(output);
            }
            else if(Integer.parseInt(input.substring(0,speakingDigit)) >= 20){
                output = output + tens[Integer.parseInt(input.substring(0,1))-1] + " " + unique[Integer.parseInt(input.substring(1,2))] + " " + others[realDigit-1] + " ";
                input = input.substring(2);
                //System.out.println(output);
            }
            //adding in the "billion", "million"... etc keywords
            //System.out.println("the new input: " + input);
            return output + readNumber(input);
        }
    }

    public static String readDecimal(String input){
        String[] split = input.split(".");
        String output = readNumber(split[0]);

        char[] decimals = split[1].toCharArray();
        for(char c: decimals){
            output += " " + readNumber(c + "");
        }

        return output;
    }

    public static String readTelephone(String input){
        char[] ca = input.toCharArray();
        String output = "";
        for(int i = 0; i < ca.length; i++){
            String unit = ca[i] + "";
            if(!unit.equals("(")){
                if(unit.equals(")")){
                    output += "sil" + " ";
                }
                else if (unit.equals("-")){
                    output += "sil" + " ";
                }
                else if(Character.isDigit(ca[i])){
                    output += readNumber(unit) + " ";
                }
                else if(Character.isLetter(ca[i])){
                    if(Character.isLetter(ca[i+1])){
                        output += unit;
                    }
                    else{
                        output += unit + " ";
                    }
                }
            }
        }

        return output.trim();
    }

    public static String readWebAddress(String input){
        char[] ca = input.toCharArray();
        String output = "";
        for(int i = 0; i < ca.length; i++) {
            String unit = ca[i] + "";
            if(unit.equals("/")){
                output += "slash" + " ";
            }
            else if(unit.equals(":")){
                output += "color" + " ";
            }
            else if(unit.equals(".")){
                output += "dot" + " ";
            }
            else if(Character.isDigit(ca[i])){
                output += readNumber(unit) + " ";
            }
            else{
                output += unit + " ";
            }

        }
        return output.trim();
    }
}
