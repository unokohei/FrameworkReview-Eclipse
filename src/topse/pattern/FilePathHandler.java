package topse.pattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FilePathHandler implements PathHandler {

	@Override
	public InputStream getInputStream(String path) {
        File target = new File(path);    //Fileクラスのオブジェクトを確保
		try {
			return new FileInputStream(target);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
