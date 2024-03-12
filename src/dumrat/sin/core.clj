(ns dumrat.sin.core
  (:require [ring.adapter.jetty :as jetty]
            [dumrat.sin.transliteration :as trans]
            [clojure.java.io :as io]
            [dumrat.sin.http-server :as http-server]))

(def transliterator (trans/create-transliterator trans/vowel-mapping trans/pureconsonant-mapping))

(defn transliterate [text]
  (trans/transliterate transliterator text))

(comment
  (http-server/start-server 4001 transliterate)
  (http-server/stop-server)
  #_())

(defn send-to-server [text]
  (with-open [socket (java.net.Socket. "localhost" 4002)
              reader (io/reader socket)
              writer (io/writer socket)]
    (.write writer text)
    (.newLine writer)
    (.flush writer)
    (let [response (.readLine reader)]
      (println "Server response: " response)
      response)))

(comment

  (require '[criterium.core :as cc])

  (cc/quick-bench
   (send-to-server "samantha"))

  #_f)
