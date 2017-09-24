package com.company;

import java.util.HashMap;

public class Main {

    //class variables
    public HashMap<String, String> verbatim = new HashMap<String, String>();
    public HashMap<String, String> fractionSymbols = new HashMap<String, String>();
    public HashMap<String, String> measureAbbr = new HashMap<String, String>();
    public HashMap<String, String> money = new HashMap<String, String>();
    public HashMap<String, String> time = new HashMap<String, String>();

    public static void main(String[] args) {
        System.out.println("sorry i fucked up the git stuff and had to make a new one teds");
    }

    public void readAndCreateHashmaps(String type, String input, String output){
        if(type.equals("VERBATIM")){
            verbatim.put(input, output);
            return;
        }

        if(type.equals("FRACTION")){
            String[] splitOutput = output.split("and");
            fractionSymbols.put(input.substring(input.length() - 1),
                    splitOutput[splitOutput.length-1]);
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

            //we need to be able to separate the after words to isolate the translation for the abbr

        }

        if(type.equals("MONEY")){

        }

        if(type.equals("TIME")){

        }
    }

    public String identifyAndNormalize(String before){
        before = before.trim();
        if(before.matches("\\p{Punct}*")){
            return before;
        }

        if(before.matches("[a-z]*") || before.matches("[A-Z][a-z]*")){
            return before;
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
            //remove commas
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

        // Measure seems really hard
        // Basically, its numbers followed by abbreviations or symbols that we need to understand
        // We should probably train these too

        // Verbatim is one that needs to be trained
        // We need to create a hashmap for the different symbols and characters they label as verbatim
        // This should happen last - anything we can't identify that is one character long should
        // just be returned and added to the hashmap?

        // Electronic is easy to deal with once we identify it's electronic
        // Identifying is harder - we should be looking for [.com; .org; .net; www.; http]

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

    private String readYear(String before) {
        int num = Integer.parseInt(before);
        if(num >= 2000 && num < 2010){
            return readNumber(before);
        }
        else{
            return readNumber(before.substring(0, 2)) + readNumber(before.substring(2));
        }
    }

    private String readRomanNumeral(String before) {
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

    public String spreadAcronym(String input){
        String output = "";
        input = input.toLowerCase();
        for(char c: input.toCharArray()){
            output += c + " ";
        }
        return output.trim();
    }

    public String readNumber(String input){
        int number = Integer.parseInt(input);

        return "";
    }

    public String readDecimal(String input){
        String[] split = input.split(".");
        String output = readNumber(split[0]);

        char[] decimals = split[1].toCharArray();
        for(char c: decimals){
            output += " " + readNumber(c + "");
        }

        return output;
    }
}
