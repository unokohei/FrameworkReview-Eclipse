# FrameworkReview-Eclipse

Masayuki Nii 2018-10-20

Source code files are in the "src/topse/pattern" directory.

minimal-json is included. Thanks to developers.
https://github.com/ralfstx/minimal-json

---
Eclipseでレポジトリから直接読み込むには、以下のように操作します。
* File > Import とメニュー選択します。
* インポート先として、Gitの項目にあるClone URIを選択します。
* URIの指定では、このレポジトリのURL「https://github.com/msyk/FrameworkReview-Eclipse/」を指定します。
* そのほかはそのままでOKでしょう。

このレポジトリの中身はUTF-8ですが、EclipseのWindows版は既定のエンコーディングがShift-JISになっていることが多いので、プロジェクトを読み込んだあと、すぐに、ProjectメニューのPropertiesを選択して、Resourceの項目で、Text File EncodingをUTF-8に選択するなどしてください。

Windows以外で利用する場合は、src/topse/pattern/test/ToolTestRun.javaを見て、一部修正をしてください。クラスパスのセパレータが、Windowsの初期値である;担っていますが、それを:に変える必要があります。
