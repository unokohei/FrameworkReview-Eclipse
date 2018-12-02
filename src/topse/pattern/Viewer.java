package topse.pattern;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public abstract class Viewer {

	private Charset inputCharset = Charset.defaultCharset();

	public Charset getInputCharset() {
		return inputCharset;
	}

	public void setInputCharset(Charset inputCharset) {
		this.inputCharset = inputCharset;
	}

	public void process(String path) throws IOException {
		try(InputStream is = getInputStream(path)) {
			read(is);
		}
		parse();
		show();
	}

	protected InputStream getInputStream(String path) {
		return InputStreamDetector.getDefault().detect(path);
	}
	
	protected abstract void read(InputStream is) throws IOException;

	protected abstract void parse();

	protected abstract void show();

}
