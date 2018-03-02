package com.fox.Dex;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class Main {

    public static String specialString = "*";

    public static void main(String[] args) {

        if (args.length < 1){
            System.out.println("USAGE: dex [zipname | txtname | .]");
            System.out.println("Examples:");
            System.out.println("thisapp tegra.zip -- To encrypt");
            System.out.println("or");
            System.out.println("thisapp tegra.txt -- To decrypt");
            System.out.println("");
            System.out.println("To encrypt data, just add \"*\" as second argument");
            System.out.println("");
            System.out.println("To decrypt from clipboard, type: dex .");
            return;
        }

        boolean encrypt = false;
        if (args.length > 1){
            if (args[1].equals("*")) encrypt = true;
        }



        String filename = args[0];
        // If it clipboard
        if (filename.equals(".")){
            String newFileName = "clipboard.txt";
            doSaveFileFromClipBoardAs(newFileName);
            filename = newFileName;
        }

        boolean textFile = isItTextFile(filename);
        boolean zipFile = isZipFile(filename);

        File file = new File(filename);

        if (zipFile){
            doSaveAsText(file, encrypt);
        } else if (textFile){
            doDecodeFromTextFile(file);
        }



    }

    private static boolean isItTextFile(String filename){
        return filename.endsWith(".txt");
    }

    private static boolean isZipFile(String filename){
        return filename.endsWith(".zip");
    }

    private static byte[] decode(String base64){
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(base64);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{0x0};
        }
    }

    private static String encode(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    private static String replaceLastFourChars(String str, String chars){
        String string = str.substring(0, str.length()-4);
        string += chars;
        return string;
    }

    private static void doSaveAsText(File file, boolean encrypt){
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = encode(bytes);

            String outputFileName = file.getCanonicalPath();
            outputFileName = replaceLastFourChars(outputFileName, ".txt");
            File newFile = new File(outputFileName);

            if (encrypt){
                System.out.println("I am encrypting!..");
                base64 = baseCyph(base64);
                base64 = specialString + base64;
            }

            Files.write(newFile.toPath(), base64.getBytes());

            System.out.println("File saved as "+newFile.getName()+"!");
            System.out.println("You can open it and copy context to clipboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doDecodeFromTextFile(File file){
        try {
            String outputFileName = file.getCanonicalPath();
            outputFileName = replaceLastFourChars(outputFileName, ".zip");

            File outPutFile = new File(outputFileName);

            String base64 = new String( Files.readAllBytes(file.toPath()) );

            // IS it encrypted?
            if (base64.startsWith(specialString)){
                System.out.println("I am decrypting!..");
                base64 = base64.substring(specialString.length());
                base64 = baseCyph(base64);
            }

            byte[] bytes = decode(base64);
            Files.write(outPutFile.toPath(), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void doSaveFileFromClipBoardAs(String name){
        File file = new File(name);
        try {
            if (file.exists()) file.delete();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            byte[] bytes = ((String) clipboard.getData(DataFlavor.stringFlavor)).getBytes();
            Files.write(file.toPath(), bytes, StandardOpenOption.CREATE, StandardOpenOption.SPARSE);
            System.out.println("File created with name "+name+" from clipboard!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String swapSubstrings(String str, String from, String to){
        String line = str;
        String randomString = "$@#$";
        String oldFrom = from;
        String oldTo = to;
        line = line.replace(from, randomString);
        line = line.replace(to, from);
        line = line.replace(randomString, to);
        return line;
    }

    private static String baseCyph(String base64){
        String
        string = swapSubstrings(base64, "1", "9");
        string = swapSubstrings(string, "2", "8");
        string = swapSubstrings(string, "3", "7");
        string = swapSubstrings(string, "4", "6");
        string = swapSubstrings(string, "5", "0");
        string = swapSubstrings(string, "S", "G");
        string = swapSubstrings(string, "V", "a");
        string = swapSubstrings(string, "g", "A");
        string = swapSubstrings(string, "y", "e");
        string = swapSubstrings(string, "E", "c");
        string = swapSubstrings(string, "Q", "o");
        string = swapSubstrings(string, "b", "p");
        string = swapSubstrings(string, "P", "B");
        string = swapSubstrings(string, "n", "N");
        string = swapSubstrings(string, "H", "h");
        string = swapSubstrings(string, "s", "d");
        string = swapSubstrings(string, "I", "x");
        string = swapSubstrings(string, "j", "W");
        string = swapSubstrings(string, "w", "J");
        return string;
    }



}
