package jeffrey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Check {
    public static final String pakage = "jeffrey"; // Name of the package
    public static final String name = "PowerBall"; // Name of the your main class
    
    public static final int numberOfTests = 10; // Number of tests to run
    
    public static final boolean debug = false; // Set to true to see the output of your program
    
    public static class Color{
        public static final String RESET = "\u001B[0m"; 
        public static final String YELLOW_TEXT = "\u001B[33m"; 
        public static final String BLACK_TEXT = "\u001B[30m"; 
        public static final String RED_TEXT = "\u001B[31m"; 
        public static final String GREEN_TEXT = "\u001B[32m"; 
        public static final String WHITE_TEXT = "\u001B[37m"; 
        public static void println(String text,String color) {
            System.out.println(text);
        }  
        public static void println(String text,String color, boolean enabled) {
            System.out.println(color+text+RESET);
        }
        
    }
    
    
    public static class RuntimeCheek {
        
        public static String run(String pakage, String name, String input, boolean compile) {
            try {
                if (compile) {
                    int result = compile(pakage + "/" + name + ".java");
                    if (result != 0) {
                        return "Error";
                    }
                }
    
                String finalResult = execute(pakage+"."+name, input);
                return finalResult;
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                return ex.toString();
            }
        }
        
        public static String execute(String clazz, String input) throws IOException, InterruptedException {        
            ProcessBuilder pb = new ProcessBuilder("java", clazz);
            pb.redirectError();
            pb.directory(new File("src"));
            Process p = pb.start();
            OutputStream dataInputStream = p.getOutputStream();
            dataInputStream.write(input.getBytes());
            dataInputStream.close();
            InputStreamConsumer consumer = new InputStreamConsumer(p.getInputStream());
            consumer.start();
            int code = p.waitFor();
            consumer.join();
            String output = consumer.getOutput().toString();
            if(code == 1) {
                return("Error");
            }else {
                return output;
            }
        }
    
        public static int compile(String file) throws IOException, InterruptedException {        
            ProcessBuilder pb = new ProcessBuilder("javac", file);
            pb.redirectError();
            pb.directory(new File("src"));
            Process p = pb.start();
            InputStreamConsumer consumer = new InputStreamConsumer(p.getInputStream());
            consumer.start();
    
            int result = p.waitFor();
    
            consumer.join();

            return result;
        }
    
        public static class InputStreamConsumer extends Thread {
    
            private InputStream is;
            private IOException exp;
            private StringBuilder output;
    
            public InputStreamConsumer(InputStream is) {
                this.is = is;
            }
    
            @Override
            public void run() {
                int in = -1;
                output = new StringBuilder(64);
                try {
                    while ((in = is.read()) != -1) {
                        output.append((char) in);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    exp = ex;
                }
            }
    
            public StringBuilder getOutput() {
                return output;
            }
    
            public IOException getException() {
                return exp;
            }
        }
    }
    
    public static class StaticCheek {
        public static boolean cheekLineSize(String filename) {
            List<String> badLines = new ArrayList<String>();
            boolean toReturn = true;
            try {
                File fileObj = new File(filename);
                Scanner fileReader = new Scanner(fileObj);
                int lineNum = 1;
                while (fileReader.hasNextLine()) {
                    String data = fileReader.nextLine();
                    if(data.length()>80 && ! data.startsWith("//")) {
                        badLines.add(lineNum+":"+data);
                        toReturn = false;
                    }
                    lineNum++;
                  }
                fileReader.close();
            } catch (FileNotFoundException e) {
                String toPrint = "Line Size cheek:\n\tError while cheeking line size";
                Color.println(toPrint, Color.RED_TEXT);
                toReturn = false;
            }
            if(toReturn) {
                String toPrint = "Line Size cheek:\n\tAll lines are less than 80 characters";
                Color.println(toPrint, Color.GREEN_TEXT);
            }else {
                String toPrint = "Line Size cheek:\n\tFollowing lines are more than 80 characters:";
                Color.println(toPrint, Color.RED_TEXT);
                for (String i : badLines) {
                    Color.println("\t\t\t" + i, Color.RED_TEXT);
                }
            }
            return(toReturn);
        }
        
        public static boolean cheekFileHeader(String filename) {
            try {
                Path fileObj = new File(filename).toPath();
                String content = Files.readString(fileObj);

                String regex = "(?:/*\r\n/.[0-9]+\r\n/*\r\n/* *ALL STUDENTS COMPLETE THESE SECTIONS\r\n/* *) Title: *(.*)\r\n/* Class: *(.*)\r\n/*\r\n/* Author: *(.*)[ -~\n\r\t]*";
                Pattern pat = Pattern.compile(regex);
                Matcher m = pat.matcher(content);
                if(m.matches()) {
                    String toPrint = "FileHeader cheek:\n\tName: "+m.group(3)+"\n\tTitle: "+m.group(1)+"\n\tClass: "+m.group(2);
                    Color.println(toPrint, Color.GREEN_TEXT);
                    return true;
                }else {
                    String toPrint ="FileHeader cheek:\n\tNo file header found";
                    Color.println(toPrint, Color.RED_TEXT);
                    return(false);
                }
                
            } catch (IOException e) {
                String toPrint ="FileHeader cheek:\n\tError while cheeking filename";
                Color.println(toPrint, Color.RED_TEXT);
                return(false);
            }
            
        }
    
        public static boolean cheekFortabs(String filename) {
            List<String> badLines = new ArrayList<String>();
            boolean toReturn = true;
            try {
                File fileObj = new File(filename);
                Scanner fileReader = new Scanner(fileObj);
                int lineNum = 1;
                while (fileReader.hasNextLine()) {
                    String data = fileReader.nextLine();
                    if(data.startsWith("\t")) {
                        badLines.add(lineNum+":"+data);
                        toReturn = false;
                    }
                    lineNum++;
                  }
                fileReader.close();
            } catch (FileNotFoundException e) {
                toReturn = false;
                String toPrint = "Tab cheek:\n\tError while cheeking tabs";
                Color.println(toPrint, Color.RED_TEXT);
            }
            if(toReturn) {
                String toPrint = "Tab cheek:\n\tNo tabs found";
                Color.println(toPrint, Color.GREEN_TEXT);
                }else {
                    String toPrint = "Tab cheek:\n\tFollowing lines contain tabs:";
                    Color.println(toPrint, Color.RED_TEXT);
                    for (String i : badLines) {
                        Color.println("\t\t\t" + i, Color.RED_TEXT);
                    }
                }
            
            return(toReturn);
        }

    }
    
    public static class Helper{
        public static List<String> listFilesForFolder(File folder) {
            List<String> toReturn = new ArrayList<String>();
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    toReturn.add(fileEntry.getName());
                }
            }
            return toReturn;
        }
        
        public static boolean staticCheek(String filename) {

            boolean fileCheek  = StaticCheek.cheekFileHeader(filename);
            boolean lineCheek = StaticCheek.cheekLineSize(filename);
            boolean tabsCheek = StaticCheek.cheekFortabs(filename);
            return fileCheek && lineCheek && tabsCheek;
        }
        public static String[] makeRuntimeInputGood(int amount) {
            String [] runtimeInput = new String[amount];
            for (int i = 0; i < runtimeInput.length; i++) {
                int[] nums = new int[5];
                for (int i1 = 0; i1 < 5; i1++) {
                    nums[i1] = (int) (Math.random() * 69) + 1;
                }
                int end = (int) (Math.random() * 39) + 1;

                String result = "";
                for (int i1 = 0; i1 < nums.length; i1++) {
                    result += nums[i1] + "\n";
                }
                result += "\n" + end;
                runtimeInput[i] = result;
            }
            return runtimeInput;
        }
        
        public static int generateNumber(int min, int max, boolean within) {
            if (within) {
                return (int) (Math.random() * (max - min)) + min;
            }
            else {
                int num = (int) ((Math.random() - 0.5) * 1000);
                while (num < min || num > max) {
                    num = (int) ((Math.random() - 0.5) * 1000);
                }
                return num;
            }
        }

        public static String[] makeRuntimeInputBad(int amount) {
            String [] runtimeInput = new String[amount];
            for(int i =0; i<runtimeInput.length;i++) {
                int[] nums = new int[5];
                for (int i1 = 0; i1 < 5; i1++) {
                    int num =(int) ((Math.random()-0.5) * 1000);
                    while (num > 0 && num < 70) {
                        num = (int) ((Math.random() - 0.5) * 1000);
                    }
                    nums[i1] = num;
                }
                
                int end = (int) ((Math.random()-0.5) * 1000) + 1;
                while (end > 0 && end < 40) {
                    end = (int) ((Math.random() - 0.5) * 1000);
                }
                
                String result = "";
                for (int i1 = 0; i1 < nums.length; i1++) {
                    result += nums[i1] + "\n";
                }
                result += "\n"+end;
                runtimeInput[i] = result;
            }
            return runtimeInput;
        }
        
    }
    
    public static void main(String[] args) throws IOException {
        List<String> files = Helper.listFilesForFolder(new File("./src/"+pakage));
        boolean toGo = true;
        for (String i : files) {
            if (i.endsWith(".java")&&!i.equals("Check.java")) {
                System.out.println(i);
                boolean cheek = Helper.staticCheek("./src/" + pakage + "/" + i);
                if (!cheek) {
                    toGo = false;
                }
            }
        }
        if(!toGo) {
                Color.println("Some static tests failed", Color.RED_TEXT);
                throw new RuntimeException();
            }else {
                Color.println("All static tests passed", Color.GREEN_TEXT);
            }
        String [] runtimeInputGood = Helper.makeRuntimeInputGood((int)numberOfTests/2);
        String[] runtimeInputBad= Helper.makeRuntimeInputBad(numberOfTests - (int)numberOfTests/2);
        String project3Regex = "Thewinningnumbercombinationis:"
                + "([0-9]{1,2})\t([0-9]{1,2})\t([0-9]{1,2})"
                + "\t([0-9]{1,2})\t([0-9]{1,2})\t"
                + "PowerBall:([0-9]{1,2})"
                + "Enteryour5numbers:Enteryourpowerball:"
                + "Yournumbercombinationis:"
                + "([0-9]{1,2})\t([0-9]{1,2})\t([0-9]{1,2})"
                + "\t([0-9]{1,2})\t([0-9]{1,2})\t"
                + "PowerBall:([0-9]{1,2})"
                + "((?:Sorry,youdidnotwin\\.)|(?:Congratulations!Youwon:\\$([0-9]*)))";

        Pattern pattern = Pattern.compile(project3Regex);
        int count = 0;
        boolean[] runtimeResults = new boolean[runtimeInputGood.length+runtimeInputBad.length];
        for (String i : runtimeInputGood) {
            String runTime = RuntimeCheek.run(pakage, name, i, count==0);
            if (debug) {
                System.out.println(runTime);
            }
            runTime = runTime.strip().replace(" ", "").replace("\n", "").replace("\r", "");
            Matcher matcher = pattern.matcher(runTime);
            if (matcher.matches()) {
                int totalWinnings = 0;
                int num1 = Integer.parseInt(matcher.group(1));
                int num2 = Integer.parseInt(matcher.group(2));
                int num3 = Integer.parseInt(matcher.group(3));
                int num4 = Integer.parseInt(matcher.group(4));
                int num5 = Integer.parseInt(matcher.group(5));
                int powerBall = Integer.parseInt(matcher.group(6));
                int playerNum1 = Integer.parseInt(matcher.group(7));
                int playerNum2 = Integer.parseInt(matcher.group(8));
                int playerNum3 = Integer.parseInt(matcher.group(9));
                int playerNum4 = Integer.parseInt(matcher.group(10));
                int playerNum5 = Integer.parseInt(matcher.group(11));
                int playerPowerBall = Integer.parseInt(matcher.group(12));
                String outcome = matcher.group(13);
                if(num1==playerNum1) {
                    totalWinnings+=50;
                }
                if (num2 == playerNum2) {
                    totalWinnings += 50;
                }
                if (num3 == playerNum3) {
                    totalWinnings += 50;
                }
                if (num4 == playerNum4) {
                    totalWinnings += 50;
                }
                if (num5 == playerNum5) {
                    totalWinnings += 50;
                }
                if (powerBall == playerPowerBall) {
                    totalWinnings += 500;
                }
                if (totalWinnings == 0) {
                    if(outcome.equals("Sorry,youdidnotwin.")) {
                        Color.println("Passed test "+count, Color.GREEN_TEXT);
                        runtimeResults[count] = true;
                    }else {
                        Color.println("Failed test "+count, Color.RED_TEXT);
                    }
                }else {
                    if (outcome.equals("Congratulations!Youwon:$"+ totalWinnings)) {
                        Color.println("Passed test "+count, Color.GREEN_TEXT);
                        runtimeResults[count] = true;
                    } else {
                        Color.println("Failed test "+count, Color.RED_TEXT);
                    }
                }
            } else {
                Color.println("Failed test "+count, Color.RED_TEXT);
            }
            count++;
        }
        for (String i : runtimeInputBad) {
            String runTime = RuntimeCheek.run(pakage, name, i, false);
            if (debug) {
                System.out.println(runTime);
            }
            runTime = runTime.strip().replace(" ", "").replace("\n", "").replace("\r", "");
            Matcher matcher = pattern.matcher(runTime);
            if (matcher.matches()) {
                Color.println("Failed test " + count, Color.RED_TEXT);
            } else {
                
                Color.println("Passed test " + count, Color.GREEN_TEXT);
                runtimeResults[count] = true;
            }
            count++;
        }
        int runtimeResulsPassed = 0;
        for (boolean i : runtimeResults) {
            if (i) {
                runtimeResulsPassed++;
            }
        }
        if(runtimeResulsPassed==runtimeResults.length) {
            Color.println("All runtime tests passed", Color.GREEN_TEXT);
            }else {
                Color.println("Some runtime tests failed", Color.RED_TEXT);
                throw new RuntimeException();
            }

        if(toGo) {
            Color.println("All tests passed", Color.GREEN_TEXT);
            }else {
                Color.println("Some tests failed", Color.RED_TEXT);
                throw new RuntimeException();
            }
    }
}