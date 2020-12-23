(ns safehammad.grazing-goat
  (:gen-class))

;; The field is centred at (0,0) with a nominal radius of 1.
(def field-x 0)
(def field-y 0)
(def field-radius 1)

(defn dp
  "Format to p decimal places, defaulting to 2."
  ([p n] (format (str "%." p "f") n))
  ([n] (dp 2 n)))

(defn square [x] (* x x))

(defn point-in-circle?
  "Return true if circle with centre [centre-x centre-y] and radius r contains point [point-x point-y]."
  [centre-x centre-y r [point-x point-y]]
  (let [x (- point-x centre-x)
        y (- point-y centre-y)]
    (< (+ (square x) (square y)) (square r))))

(defn point-in-field? [point] (point-in-circle? field-x field-y field-radius point))
(defn point-in-rope? [rope-len point] (point-in-circle? field-radius field-y rope-len point))

(defn random-axis [] (- (rand (* 2 field-radius)) field-radius))  ; Random x or y value.
(defn random-point-in-field [] [(random-axis) (random-axis)])     ; Random point in square surrounding field.
(defn random-points [n] (repeatedly n random-point-in-field))     ; Generate n random points in field

(defn generate-outcomes
  "Generate `trials` random points with true/false outcomes where a point is in the roped area and/or the field."
  [trials rope-len]
  (map
    (fn [point] {:rope (point-in-rope? rope-len point) :field (point-in-field? point)})
    (random-points trials)))

(defn calculate-ratio
  "Given a set of outcomes, calculate the ratio of rope length to field radius."
  [outcomes]
  (let [in-field (filter :field outcomes)
        in-rope (filter :rope in-field)]
    (/ (count in-rope) (count in-field))))

(defn run []
  (for [rope-len (range 1.0 1.25 0.01)]
    (let [ratio (double (calculate-ratio (generate-outcomes 10000 rope-len)))]
      {:rope-len rope-len :ratio ratio})))

(defn -main
  [& args]
  (let [results (map #(update % :ratio dp) (run))
        successes (filter #(= "0.50" (:ratio %)) results)
        avg-rope-len (/ (apply + (map :rope-len successes)) (count successes))]
    (println "Trials:\n")
    (doseq [{:keys [rope-len ratio]} results]
      (println "Rope length:" (dp rope-len) "-> Ratio:" ratio (if (= ratio "0.50") " <---" "")))
    (println "\nApproximate rope length so goat can graze half the field:" (dp 3 avg-rope-len))))
