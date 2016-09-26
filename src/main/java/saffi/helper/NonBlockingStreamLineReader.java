package saffi.helper;

import com.google.gson.Gson;

import java.io.*;

public class NonBlockingStreamLineReader implements StreamLineReader {
	private Gson g=new Gson();

	private final InputStream stream;
	private BufferedReader br;
	private StringBuilder sb = new StringBuilder();

	public NonBlockingStreamLineReader(InputStream stream) {
		this.stream = stream;

		try {
			this.br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getLine() throws IOException {

		String found = null;
		// todo use char []
		while (br.ready()) {
			final char c = (char) br.read();
			sb.append(c);
			if (c=='\n'){
				found = sb.toString();
				sb = new StringBuilder();
				return found;
			}
		}
		return null;
	}

}
