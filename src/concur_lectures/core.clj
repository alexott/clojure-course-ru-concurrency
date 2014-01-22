(ns concur-lectures.core)

(def ^:private counters-atom (atom {}))

(defn inc-counter [name]
  (swap! counters-atom update-in [name] (fnil inc 0)))

(defn dec-counter [name]
  (swap! counters-atom update-in [name] (fnil dec 0)))

(defn reset-counter [name]
  (swap! counters-atom assoc name 0))



(def ^:private acc-1 (ref 1000))
(def ^:private acc-2 (ref 1000))

(defn transfer-money [from to amount]
  (dosync
   (if (< @from amount)
     (throw (IllegalStateException. (str "Account has less money that required! "
                                         @from " < " amount)))
     (do (alter from - amount)
         (alter to + amount)))))

(defn add-to-deposit [to amount]
  (dosync
   (commute to + amount)))

(defn write-log [log-msg]
  (io!
   (println log-msg)))



(def ^:private counters-agent (agent {}))

(defn a-inc-counter [name]
  (send counters-agent update-in [name] (fnil inc 0)))

(defn a-dec-counter [name]
  (send counters-agent update-in [name] (fnil dec 0)))

(defn a-reset-counter [name]
  (send counters-agent assoc name 0))


(def err-agent (agent 1))

(send err-agent (fn [_] (throw (Exception. "we have a problem!"))))

(send err-agent identity)

(def err-agent (agent 1 :error-mode :continue))

(send err-agent inc)


(def ^:dynamic *test-var* 20)

(defn run-thread [x]
  (.run (fn []
            (binding [*test-var* (rand-int 10000)]
              (println "Thread " x " var=" *test-var*)))))

(defn run-thread2 [x]
  (.run (fn []
          (binding [*test-var* (rand-int 10000)]
            (println "Thread " x " var=" *test-var*)
            (set! *test-var* (rand-int 10000))
            (println "Thread " x " var2=" *test-var*)))))

(defn run-thread3 [x]
  (.run (fn []
          (set! *test-var* (rand-int 10000))
          (println "Thread " x " var2=" *test-var*))))

(doseq [x (range 3)] (run-thread x))
(doseq [x (range 3)] (run-thread2 x))


(def p (promise))
(do (future
      (Thread/sleep 5000)
      (deliver p :fred))
    @p)

(defn long-running-job [n]
     (Thread/sleep 3000)
     (+ n 10))

;; user=> (time (doall (map long-running-job (range 4))))
;; "Elapsed time: 12000.662614 msecs"
;; (10 11 12 13)
;; user=> (time (doall (pmap long-running-job (range 4))))
;; "Elapsed time: 3001.826403 msecs"
;; (10 11 12 13)
;; 

(defn func-1 [] 1)
(defn func-2 [] 2)
(defn func-3 [] 3)
(defn func-4 [] 4)
(defn func-5 [] 5)

(pcalls func-1 func-2 func-3 func-4 func-5)


(defn use-delays [x]
  {:result (delay (do (println "Evaluating result..." x) x))
   :some-info true})


