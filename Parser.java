import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jude on 3/24/2016.
 */
public class Parser {

    public void parse(File file) {
        HashMap<Integer, HashSet<String>> FOccurrences = new HashMap<Integer,HashSet<String>>();
        ArrayList<String> functions = new ArrayList<String>();
        try {
            BufferedReader lines = new BufferedReader(new FileReader(file));
            String line;
            String sKey = null;
            HashSet<String> tempHash = new HashSet<String>();
            //temporary hashset for displaying strings for sets, delete it after testing
            HashSet<String> tempHashString = new HashSet<String>();

            while ((line = lines.readLine()) != null) {
                if (line.contains("null function")) {
                    //currently adding functions
                    sKey = "functions";
                }
                if (line.contains("Call graph node ")) {
                    //if its not null and not adding functions
                    if (sKey != null && !sKey.equals("functions")) {
                        //add to hashmap
                        FOccurrences.put(sKey.hashCode(), tempHash);
                        //System.out.println(sKey + " : " + tempHashString);
                    }
                    Pattern pattern = Pattern.compile("'(.*?)'");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        sKey = (matcher.group(0));
                        sKey = sKey.replace("'","");
                    }
                    //reset the temp hashset;
                    tempHash = new HashSet<String>();
                    //reset the testing object
                    tempHashString = new HashSet<String>();
                } else if (line.contains("calls function")) {
                    if (sKey.equals("functions")) {
                        String[] Tokens = line.split("\\s+");
                        String functionName = Tokens[Tokens.length - 1];
                        functionName = functionName.replace("'", "");
                        //System.out.println(functionName);
                    }
                    else
                    {
                        // get the function value
                        String[] temp = line.split(" ");
                        //get the last token
                        String functionName = temp[temp.length - 1];
                        //remove the quotes
                        functionName = functionName.replace("'", "");
                        tempHash.add(functionName);
                        //add to to the testing object
                        tempHashString.add(functionName);
                    }
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //Change the filename to any textfile location to test the parser
        String directory = System.getProperty("user.dir");
        String fileName = directory + "\\main.txt";
        File file = new File(fileName);
        Parser parser= new Parser();
        parser.parse(file);
    }

}
