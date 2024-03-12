(ns dumrat.sin.transliteration
  (:require [clojure.string :as str]
            [instaparse.core :as insta]))

(def vowel-mapping
  {"a" "අ"
   "aa" "ආ"
   "A" "ඇ"
   "AA" "ඈ"
   "i" "ඉ"
   "ii" "ඊ"
   "ee" "ඊ"
   "u" "උ"
   "U" "ඌ"
   "sru" "ඍ"
   "srU" "ඎ"
   "e" "එ"
   "E" "ඒ"
   "I" "ඓ"
   "ei" "ඓ"
   "o" "ඔ"
   "O" "ඕ"
   "ou" "ඖ"
   "au" "ඖ"})

(def pureconsonant-mapping
  {"k" "ක්"
   "K" "ඛ්"
   "g" "ග්"
   "G" "ඝ්"
   ;"" "ඞ"
   "xg" "ඟ්"
   "ch" "ච්"
   "chh" "ඡ්"
   "Ch" "ඡ්"
   "j" "ජ්"
   "J" "ඣ"
   "xkdh" "ඤ්"
   "xgdh" "ඥ්"
   "xj" "ඦ"
   "t" "ට්"
   "T" "ඨ්"
   "d" "ඩ්"
   "D" "ඪ්"
   "N" "ණ්"
   "xd" "ඬ්"
   "th" "ත්"
   "Th" "ථ්"
   "thh" "ථ්"
   "dh" "ද්"
   "Dh" "ධ්"
   "n" "න්"
   "xdh" "ඳ්"
   "p" "ප්"
   "P" "ඵ්"
   "b" "බ්"
   "B" "භ්"
   "m" "ම්"
   "xb" "ඹ්"
   "xmb" "ඹ්"
   "y" "ය්"
   "r" "ර්"
   "l" "ල්"
   "w" "ව්"
   "v" "ව්"
   "sh" "ශ්"
   "Sh" "ෂ්"
   "s" "ස්"
   "h" "හ්"
   "L" "ළ්"
   "f" "ෆ්"
   "z" "ං"
   "H" "ඃ"})

(defn- letter-type-rule [mapping]
  (->> mapping
    (keys)
    (sort-by #(.length %))
    (map #(str "'" % "'"))
    (str/join "|")))

(defn create-grammar [vowel-mapping pureconsonant-mapping]
  (str "S=(whitespace* trans? whitespace*)*" ";\n"
       "trans=(vowel|pureconsonant|consonant)+"
       "vowel=" (letter-type-rule vowel-mapping) ";\n"
       "pureconsonant=" (letter-type-rule pureconsonant-mapping) ";\n"
       "consonant=pureconsonant,vowel" ";\n"
       "whitespace=#'(\\s|\\n|\\t)+'" ";\n"))

(defn create-transliterator [vowel-mapping pureconsonant-mapping]
  {:parser (insta/parser (create-grammar vowel-mapping pureconsonant-mapping))
   :vowel-mapping vowel-mapping
   :pureconsonant-mapping pureconsonant-mapping})

;; This is fixed
(def vowel-sign-vowel-mapping
  {"අ" ""
   "ආ" "ා"
   "ඇ" "ැ"
   "ඈ" "ෑ"
   "ඉ" "ි"
   "ඊ" "ී"
   "උ" "ු"
   "ඌ" "ූ"
   "ඍ" "ෘ"
   "ඎ" "ෲ"
   "එ" "ෙ"
   "ඒ" "ේ"
   "ඓ" "ෛ"
   "ඔ" "ො"
   "ඕ" "ෝ"
   "ඖ" "ෞ"})

(defn- combine [c v]
  (let [c (subs c 0 1)]
    (if (= v "අ")
      c (str c (vowel-sign-vowel-mapping v)))))

(defn transliterate [transliterator text]
  (let [parse ((:parser transliterator) text)
        failed? (insta/failure? parse)]
    (if failed? text
        (insta/transform
         {:pureconsonant
          #(if (string? %) ((:pureconsonant-mapping transliterator) %) %)
          :vowel
          #(if (string? %) ((:vowel-mapping transliterator) %) %)
          :consonant
          (fn [pureconsonant vowel]
            (if (and (string? pureconsonant) (string? vowel))
              (combine pureconsonant vowel)
              "test"))
          :trans str
          :whitespace identity
          :S str}
         parse))))

(comment
  (transliterate (create-transliterator vowel-mapping pureconsonant-mapping) "helO")

  (println (create-grammar vowel-mapping pureconsonant-mapping))

  (insta/visualize
   ((:parser (create-transliterator vowel-mapping pureconsonant-mapping)) "saamaya")
   :output-file "resources/parseexample1.png"
   :options {:dpi 128})

  #_())
