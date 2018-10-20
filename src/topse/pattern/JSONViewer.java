package topse.pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
Required minimal-json,
https://github.com/ralfstx/minimal-json

1. download repository
2. cd minimal-json-master/com.eclipsesource.json
3. mvn package
4. cd target
 */

public class JSONViewer {
    public static void main(String[] args) {
        int i;
        for (i = 0; i < args.length; i++) {
            System.out.println(args[i]);  //引数は全てURLと解釈してそれぞれ処理をする
            try {
                URL url = new URL(args[i]); //URLクラスのオブジェクトを生成

                HttpURLConnection con = (HttpURLConnection)url.openConnection();    //HTTP接続
                con.setAllowUserInteraction(false);     //処理に専念
                con.setInstanceFollowRedirects(true);   //リダイレクトは受け入れて処理する
                con.setRequestMethod("GET");            //GETメソッドを使う
                con.connect();                          //接続

                int statusCode = con.getResponseCode(); //ステータスコードを取得
                if (statusCode != HttpURLConnection.HTTP_OK) {  //200でなければ、例外を出し、処理を終える
                    throw new Exception();
                }
                DataInputStream is = new DataInputStream(con.getInputStream()); //ダウロードのためストリームを用意
                byte[] buffer = new byte[4096]; //バッファを確保
                int bSize;                      //ダウンロード量を取得する変数
                StringBuffer jsonString = new StringBuffer();   //ダウンロード結果を追記する文字列を用意
                while (-1 != (bSize = is.read(buffer))) {   //データが読み取れれば
                    jsonString.append(new String(buffer, 0, bSize, "UTF-8"));   //文字列に追加
                }
                is.close(); //ストリームを閉じる
                indent = 0;
                showJSON(Json.parse(jsonString.toString()).asObject()); //文字列をJSONのパーサにかけて、表示する
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int indent = 0;  //表示する時のインデントレベル
    private static boolean nameOut = false; //名前出力後に余分なスペースを出さないようにするためのフラグ変数

    //JSONの内容をダンプする。再帰呼び出しにより、データの形式に寄らずに出力可能
    private static void showJSON(JsonValue obj) {
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
    private static void indentSpace() {
        for (int i = 0; i < indent; i++) {
            System.out.print("   ");
        }
    }
}