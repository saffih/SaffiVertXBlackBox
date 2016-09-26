package saffi.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by saffi on 26/09/16.
 */
public class ArrayStreamLineReader implements StreamLineReader {
	private ArrayList<String> buffer = new ArrayList<String>();

	public boolean add(String line){
		return buffer.add(line);
	}

	public boolean addAll(Collection<String> lines){
		return buffer.addAll(lines);
	}

	public String getLine() throws IOException {
		if (buffer.isEmpty()) {
			return null;
		}
		return buffer.remove(0);
	}
}
