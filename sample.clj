(import 'javax.imageio.ImageIO
        'java.io.File
        'java.awt.Color)

(def *context* (make-context {:width 640 :height 480
                              :font (Font. "Gill Sans" 0 30)
                              :color (Color/white)
                              :background-image (ImageIO/read (File. "jellyfish.jpg"))}))

(defmacro title-page [& strs]
  `(with {:padding [50 30 100 30]} (fit (list ~@strs))))

(defmacro with-title [ttl & body]
  `(with {:padding [20 20 420 20]}
     (title ~ttl)
     (with-padding 40 ~@body)))

(def slides
     [(p (title-page "JavaからClojure"
                     "そして夢の世界へ")
         (with-size 20
           (with-padding 160
             (lines
              "アリエル・ネットワーク"
              "深町英太郎"
              "2010/05/18"))))
      (p (with-title "自己紹介"
           (item "深町英太郎です"
                 "Webプログラマ"
                 "Lisper (CL, Clojure)"
                 "Perl5, JavaScript..."
                 "オープンソース愛好家")))
      (p (with-title "入社の経緯"
           (item "最近Vim→Emacs"
                 "Emacs勉強会に参加"
                 "特に業務内容を知らずに応募"
                 "「Lispの仕事は、ありませんよ…？」"
                 "入社"
                 "Javaに苦戦中←今ここ")))
      (p (with-title "Java？"
           (item "Javaをやるのは大学以来"
                 "基本しかやってない"
                 "Spring？ Tomcat？ なにそれ？"
                 "オブジェクト指向？"
                 "全部クラス？")))
      (p (th "は？"))
      (p (with-title "OOP sucks"
           (item "Javaは全部オブジェクト"
                 "データも手続きも全部オブジェクト"
                 "→それぞれのクラスが何をするのか不明瞭"
                 "→うまく構造化できない")))
      (p (with-title "OOP sucks"
           (item "そもそも"
                 "余計な概念が多い"
                 "クラス？ インスタンス？ インターフェイス？"
                 "ハッシュとレキシカル関数で事足りる"
                 "→Lispのサブセットの再発明")))
      (p (th "なぜLispを" "使わないのですか？"))
      (p (with-title "Lispの短所"
           (item "仕様に並列処理がない"
                 "各処理系が独自実装"
                 "→保守が大変"
                 "ライブラリが少なめ"
                 "開発者が少なめ"
                 "情報が少なめ")))
      (p (with-title "一方、Javaは"
           (item "移植性が高い"
                 "豊富なライブラリ"
                 "GC"
                 "並列処理")))
      (p (th "Javaのほうが" "実践的？"))
      (p (th "いや、"))
      (p (th "そこで" "\"Clojure\"ですよ"))
      (p (with-title "\"Clojure\"とは"
           (item "JVMで動くLisp方言 (2008〜)"
                 "バイトコードを吐ける"
                 "データは基本immutable"
                 "並列処理をサポート"
                 "オブジェクト指向は非サポート※")))
      (p (with-title "ClojureはモダンLisp"
           (item "標準で有用なリードマクロ"
                 "letが高機能に"
                 "その他Lispとしては革新的な仕様"
                 "→Arcを意識？")))
      (p (with-title "たとえば、無名関数"
           (lines "・lambdaではなくfn"
                  "  (fn [to] (println \"Hello, \" to))"
                  "・さらにリードマクロで"
                  "  #(println \"Hello, \" to)"
                  "・2乗の数リスト"
                  "  (map #(* % %) [1 2 3])"
                  "  → [1 4 6]")))
      (p (with-title "データ"
           (item "Clojureではlistを使うことはあまりない"
                 "Vector [1 2 3 4 5]"
                 "Map {:name \"深町\", :age 22}"
                 "各要素へのアクセスも楽"
                 "([1 2 3 4 5] 3) → 4"
                 "({:name \"深町\", :age 22} :name) → \"深町\"")))
      (p (with-title "遅延評価"
           (item "Clojureでは積極的に評価が遅延"
                 "1から2倍になっていく無限リストは、")
           (lines "  (iterate #(* % 2) 1)"
                  "  → (1 2 4 8 16 32 64 128...)")
           (item "lazy-seqを使えばマイ無限リストも")))
      (p (with-title "高機能なlet"
           (lines "・カッコが少ない"
                  "  CL : (let ((a 10) (b 20)) ...)"
                  "  Clojure : (let [a 10, b 20] ...)"
                  "・分配機能付き"
                  "  (let [[a b & c] [1 2 3 4]]...)"
                  "  → a=1, b=2, c=(3 4)")))
      (p (with-title "let = let*"
           (item "Clojureのletはlet*と等価")
           (lines "  (let [time 365"
                  "        time (* time 24)"
                  "        time (* time 60)"
                  "        time (* time 60)]"
                  "    time)"
                  "  → 31536000")))
      (p (with-title "副作用"
           (item "Clojureは基本immutable"
                 "ただ、副作用も(使いたければ)使える")))
      (p (with-title "副作用"
           (lines "(let [sum (ref 0)]"
                  "  (doseq [e (range 0 100)]"
                  "    (dosync"
                  "      (alter sum #(+ % e))))"
                  "  @sum)"
                  "ただし、パフォーマンスに大きく影響")))
      (p (with-title "副作用を使わないと"
           (lines "(reduce + (range 0 100))")))
      (p (th "モダンLispツアー終了"))
      (p (th "「Javaとの親和性は？」"))
      (p (with-title "Javaへのアクセス"
           (lines "・コード中でJavaのクラスを直接利用可"
                  "  (def date (new java.util.Date))"
                  "・メソッドも呼べる"
                  "  (.getYear date) → 110")))
      (p (with-title "メソッドチェーン"
           (lines "・S式だとメソッドチェーンがわかりにくい"
                  "  (.getClass (.getYear (new java.util.Date)))"
                  "・Clojureでは簡易表現を用意"
                  "  (.. (new java.util.Date) getYear getClass)")))
      (p (with-title "型ヒント"
           (item "仮引数に型ヒントをつけられる")
           (lines "  (def add-str [#^String a, #^String b]"
                  "    (str a b))")
           (item "型ヒントをつけると速度があがるらしい")))
      (p (with-title "Javaのクラスを作る"
           (item "Javaのクラスも作れる"
                 "名前空間に:gen-classをつけるだけ")
           (lines "  (ns some.thing :gen-class)")
           (item "継承も可能")
           (lines "  (ns some.thing"
                  "    :gen-class :extends java.io.StringReader)"
                  "まああんまり使わないけど")))
      (p (with-title "メソッドのOverrideはProxy"
           (item "メソッドをOverrideするだけならproxy")
           (lines "(let [date"
                  "      (proxy [java.util.Date] []"
                  "        (getYear [] (+ 1900"
                  "                       (proxy-super getYear))))]"
                  "  (.getYear date))"
                  "→ 2010")))
      (p (th "Javaとの親和性ツアー終了"))
      (p (th "ここまで聞いて"))
      (p (th "「それ、Scalaでもできるよ」"))
      (p (th "と思った" "そこのアナタ！"))
      (p (th "Scalaって" "マクロ使えたっけ？"))
      (p (with-title "マクロ"
           (item "Clojureはマクロもサポート"
                 "CL風のマクロが使える"
                 "  (defmacro unless [pred then else]"
                 "    `(if (not ~pred) ~then ~else))")))
      (p (with-title "gensym"
           (item "gensymがリードマクロで定義"
                 "LOLのdefmacro/g!風に記述できる"
                 "というか使わないと例外が飛ぶ")))
      (p (with-title "gensym"
           (lines "  (defmacro today-date []"
                  "    `(let [date# (java.util.Date.)"
                  "           year# (+ 1900 (.getYear date#))"
                  "           mon# (+ 1 (.getMonth date#))"
                  "           day# (.getDate date#)]"
                  "       (str-join \"/\" [year# mon# day#])))")))
      (p (th "マクロツアー終了"))
      (p (th "ライブラリや開発環境は？"))
      (p (with-title "ライブラリ"
           (item "Javaのライブラリがそのまま利用"
                 "→あまり困らない"
                 "Clojureでも徐々に増えつつある"
                 "ORMやWAFなど基本的なもの出揃い")))
      (p (with-title "ビルドツール"
           (item "昔はAntやMavenを使っていた"
                 "でもLisperは非常にLazy"
                 "XML書くのは面倒"
                 "S式で書けばいいんじゃね？")))
      (p (with-title "Leiningen"
           (item "S式で設定を書ける"
                 "必要なライブラリを自動ダウンロード"
                 "もはやClojureには欠かせない"
                 "プラグイン追加できる")))
      (p (with-title "clojars.org"
           (item "jarファイルのホスティングサービス"
                 "Leiningenの作者が提供"
                 "Leiningenとの相性がいい"
                 "lein pushでプロジェクトのjarを公開")))
      (p (with-title "swank-clojure"
           (item "EmacsでLispの開発といえば"
                 "SLIMEですが"
                 "ClojureでSLIMEを使うには"
                 "→ swank-clojure"
                 "Clojureでもインタラクティブな開発可")))
      (p (with-title "VimClojure"
           (item "Vimもサポート"
                 "VimでSLIMEが使えるのはClojureだけ！")))
      (p (with-title "Eclipse"
           (item "あるらしいです")))
      (p (th "Web関連"))
      (p (with-title "Compojure"
           (item "RubyのSinatraを模した軽量WAF"
                 "ClojureのWAFのデファクト"
                 "だった"
                 "→Ringに吸収")))
      (p (with-title "AppEngine"
           (item "ClojureはAppEngineで動く"
                 "APIラッパー「appengine-clj」を使おう"
                 "github.com/fukamachi/appengine-clj"
                 "サーブレットはCompojureのdefroute")))
      (p (with-title "コミュニティ"
           (with-size 20
           (item "本家Googleグループ"
                 "http://groups.google.com/group/clojure"
                 "clojure-ja"
                 "http://groups.google.com/group/clojure-ja"
                 "日本Clojureユーザ会(仮)"
                 "http://clojure-users.org/"
                 "逆引きClojure"
                 "http://rd.clojure-users.org/"))))
      (p (with-title "Tokyo.clj"
           (item "毎月1回土曜にHackasonを開催"
                 "次回のTokyo.clj#3は6/26"
                 "http://twtvite.com/tokyoclj3"
                 "ハッシュタグ #tokyoclj")))
      (p (with-title "情報収集"
           (item "Planet Clojure"
                 "http://planet.clojure.in/"
                 "@clojurism"
                 "http://twitter.com/clojurism")))
      (p (th "最後にちょっと" "Clojureの愚痴"))
      (p (with-title "1. 末尾再帰の最適化"
           (item "再帰できない"
                 "末尾再帰でもできない"
                 "再帰するとStackOverFlow"
                 "loop & recurを使う")))
      (p (with-title "2. リードマクロがない"
           (item "自分でリードマクロを定義できない"
                 "まあ標準で定義されてるから困らないけど")))
      (p (with-title "3. エラーが読みづらい"
           (item "Javaのスタックトレースが読みづらい"
                 "Javaのクラスとして表示されてる"
                 "→メッセージ解読にコツがいる")))
      (p (with-title "4. 口頭で伝わらない"
           (item "Clojure"
                 "clojars.org"
                 "clojure-ja")))
      (p (th "以上"))
      ])
