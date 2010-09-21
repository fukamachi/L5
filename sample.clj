(ns L5.sample
  (:use L5 L5.layout)
  (:import [java.awt Color]))

(defcontext
  {:width 640 :height 480
   :font-family "Gill Sans"
   :font-size 30
   :color (Color/white)
   :background-color Color/darkGray})

(defslides
  [(t "JavaからClojure"
      "そして夢の世界へ")
   (with-size 15
     (lines "アリエル・ネットワーク"
            "深町英太郎"
            "2010/05/18"))]
  [(title "自己紹介")
   (item "深町英太郎です"
         "Webプログラマ"
         "Lisper (CL, Clojure)"
         "Perl5, JavaScript..."
         "オープンソース愛好家")]
  [(title "入社の経緯")
   (item "最近Vim→Emacs"
         "Emacs勉強会に参加"
         "特に業務内容を知らずに応募"
         "「Lispの仕事は、ありませんよ…？」"
         "入社"
         "Javaに苦戦中←今ここ")]
  [(title "Java？")
   (item "Javaをやるのは大学以来"
         "基本しかやってない"
         "Spring？ Tomcat？ なにそれ？"
         "オブジェクト指向？"
         "全部クラス？")]
  [(t "は？")]
  [(title "OOP sucks")
   (item "Javaは全部オブジェクト"
         "データも手続きも全部オブジェクト"
         "→それぞれのクラスが何をするのか不明瞭"
         "→うまく構造化できない")]
  [(title "OOP sucks")
   (item "そもそも"
         "余計な概念が多い"
         "クラス？ インスタンス？ インターフェイス？"
         "ハッシュとレキシカル関数で事足りる"
         "→Lispのサブセットの再発明")]
  [(t "なぜLispを" "使わないのですか？")]
  [(title "Lispの短所")
   (item "仕様に並列処理がない"
         "各処理系が独自実装"
         "→保守が大変"
         "ライブラリが少なめ"
         "開発者が少なめ"
         "情報が少なめ")]
  [(title "一方、Javaは")
   (item "移植性が高い"
         "豊富なライブラリ"
         "GC"
         "並列処理")]
  [(t "Javaのほうが" "実践的？")]
  [(t "いや、")]
  [(t "そこで" "\"Clojure\"ですよ")]
  [(title "\"Clojure\"とは")
   (item "JVMで動くLisp方言 (2008〜)"
         "バイトコードを吐ける"
         "データは基本immutable"
         "並列処理をサポート"
         "オブジェクト指向は非サポート※")]
  [(title "ClojureはモダンLisp")
   (item "標準で有用なリードマクロ"
         "letが高機能に"
         "その他Lispとしては革新的な仕様"
         "→Arcを意識？")]
  [(title "たとえば、無名関数")
   (lines "・lambdaではなくfn"
          "  (fn [to] (println \"Hello, \" to))"
          "・さらにリードマクロで"
          "  #(println \"Hello, \" %)"
          "・2乗の数リスト"
          "  (map #(* % %) [1 2 3])"
          "  → [1 4 6]")]
  [(title "データ")
   (item "Clojureではlistを使うことはあまりない"
         "Vector [1 2 3 4 5]"
         "Map {:name \"深町\", :age 22}"
         "各要素へのアクセスも楽"
         "([1 2 3 4 5] 3) → 4"
         "({:name \"深町\", :age 22} :name) → \"深町\"")]
  [(title "遅延評価")
   (item "Clojureでは積極的に評価が遅延"
         "1から2倍になっていく無限リストは、")
   (lines "  (iterate #(* % 2) 1)"
          "  → (1 2 4 8 16 32 64 128...)")
   (item "lazy-seqを使えばマイ無限リストも")]
  [(title "高機能なlet")
   (lines "・カッコが少ない"
          "  CL : (let ((a 10) (b 20)) ...)"
          "  Clojure : (let [a 10, b 20] ...)"
          "・分配機能付き"
          "  (let [[a b & c] [1 2 3 4]]...)"
          "  → a=1, b=2, c=(3 4)")]
  [(title "let = let*")
   (item "Clojureのletはlet*と等価")
   (lines "  (let [time 365"
          "        time (* time 24)"
          "        time (* time 60)"
          "        time (* time 60)]"
          "    time)"
          "  → 31536000")]
  [(title "副作用")
   (item "Clojureは基本immutable"
         "ただ、副作用も(使いたければ)使える")]
  [(title "副作用")
   (lines "(let [sum (ref 0)]"
          "  (doseq [e (range 0 100)]"
          "    (dosync"
          "      (alter sum #(+ % e))))"
          "  @sum)"
          "ただし、パフォーマンスに大きく影響")]
  [(title "副作用を使わないと")
   (lines "(reduce + (range 0 100))")]
  [(t "モダンLispツアー終了")]
  [(t "「Javaとの親和性は？」")]
  [(title "Javaへのアクセス")
   (lines "・コード中でJavaのクラスを直接利用可"
          "  (def date (new java.util.Date))"
          "・メソッドも呼べる"
          "  (.getYear date) → 110")]
  [(title "メソッドチェーン")
   (lines "・S式だとメソッドチェーンがわかりにくい"
          "  (.getClass (.getYear (new java.util.Date)))"
          "・Clojureでは簡易表現を用意"
          "  (.. (new java.util.Date) getYear getClass)")]
  [(title "型ヒント")
   (item "仮引数に型ヒントをつけられる")
   (lines "  (def add-str [#^String a, #^String b]"
          "    (str a b))")
   (item "型ヒントをつけると速度があがるらしい")]
  [(title "Javaのクラスを作る")
   (item "Javaのクラスも作れる"
         "名前空間に:gen-classをつけるだけ")
   (lines "  (ns some.thing :gen-class)")
   (item "継承も可能")
   (lines "  (ns some.thing"
          "    :gen-class :extends java.io.StringReader)"
          "まああんまり使わないけど")]
  [(title "メソッドのOverrideはProxy")
   (item "メソッドをOverrideするだけならproxy")
   (lines "(let [date"
          "      (proxy [java.util.Date] []"
          "        (getYear [] (+ 1900"
          "                       (proxy-super getYear))))]"
          "  (.getYear date))"
          "→ 2010")]
  [(t "Javaとの親和性ツアー終了")]
  [(t "ここまで聞いて")]
  [(t "「それ、Scalaでもできるよ」")]
  [(t "と思った" "そこのアナタ！")]
  [(t "Scalaって" "マクロ使えたっけ？")]
  [(title "マクロ")
   (item "Clojureはマクロもサポート"
         "CL風のマクロが使える"
         "  (defmacro unless [pred then else]"
         "    `(if (not ~pred) ~then ~else))")]
  [(title "gensym")
   (item "gensymがリードマクロで定義"
         "LOLのdefmacro/g!風に記述できる"
         "というか使わないと例外が飛ぶ")]
  [(title "gensym")
   (lines "  (defmacro today-date []"
          "    `(let [date# (java.util.Date.)"
          "           year# (+ 1900 (.getYear date#))"
          "           mon# (+ 1 (.getMonth date#))"
          "           day# (.getDate date#)]"
          "       (str-join \"/\" [year# mon# day#])))")]
  [(t "マクロツアー終了")]
  [(t "ライブラリや開発環境は？")]
  [(title "ライブラリ")
   (item "Javaのライブラリがそのまま利用"
         "→あまり困らない"
         "Clojureでも徐々に増えつつある"
         "ORMやWAFなど基本的なもの出揃い")]
  [(title "ビルドツール")
   (item "昔はAntやMavenを使っていた"
         "でもLisperは非常にLazy"
         "XML書くのは面倒"
         "S式で書けばいいんじゃね？")]
  [(title "Leiningen")
   (item "S式で設定を書ける"
         "必要なライブラリを自動ダウンロード"
         "もはやClojureには欠かせない"
         "プラグイン追加できる")]
  [(title "clojars.org")
   (item "jarファイルのホスティングサービス"
         "Leiningenの作者が提供"
         "Leiningenとの相性がいい"
         "lein pushでプロジェクトのjarを公開")]
  [(title "swank-clojure")
   (item "EmacsでLispの開発といえば"
         "SLIMEですが"
         "ClojureでSLIMEを使うには"
         "→ swank-clojure"
         "Clojureでもインタラクティブな開発可")]
  [(title "VimClojure")
   (item "Vimもサポート"
         "VimでSLIMEが使えるのはClojureだけ！")]
  [(title "Eclipse")
   (item "あるらしいです")]
  [(t "Web関連")]
  [(title "Compojure")
   (item "RubyのSinatraを模した軽量WAF"
         "ClojureのWAFのデファクト"
         "だった"
         "→Ringに吸収")]
  [(title "AppEngine")
   (item "ClojureはAppEngineで動く"
         "APIラッパー「appengine-clj」を使おう"
         "github.com/fukamachi/appengine-clj"
         "サーブレットはCompojureのdefroute")]
  [(title "コミュニティ")
   (with-size 20
     (item "本家Googleグループ"
           "http://groups.google.com/group/clojure"
           "clojure-ja"
           "http://groups.google.com/group/clojure-ja"
           "日本Clojureユーザ会(仮)"
           "http://clojure-users.org/"
           "逆引きClojure"
           "http://rd.clojure-users.org/"))]
  [(title "Tokyo.clj")
   (item "毎月1回土曜にHackasonを開催"
         "次回のTokyo.clj#3は6/26"
         "http://twtvite.com/tokyoclj3"
         "ハッシュタグ #tokyoclj")]
  [(title "情報収集")
   (item "Planet Clojure"
         "http://planet.clojure.in/"
         "@clojurism"
         "http://twitter.com/clojurism")]
  [(t "最後にちょっと" "Clojureの愚痴")]
  [(title "1. 末尾再帰の最適化")
   (item "再帰できない"
         "末尾再帰でもできない"
         "再帰するとStackOverFlow"
         "loop & recurを使う")]
  [(title "2. リードマクロがない")
   (item "自分でリードマクロを定義できない"
         "まあ標準で定義されてるから困らないけど")]
  [(title "3. エラーが読みづらい")
   (item "Javaのスタックトレースが読みづらい"
         "Javaのクラスとして表示されてる"
         "→メッセージ解読にコツがいる")]
  [(title "4. 口頭で伝わらない")
   (item "Clojure"
         "clojars.org"
         "clojure-ja")]
  [(t "以上")]
  )
