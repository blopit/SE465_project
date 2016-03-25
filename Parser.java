import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jude on 3/24/2016.
 */
public class Parser {

    public void parse(File file) {
        HashMap<Integer, HashSet<Integer>> FOccurrences = new HashMap<Integer,HashSet<Integer>>();
        try {
            BufferedReader lines = new BufferedReader(new FileReader(file));
            String line;
            String sKey = null;
            HashSet<Integer> tempHash = new HashSet<Integer>();
            //temporary hashset for displaying strings for sets, delete it after testing
            HashSet<String> tempHashString = new HashSet<String>();

            while ((line = lines.readLine()) != null) {
                if (line.contains("null function")) {
                    //ignore the first calls function
                    sKey = null;
                }
                if (line.contains("Call graph node ")) {
                    if (sKey != null) {
                        //add to hashmap
                        FOccurrences.put(sKey.hashCode(), tempHash);
                        System.out.println(sKey + " : " + tempHashString);
                    }
                    Pattern pattern = Pattern.compile("'(.*?)'");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        sKey = (matcher.group(0));
                    }
                    //reset the temp hashset;
                    tempHash = new HashSet<Integer>();
                    //reset the testing object
                    tempHashString = new HashSet<String>();
                } else if (line.contains("calls function")) {
                    // get the function value
                    String[] temp = line.split(" ");
                    //get the last token
                    String functionName = temp[temp.length - 1];
                    //remove the quotes
                    functionName = functionName.replace("'", "");
                    tempHash.add(functionName.hashCode());
                    //add to to the testing object
                    tempHashString.add(functionName);
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
