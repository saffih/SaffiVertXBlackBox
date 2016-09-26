package saffi.helper;

import java.io.*;

public class StreamHelper implements IStreamHelper {

	private final InputStream stream;
	private BufferedReader br;
	private StringBuilder sb = new StringBuilder();

	public StreamHelper(InputStream stream) {
		this.stream = stream;

		try {
			this.br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getString() throws IOException {
		String found = null;
		// todo use char []
		while (br.ready()) {
			sb.append((char) br.read());
		}
		final int iEnd = sb.indexOf("}");
		if (iEnd == -1) {
			return null;
		}
		final int iStart = sb.lastIndexOf("{", iEnd);
		if (iStart != -1) {
			found = sb.substring(iStart, iEnd + 1);
		}
		sb.delete(0, iEnd + 1);
		return found;
	}
}
