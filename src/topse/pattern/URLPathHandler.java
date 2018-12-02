package topse.pattern;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLPathHandler implements PathHandler {

	@Override
	public InputStream getInputStream(String path) {
		try {
			//URLクラスのオブジェクトを生成
			URL url = new URL(path);

			HttpURLConnection con = (HttpURLConnection)url.openConnection();    //HTTP接続
			con.setAllowUserInteraction(false);     //処理に専念
			con.setInstanceFollowRedirects(true);   //リダイレクトは受け入れて処理する
			con.setRequestMethod("GET");            //GETメソッドを使う
			con.connect();                          //接続

			int statusCode = con.getResponseCode(); //ステータスコードを取得
			if (statusCode != HttpURLConnection.HTTP_OK) {  //200でなければ、例外を出し、処理を終える
				return null;
			}
			return con.getInputStream(); //ダウロードのためストリームを用意
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} 

	}

}
