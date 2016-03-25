import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CallGraph {
	public enum Status {
		OK, ERR_CMD, ERR_OUT
	}

	/*
	 * Generate a call graph from the given file with command
	 */
	public static Status generate(String cmnd, String file) {
		String[] command = new String[] { cmnd, "-print-callgraph", file };
		try {
			// build command
			Process procbuilder = new ProcessBuilder(command).start();
			// read output from error stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					procbuilder.getErrorStream()));
			StringBuilder builder = new StringBuilder();

			// iterate and append though all line of output
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();

			// print to file
			try (PrintWriter out = new PrintWriter("main.txt")) {
				out.println(result);
			} catch (IOException e) {
				//e.printStackTrace();
				return Status.ERR_CMD;
			}

		} catch (IOException e) {
			//e.printStackTrace();
			return Status.ERR_OUT;
		}

		return Status.OK;
	}
}
