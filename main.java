import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ChrisKatigbak on 2016-03-24.
 */
public class main {
    final static int T_SUPPORT = 3;
    final static double T_CONFIDENCE = 0.65;

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
                if(f1Occurrences.contains(it.next())) {
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

        if(f1Confidence >= T_CONFIDENCE) {
            Iterator<String> it = f1MissingF2.iterator();

            while(it.hasNext()) {
                System.out.println("bug: " + function1 + " in " + it.next() +
                        ", pair: (" + function1 + ", " + function2 +
                        "), support: " + pairSupport + ", confidence: " + f1Confidence*100 + "%");
            }
        }

        if(f2Confidence >= T_CONFIDENCE) {
            Iterator<String> it = f2MissingF1.iterator();

            while(it.hasNext()) {
                System.out.println("bug: " + function2 + " in " + it.next() +
                        ", pair: (" + function1 + ", " + function2 +
                        "), support: " + pairSupport + ", confidence: " + f2Confidence*100 + "%");
            }
        }
    }

    public static void main(String[] args) {
        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";
        String scope1 = "scope1";
        String scope2 = "scope2";
        String scope3 = "scope3";
        String scope4 = "scope4";
        String scope5 = "scope5";
        String scope6 = "scope6";

        functions.add(a);
        functions.add(b);
        functions.add(c);
        functions.add(d);

        fOccurrences.put(a.hashCode(), new HashSet<String>());
        fOccurrences.put(b.hashCode(), new HashSet<String>());
        fOccurrences.put(c.hashCode(), new HashSet<String>());
        fOccurrences.put(d.hashCode(), new HashSet<String>());

        fOccurrences.get(a.hashCode()).add(scope1);
        fOccurrences.get(a.hashCode()).add(scope2);
        fOccurrences.get(a.hashCode()).add(scope3);
        fOccurrences.get(a.hashCode()).add(scope5);

        fOccurrences.get(b.hashCode()).add(scope1);
        fOccurrences.get(b.hashCode()).add(scope3);
        fOccurrences.get(b.hashCode()).add(scope4);
        fOccurrences.get(b.hashCode()).add(scope5);
        fOccurrences.get(b.hashCode()).add(scope6);

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
