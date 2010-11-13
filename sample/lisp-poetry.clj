(ns L5.lisp_poetry
  (:use L5 L5.layout))

(defcontext
  {:width 640 :height 480
   :font-family "Gill Sans"
   :font-size 30})

(defslides
  [(t "Lisp Poetry")
   (with {:font-size 15
          :position :fixed
          :padding {:top 360}}
     (lines "アリエル・ネットワーク"
            "深町英太郎"
            "2010/08/21"))]
  [(title "About Me")
   (item "深町英太郎です (@nitro_idiot)"
         "Lisper (CL, Clojure)"
         "一応Emacs"
         "オープンソース愛好家"
         "手嶋屋でOpenPNE→アリエルでJava")
   (with-padding {:top 150 :right 0 :bottom 0 :left 500}
     (with {:position :fixed} (img "redhat.png")))]
  [(title "About Me")
   (item "基本的にLispが好きなんだけど…")]
  [(title "About Me")
   (item "基本的にLispが好きなんだけど…"
         "実はPerlも好きだったりする")]
  [(title "What's Perl")
   (item "自由度が高い"
         "自由度が高い"
         "自由度が高すぎる")]
  [(title "What's Perl")
   (item "というのも、"
         "作者は言語学者(Larry Wall)"
         "自然言語を意識した構文と文法")]
  [(title "Example")
   (lines "if ($age < 20) {"
          "    print \"I love you!\";"
          "}")]
  [(title "Example")
   (lines "print \"I love you!\" if $age < 20;")]
  [(title "Example")
   (lines "print \"I love you!\" if $age < 20;"
          "print \"Ah..\" if not $age < 20;")]
  [(title "Example")
   (lines "print \"I love you!\" if $age < 20;"
          "print \"Ah..\" if not $age < 20;"
          "print \"I love you!\" while $age < 20;")]
  [(t "詩")]
  [(title "Perl Poetry")
   (lines "If you understand, things are such as they are."
          "If you do not understand, things are such as"
          "they are.")]
  [(title "Perl Poetry")
   (lines "if ($you{understand}) {"
         "    @things = qw(such as they are);"
         "}"
         "else {"
         "    @things = qw(such as they are);"
         "}")]
  [(title "Perl Poetry")
   (lines "if ($you{understand}) {"
          "    @things = @things;"
          "}"
          "elsif (not $you{understand}) {"
          "    @things = @things;"
          "}")]
  [(title "Perl Poetry")
   (lines "@things = @things if $you{understand};"
          "@things = @things if not $you{understand};")]
  [(title "Perl Poetry")
   (lines "@things = ($you{understand})"
          "      ? @things"
          "      : @things;")]
  [(title "Perl Poetry")
   (lines "@things = @things;")]
  [(title "Perl Poetry")
   (lines "@things;")]
  [(title "Perl Poetry")
   (lines)]
  [(t "無の" "境地")]
  [(t "Perlに" "できるなら、")]
  [(t "Lispに" "できない")]
  [(t "わけが" "ない")]
  [(t "作ってみた")]
  [(title "Lisp Poetry")
   (with-size 25
     (lines ";; What language would you need?"
           "(defun recommend-language (you)"
           "  (case (what you :need 4 'language)"))]
  [(title "Lisp Poetry")
   (with-size 25
     (lines ";; What language would you need?"
            "(defun recommend-language (you)"
            "  (case (what you :need 4 'language)"
            "    (easy? (use 'lisp))"
            "    (abstraction? (use 'lisp))"
            "    (fast? (use 'lisp))"
            "    (fun? (use 'lisp))))"))]
  [(title "Lisp Poetry")
   (with-size 25
     (lines ";; What language would you need?"
            "(use 'lisp)"))]
  [(t "(use 'lisp)")]
  [(t "もう一個")]
  [(title "Lisp Poetry II")
   (with-size 25
     (lines ";; What would you like to create?"
            "(defun recommend-language (you)"
            "  (case (what you :want 2 'do)"))]
  [(title "Lisp Poetry II")
   (with-size 25
     (lines ";; What would you like to create?"
            "(defun recommend-language (you)"
            "  (case (what you :want 2 'do)"
            "    (AI? (use 'lisp))"
            "    (compiler? (use 'lisp))"
            "    (web-app? (use 'lisp))"
            "    (iphone-app? (use 'lisp))"
            "    (poem? (use '????))))"))]
  [(title "Lisp Poetry II")
   (with-size 25
     (lines ";; What would you like to create?"
            "(defun recommend-language (you)"
            "  (case (what you :want 2 'do)"
            "    (AI? (use 'lisp))"
            "    (compiler? (use 'lisp))"
            "    (web-app? (use 'lisp))"
            "    (iphone-app? (use 'lisp))"
            "    (poem? (use 'perl))))"))]
  [(t "(use 'lisp)")
   (with {:padding {:top 100 :right 100 :bottom 100 :left 100}
          :font-size 15
          :text-align :center
          :position :fixed}
     "(use 'perl)")]
  [(title "まとめ")]
  [(title "まとめ")
   (item "Lispを使え"
         "Lispを使え"
         "Lispを使え")]
  [(title "まとめ")
   (item "Lispを使え"
         "Lispを使え"
         "Lispを使え"
         "でも詩を書くならPerlだよね。")]
  [(t "おわり")]
  [(title "See Also")
   (lines
    "・Zen And The Art Of Perl Poetry"
    "<http://www.perlmonks.org/?node_id=170492>")]
  )
