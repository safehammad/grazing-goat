(ns safehammad.grazing-goat
  (:gen-class))

;; The field is centred at (0,0) with a nominal radius of 1.
(def field-x 0)
(def field-y 0)
(def field-radius 1)

(defn point-in-circle?
  [centre-x centre-y radius [point-x point-y]]
  (< (+ (Math/pow (- point-x centre-x) 2) (Math/pow (- point-y centre-y) 2))
     (Math/pow radius 2)))

(def point-in-field? (partial point-in-circle? field-x field-y field-radius))
(def point-in-rope?
  "Is the point in the roped area? The rope is tied to the right edge of the field i.e. (1,0)."
  (partial point-in-circle? field-radius field-y))

(defn random-axis [] (- (rand (* 2 field-radius)) field-radius))
(defn random-point-in-field [] [(random-axis) (random-axis)])

(defn generate-outcomes
  "Generate n random points resulting in n outcomes where an outcome is [point-in-rope, point-in-field]."
  [n rope-len]
  (map
    (fn [point] [(point-in-rope? rope-len point) (point-in-field? point)])
    (repeatedly n random-point-in-field)))

(defn calculate-ratio
  "Given a set of outcomes, calculate the ratio of rope length to field radius."
  [outcomes]
  (->> outcomes
       (map (fn [[rope field]] [(if (and rope field) 1 0) (if field 1 0)]))
       (apply (partial map vector))
       (map (partial apply +))
       (apply /)
       double))

(defn run []
  (for [rope-len (range 1.0 1.25 0.01)]
    (let [outcomes (generate-outcomes 10000 rope-len)
          result (calculate-ratio outcomes)]
      [rope-len (format "%.2f" result)])))
      
(defn -main
  [& args]
  (let [results (run)
        successes (filter #(= "0.50" (second %)) results)
        avg-rope-len (/ (apply + (map first successes)) (count successes))]
    (println "Trials:\n")
    (doseq [[rope-len result] results]
      (println "Rope length:" (format "%.2f" rope-len) "-> Ratio:" result (if (= result "0.50") " <---" "")))
    (println "\nApproximate rope length so goat can graze half the field:" (format "%.3f" avg-rope-len))))
