(ns safehammad.grazing-goat
  (:gen-class))

;; The field is centred at (0,0) with a nominal radius of 1.
(def field-x 0)
(def field-y 0)
(def field-radius 1)

(defn point-in-circle?
  "Return true if circle with centre [centre-x centre-y] and radius r contains point [point-x point-y]."
  [centre-x centre-y r [point-x point-y]]
  (let [x (- point-x centre-x)
        y (- point-y centre-y)]
    (< (+ (* x x) (* y y)) (* r r))))

(defn point-in-field? [point] (point-in-circle? field-x field-y field-radius point))
(defn point-in-roped-area? [rope-len point] (point-in-circle? field-radius field-y rope-len point))

(defn random-axis [] (- (rand (* 2 field-radius)) field-radius))  ; Random x or y value.
(defn random-point-in-field [] [(random-axis) (random-axis)])     ; Random point in square surrounding field.

(defn generate-outcomes
  "Generate n random points resulting in n outcomes where an outcome is [point-in-rope, point-in-field]."
  [n rope-len]
  (map
    (fn [point] [(point-in-roped-area? rope-len point) (point-in-field? point)])
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
