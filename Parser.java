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
    HashMap<Integer,HashSet<String>> FOccurrences = new HashMap<Integer,HashSet<String>>();
    ArrayList<String> functions = new ArrayList<String>();

    public HashMap<Integer, HashSet<String>> getFOccurrences() {
        return FOccurrences;
    }
    public ArrayList<String> getFunctions() {
        return functions;
    }
    public void parse(File file) {
        FOccurrences = new HashMap<Integer,HashSet<String>>();
        functions = new ArrayList<String>();
        //temp HashSet
        HashSet<String> hashFunctions = new HashSet<String>();
        try {
            BufferedReader lines = new BufferedReader(new FileReader(file));
            String line;
            String sKey = null;

            while ((line = lines.readLine()) != null) {
                if (line.contains("null function")) {
                    //currently adding functions
                    sKey = "functions";
                }
                if (line.contains("Call graph node")) {
                    //skey is the currentscope
                    Pattern pattern = Pattern.compile("'(.*?)'");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        sKey = (matcher.group(0));
                        sKey = sKey.replace("'","");
                    }
                } else if (line.contains("calls function")) {
                    String functionName;
                    if (sKey.equals("functions")) {
                        String[] Tokens = line.split("\\s+");
                        functionName = Tokens[Tokens.length - 1];
                        functionName = functionName.replace("'", "");
                        //add to hashset to prevent duplicates
                        if (!hashFunctions.contains(functionName))
                        {
                            functions.add(functionName);
                            hashFunctions.add(functionName);
                        }
                    }
                    else
                    {
                        // get the function value
                        String[] temp = line.split(" ");
                        //get the last token
                        functionName = temp[temp.length - 1];
                        //remove the quotes
                        functionName = functionName.replace("'", "");
                    }
                    if (!sKey.equals("functions")&& sKey !=null)
                    {
                        if (FOccurrences.containsKey(functionName.hashCode()))
                        {
                            //add the current scope to the functionName
                            //FOccurrences.get(functionName.hashCode()).add(sKey);
                        }
                        else
                        {
                            //FOccurrences.put(functionName.hashCode(), new HashSet<String>());
                        }
                    }
                }
            }
            for (int i = 0 ; i < functions.size();i++)
            {
                System.out.println(functions.get(i));
            }
            for (Integer key: FOccurrences.keySet())
            {
                System.out.println(key + " : "+ FOccurrences.get(key));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    public static void main(String[] args) {
        //Change the filename to any textfile location to test the parser
        String directory = System.getProperty("user.dir");
        String fileName = directory + "\\main.txt";
        File file = new File(fileName);
        Parser parser= new Parser();
        parser.parse(file);
    }
    */
}
