import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ChrisKatigbak on 2016-03-24.
 */
public class main {
    static int T_SUPPORT;
    static double T_CONFIDENCE;
    static int EXPAND;

    //Scopes where f1 is called but f2 is not
    static HashSet<String> f1MissingF2;

    //Scopes where f2 is called but f1 is not
    static HashSet<String> f2MissingF1;

    static HashMap<Integer, HashSet<String>> fOccurrences = new HashMap<Integer, HashSet<String>>();
    static ArrayList<String> functions = new ArrayList<String>();

    public static int support(Integer f1, Integer f2) {
        int sup = 0;
        HashSet<String> f1Occurrences = fOccurrences.get(f1);
        HashSet<String> f2Occurrences = fOccurrences.get(f2);

        //If support for either function is less than threshold, pair support will not meet the threshold
        if(f1Occurrences.size() < T_SUPPORT || f2Occurrences.size() < T_SUPPORT) {
            return -1;
        }

        if(f1Occurrences.size() < f2Occurrences.size()) {
            Iterator<String> it = f1Occurrences.iterator();
            f1MissingF2 = new HashSet<String>();
            f2MissingF1 = (HashSet<String>) f2Occurrences.clone();

            while(it.hasNext()) {
                String scope = it.next();
                if (f2Occurrences.contains(scope)) {
                    f2MissingF1.remove(scope);
                    sup++;
                } else {
                    f1MissingF2.add(scope);
                }
            }
        } else {
            Iterator<String> it = f2Occurrences.iterator();
            f2MissingF1 = new HashSet<String>();
            f1MissingF2 = (HashSet<String>) f1Occurrences.clone();

            while(it.hasNext()) {
                String scope = it.next();
                if(f1Occurrences.contains(scope)) {
                    f1MissingF2.remove(scope);
                    sup++;
                } else {
                    f2MissingF1.add(scope);
                }
            }
        }

        return sup;
    }

    public static void confidence(String function1, String function2) {
        Integer f1 = function1.hashCode();
        Integer f2 = function2.hashCode();

        int pairSupport = support(f1, f2);
        int f1Support = fOccurrences.get(f1).size();
        int f2Support = fOccurrences.get(f2).size();

        double f1Confidence = (double) pairSupport / (double) f1Support;
        double f2Confidence = (double) pairSupport / (double) f2Support;

        if(f1Confidence >= T_CONFIDENCE && pairSupport >= T_SUPPORT) {
            Iterator<String> it = f1MissingF2.iterator();

            while(it.hasNext()) {
                System.out.println("bug: " + function1 + " in " + it.next() +
                        ", pair: (" + function1 + ", " + function2 +
                        "), support: " + pairSupport + ", confidence: " + String.format( "%.2f", f1Confidence*100 ) + "%");
            }
        }

        if(f2Confidence >= T_CONFIDENCE && pairSupport >= T_SUPPORT) {
            Iterator<String> it = f2MissingF1.iterator();

            while(it.hasNext()) {
                System.out.println("bug: " + function2 + " in " + it.next() +
                        ", pair: (" + function1 + ", " + function2 +
                        "), support: " + pairSupport + ", confidence: " + String.format( "%.2f", f2Confidence*100 ) + "%");
            }
        }
    }

    public static void expand() {
        //create a clone of the fOccurrences map that will be read by this function
        HashMap<Integer, HashSet<String>> fOccurrencesClone = (HashMap<Integer, HashSet<String>>) fOccurrences.clone();

        for(int i = 0; i < functions.size(); i++) {
            String function = functions.get(i);
            int f = function.hashCode();

            //clone the hash set of the the scopes f occurs in, which will be read
            //the original may be modified
            HashSet<String> occurrences = (HashSet<String>) fOccurrences.get(f).clone();
            fOccurrencesClone.put(f, occurrences);

            //Iterate through all scopes in which f occurs
            Iterator<String> it = occurrences.iterator();
            while(it.hasNext()) {
                String fScope = it.next();

                //is fScope called in any other scopes?
                HashSet<String> expandedOcurrences = fOccurrencesClone.get(fScope.hashCode());
                if(expandedOcurrences != null) {

                    //add all scopes that call fScope to the hash set of scopes that call f
                    Iterator<String> jt = expandedOcurrences.iterator();
                    while(jt.hasNext()) {
                        String y = jt.next();
                        fOccurrences.get(f).add(y);
                    }
                }

            }

        }

    public static void main(String[] args) {

    	String filestr = "";
		if (args.length == 1) {
			filestr = args[0];
			T_SUPPORT = 3;
			T_CONFIDENCE = 0.65;
            ExPAND = 0;
		} else if (args.length == 3) {
			filestr = args[0];
			T_SUPPORT = Integer.parseInt(args[1]);
			T_CONFIDENCE = Double.parseDouble(args[2])/100.0;
            EXPAND = 0;
		} else if (args.length == 4) {
            filestr = args[0];
            T_SUPPORT = Integer.parseInt(args[1]);
            T_CONFIDENCE = Double.parseDouble(args[2])/100.0;
            EXPAND = Integer.parseInt(args[3]);

        } else {
			return;
		}

		if (CallGraph.generate("opt", filestr) != CallGraph.Status.OK) {
			CallGraph.generate("/usr/local/Cellar/llvm/3.6.2/bin/opt", filestr);
		}
		;

		// Change the filename to any textfile location to test the parser
		String directory = System.getProperty("user.dir");
		String fileName = directory + "/main.txt";
		File file = new File(fileName);
		Parser parser = new Parser();
		parser.parse(file);
		fOccurrences = parser.getFOccurrences();
		functions = parser.getFunctions();

        for(int h = 0; h < EXPAND; h++) {
            expand();
        }

        for(int i = 0; i < functions.size(); i++) {
            String function1 = functions.get(i);
            int f1 = function1.hashCode();

            for(int j = i + 1; j < functions.size(); j++) {
                String function2 = functions.get(j);
                int f2 = function2.hashCode();

                confidence(function1, function2);
            }
        }
    }
}
