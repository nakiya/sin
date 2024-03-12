(ns dumrat.sin.http-server
  (:require [ring.adapter.jetty :as jetty]))

(defn handler [transliterate request]
  (let [input (slurp (:body request))]
    {:status 200
     :headers {"Content-Type" "text/plain;charset=utf-8"}
     :body (transliterate input)}))

(defonce server (atom nil))

(defn start-server [port transliterate]
  (reset! server (jetty/run-jetty (partial handler transliterate) {:port port :join? false})))

(defn stop-server []
  (.stop @server)
  (reset! server nil))
