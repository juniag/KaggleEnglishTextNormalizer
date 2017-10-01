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
        String input = "400";
        System.out.println("printing 400: " + readNumber(input));
        input = "123456789";
        System.out.println("printing 123456789: " + readNumber(input));
        input = "999999999";
        System.out.println("printing 999999999: " + readNumber(input));
        input = "400400400";
        System.out.println("printing 400400400: " + readNumber(input));
        input = "111111111";
        System.out.println("printing 111111111: " + readNumber(input)); //oops
        input = "1000000000";
        System.out.println("printing 1000000000: " + readNumber(input));
        input = "1111111111";
        System.out.println("printing 1111111111: " + readNumber(input));
        //it still breaks sometimes, add more tests
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
