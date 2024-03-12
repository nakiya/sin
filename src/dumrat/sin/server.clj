(ns dumrat.sin.socket-server
  (:require [clojure.java.io :as io]))

(defonce server-atom (atom nil))

(defn handle-client [client-socket transliterate]
  (with-open [reader (io/reader client-socket)
              writer (io/writer client-socket)]
    (loop []
      (if-let [input (.readLine reader)]
        (let [output (transliterate input)]
          (println "Received: " input)
          (println "Transliterated: " output)
          (.write writer output)
          (.flush writer)
          (recur))
        (do
          (println "Client closed socket")
          nil)))))

(defn start-socket-server [port transliterate]
  (let [server-socket (java.net.ServerSocket. port)]
    (println "Server started on port " port)
    (future
      (loop []
        (let [client-socket (.accept server-socket)]
          (future (handle-client client-socket transliterate))
          (recur))))
    server-socket))

(defn stop-socket-server [server-socket]
  (when server-socket
    (.close server-socket)
    (println "Server stopped")))

(defn start [port transliterate]
  (reset! server-atom (start-socket-server port transliterate)))

(defn stop []
  (let [server-socket @server-atom]
    (stop-socket-server server-socket)
    (reset! server-atom nil)))
