package topse.pattern;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InputStreamDetector {

	private static final InputStreamDetector DEFAULT_DETECTOR;
	
	static {
		InputStreamDetector detecter = new InputStreamDetector();
		detecter.addHandler(new URLPathHandler());
		detecter.addHandler(new FilePathHandler());
		DEFAULT_DETECTOR = detecter;
	}
	
	public static InputStreamDetector getDefault() {
		return DEFAULT_DETECTOR;
	}
	
	private List<PathHandler> handlers = new ArrayList<>();
	
	public void addHandler(PathHandler handler) {
		handlers.add(handler);
	}
	
	public void removeHandler(PathHandler handler) {
		handlers.add(handler);
	}
	
	public InputStream detect(String path) {
		for(PathHandler handler: handlers) {
			InputStream is = handler.getInputStream(path);
			if(is != null) {
				return is;
			}
		}
		throw new IllegalArgumentException("unknown path: " + path);
	}
}
