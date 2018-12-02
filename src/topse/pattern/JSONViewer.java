package topse.pattern;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/*
Required minimal-json,
https://github.com/ralfstx/minimal-json

1. download repository
2. cd minimal-json-master/com.eclipsesource.json
3. mvn package
4. cd target
 */

public class JSONViewer extends Viewer {
	public static void main(String[] args) {
		int i;
		for (i = 0; i < args.length; i++) {
			System.out.println(args[i]);  //引数は全てURLと解釈してそれぞれ処理をする
			try {
				Viewer viewer = new JSONViewer();
				viewer.process(args[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int indent = 0;  //表示する時のインデントレベル
	private boolean nameOut = false; //名前出力後に余分なスペースを出さないようにするためのフラグ変数

	private String jsonString;
	private JsonObject jsonObject;

	public JSONViewer() {
		setInputCharset(Charset.forName("UTF-8"));
	}
	
	//JSONの内容をダンプする。再帰呼び出しにより、データの形式に寄らずに出力可能
	private void showJSON(JsonValue obj) {
		if (!nameOut) {
			indentSpace(); //必要ならインデント出力
		}
		if (obj.isObject()) {   //引数がオブジェクトの場合
			System.out.println("{");
			nameOut = false;
			for (JsonObject.Member o : obj.asObject()) {    //オブジェクトの各要素について
				indent++;
				String name = o.getName();  //名前を取得
				JsonValue value = o.getValue(); //値を取得
				indentSpace();
				System.out.print(name + ": ");
				nameOut = true;
				showJSON(value);    //値に関して再帰呼び出しする
				nameOut = false;
				indent--;
			}
			indentSpace();
			System.out.println("}");
		} else if (obj.isArray()) {   //配列のの各要素について
			System.out.println("[");
			nameOut = false;
			for (JsonValue value : obj.asArray()) {  //配列の各要素について
				indent++;
				showJSON(value); //要素に関して再帰呼び出しする
				indent--;
			}
			indentSpace();
			System.out.println("]");
		} else if (obj.isNull()) {
			System.out.println("NULL");
		} else if (obj.isBoolean()) {
			System.out.println(obj.asBoolean());
		} else if (obj.isNumber()) {
			System.out.println(obj.asFloat());  //整数か小数かは判断が難しいため、とりあえず一律にfloatにする
		} else if (obj.isString()) {
			System.out.println(obj.asString());
		}
	}

	//JSON出力時のインデント出力
	private void indentSpace() {
		for (int i = 0; i < indent; i++) {
			System.out.print("   ");
		}
	}

	@Override
	protected void read(InputStream is) throws IOException {
		try(DataInputStream dis = new DataInputStream(is)) { //ダウロードのためストリームを用意
			byte[] buffer = new byte[4096]; //バッファを確保
			int bSize;                      //ダウンロード量を取得する変数
			StringBuffer jsonString = new StringBuffer();   //ダウンロード結果を追記する文字列を用意
			while (-1 != (bSize = dis.read(buffer))) {   //データが読み取れれば
				jsonString.append(new String(buffer, 0, bSize, getInputCharset()));   //文字列に追加
			}
			this.jsonString = jsonString.toString();
		}
	}

	@Override
	protected void parse() {
		this.jsonObject = Json.parse(jsonString.toString()).asObject();
	}

	@Override
	protected void show() {
		indent = 0;
		showJSON(this.jsonObject); //文字列をJSONのパーサにかけて、表示する
	}
}