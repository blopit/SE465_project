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
			ProcessBuilder procbuilder = new ProcessBuilder(command);
			procbuilder.redirectErrorStream(true);
			Process process = procbuilder.start();
			
			// read output from error stream
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader reader = new BufferedReader(isr);
			StringBuilder builder = new StringBuilder();
			
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
				//System.out.println(line + " : " + builder.capacity());
			}
			process.waitFor();
			isr.close();
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
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Status.OK;
	}
}
