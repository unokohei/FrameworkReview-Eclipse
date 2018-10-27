package topse.pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class CSVViewer {
    public static void main(String[] args) {
        int i, j, k;
        for (i = 0; i < args.length; i++) {
            System.out.println("Input File: " + args[i]);   //引数は全てファイルパスと解釈してそれぞれ処理をする

            //CSVファイルの読み込み
            ArrayList<String> buffer = new ArrayList<>();   //CSVの1行が1つの要素となる配列
            File target = new File(args[i]);    //Fileクラスのオブジェクトを確保
            try {
                BufferedReader br = new BufferedReader(new FileReader(target)); //ファイルを開いて読む
                String line;    //1行分を読み込む変数
                while ((line = br.readLine())!= null) { //1行ずつ読み込む
                    buffer.add(line);   //配列の要素として1行を追加
                }
                br.close(); //ファイルを閉じる
            } catch (Exception e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(-1);
            }

            //CSVの各列について、最大で何文字（Shift-JISでのバイト数）なのかを求める
            int maxCols = -1;   //列数の最大値を求める変数
            ArrayList<Integer> maxWidths = new ArrayList(); //列ごとの最大バイト数を記録する配列
            for (String s : buffer) {   //CSVの各行について
                StringTokenizer token = new StringTokenizer(s, ",");    //行を,で分割する（データとして,があるものは考慮していない）
                if (maxCols < token.countTokens()) {    //列数を最大値と比較して
                    maxCols = token.countTokens();      //それよりも多ければそれを最大値とする
                }
                int col = 0;    //列番号のカウンタ
                while (token.hasMoreTokens()) { //それぞれの列について
                    String item = token.nextToken();    //列の文字列データ
                    int byteLen = sjisLength(item);     //データのShift-JISバイト数
                    try {
                        int colW = maxWidths.get(col);  //その列の最大バイト数を得る
                        if (colW < byteLen) {           //最大バイト数より多い場合
                            maxWidths.set(col, byteLen);    //最大バイト数として設定する
                        }
                    } catch (IndexOutOfBoundsException ex) {    //getで例外が出る場合、
                        maxWidths.add(byteLen);     //その列の要素が配列にないので、追加で作る
                    }
                    col += 1;   //列カウンタを進める
                }
            }

            //各列の列数を表示するが、最大30バイトとして、それより長い列は強制的に30バイトにする
            StringJoiner message = new StringJoiner(",", "[", "]"); //各列のバイト数を文字列として繋げる
            int counter = 0;    //列カウンターとなる変数
            for (Integer n : maxWidths) {   //各列のバイト数がある配列のそれぞれの要素について
                if (n > 30) {   //30を超えていれば
                    maxWidths.set(counter, 30); //30にする
                }
                message.add(maxWidths.get(counter).toString()); //現在の列のバイト数を追加
                counter += 1;   //列カウンターを進める
            }
            System.out.println("Detected Columns: " + maxCols + message);   //コンソールに出力

            //テーブル形式に出力（最初の区切り線みたいな行）
            message = new StringJoiner("-+-", "+-", "-+");
            for (Integer elm : maxWidths) {  //各列のバイト数がある配列のそれぞれの要素について
                message.add(repeatString("-", elm)); //列数分、マイナス記号を繰り返す
            }
            System.out.println(message);  //コンソールに出力

            //テーブル形式に出力
            for (String s : buffer) { //CSVの各行について
                StringTokenizer token = new StringTokenizer(s, ",");  //行を,で分割する（データとして,があるものは考慮していない）
                ArrayList<ArrayList<String>> lineBuffer = new ArrayList();  //配列の配列を記録するオブジェクトを用意する
                    //1つのフィールドについて、複数行になることを考慮して、行ごとに、さらに複数行にあらかじめ分離しておく
                int col = 0, maxLines = -1; //それぞれ、列カウンタと、1行分が最大何行に別れるか
                while (token.hasMoreTokens()) { //それぞれの列について
                    lineBuffer.add(new ArrayList());    //列用の配列をまず生成する。最初は要素はない
                    String item = token.nextToken();    //列の文字列を取り出す
                    int colWidth = maxWidths.get(col);
                    int st = 0, lineCount = 0;  //それぞれ、分割文字の最初の位置と、分割行数カウンタ
                    for (j = 0; j <= item.length(); j++) {  //切れ目を探すために、文字列を順番に取り出す
                        String part = item.substring(st, j);   //部分的に取り出す文字列の候補
                        if (sjisLength(part) > colWidth || j == item.length()) {    //バイト数が超えるか、最後の文字なら
                            lineBuffer.get(col).add(item.substring(st, j - (j == item.length() ? 0 : 1)));
                                //列用の配列に、部分文字列を追加する。最後の場合と途中の場合で2つ目の引数が変わる
                            lineCount += 1; //行カウンタが増える
                            maxLines = lineCount > maxLines ? lineCount : maxLines; //1行分が最大何行に別れるかを求める
                            st = j - 1; //分割ポインタを進める
                        }
                    }
                    col += 1;   //列カウンタを進める
                }
                for (j = 0; j < maxLines; j++) {    //CSVのレコードを複数の行に渡って出力する
                    message = new StringJoiner(" | ", "| ", " |");  //1行分の文字列を接続で作る
                    for (k = 0; k < maxCols; k++) { //列の数だけ繰り返す
                        String item = "";
                        try {
                            item = lineBuffer.get(k).get(j);    //列のその行の文字列を取り出す
                        } catch (Exception e) { //文字列がないとgetで例外が出るが、無視する。その時は、itemは""
                            // ignored
                        }
                        message.add(item + repeatString(" ", maxWidths.get(k) - sjisLength(item)));
                            //文字列接続オブジェクトに追加するが、幅を決められたバイト数取るための空白の追加が必要
                    }
                    System.out.println(message);
                }

                //テーブル形式に出力（レコード表示の下にでる区切り線みたいな行）
                message = new StringJoiner("-+-", "+-", "-+");
                for (Integer elm : maxWidths) {
                    message.add(repeatString("-", elm));
                }
                System.out.println(message);
            }
        }
    }

    // 文字列をShift-JISでエンコードした時のバイト数を求める
    public static int sjisLength(String s) {
        int byteLen = 0;
        try {
            byteLen = s.getBytes("SJIS").length;
        } catch (Exception e) {
            e.printStackTrace();    //例外があれば、0を返してとりあえず続ける
        }
        return byteLen;
    }

    // 引数にし指定した文字列を繰り返した文字列を返す（Java11ではStringクラスにrepeatメソッドがあるそうです）
    public static String repeatString(String s, int times) {
        StringJoiner aItem = new StringJoiner("");
        for (int c = 0; c < times; c++) {
            aItem.add(s);
        }
        return aItem.toString();
    }
    
}
