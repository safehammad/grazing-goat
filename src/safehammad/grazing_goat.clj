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
(defn point-in-rope? [rope-len point] (point-in-circle? field-radius field-y rope-len point))

(defn calculate-outcomes
  "Generate `trials` random points with true/false outcomes where a point is in the roped area and/or the field."
  [rope-len points]
  (map
    (fn [point] {:rope (point-in-rope? rope-len point) :field (point-in-field? point)})
    points))

(defn calculate-rope-len
  "Given a pair of results, calculate the average rope length weighted by how close the ratio is to 0.5."
  [[{rope-len1 :rope-len ratio1 :ratio} {rope-len2 :rope-len ratio2 :ratio}]]
  (let [proportion (/ (- 0.5 ratio1) (- ratio2 ratio1))]
    (+ rope-len1 (* proportion (- rope-len2 rope-len1)))))

(defn result-pair
  "Find the pair of results whose ratios are either side of 0.5."
  [trial-results]
  (->> (partition 2 1 trial-results)
       (filter (fn [[{ratio1 :ratio} {ratio2 :ratio}]] (< ratio1 0.5 ratio2)))
       (first)))

(defn calculate-ratio
  "Given a set of outcomes, calculate the ratio of rope length to field radius."
  [outcomes]
  (let [in-field (filter :field outcomes)
        in-rope  (filter :rope in-field)]
    (/ (count in-rope) (count in-field))))

(defn calculate-ratios
  "Run simulation with trials for a set of ever increasing rope lengths."
  [trials]
  (for [{:keys [rope-len points]} trials]
    (let [ratio (calculate-ratio (calculate-outcomes rope-len points))]
      {:rope-len rope-len :ratio ratio})))

(defn halton-at
  "Calculate the number in the Halton sequence at the given `index` for the given `base`."
  [index base]
  (loop [fraction 1
         result   0
         index    index]
    (if (pos? index)
      (let [fraction (/ fraction base)]
        (recur fraction
               (+ result (* fraction (mod index base)))
               (quot index base)))
      result)))

(defn halton-axis [n base]
  (let [halton-seq (map #(halton-at % base) (iterate inc 1))]
    (map #(-> % (* 2) (- 1)) (take n halton-seq))))

(defn halton-points []
  (let [xs (halton-axis 5000 2)
        ys (halton-axis 5000 3)
        points (map vector xs ys)]
    points))

(defn random-axis! [] (- (rand (* 2 field-radius)) field-radius))  ; Random x or y value.
(defn random-point-in-field! [] [(random-axis!) (random-axis!)])   ; Random point in square surrounding field.

(defn random-points! []
  (repeatedly 50000 random-point-in-field!))

(defn make-trials [points]
  (for [rope-len (range 1.0 1.25 0.01)]
    {:rope-len rope-len :points points}))

(defn run-simulation!
  [points]
  (let [trials        (make-trials points)
        trial-results (calculate-ratios trials)
        pair          (result-pair trial-results)
        rope-len      (calculate-rope-len pair)]
    (doseq [{:keys [rope-len ratio] :as result} trial-results]
      (println (format "Rope length: %.3f -> Ratio: %.3f %s" rope-len (float ratio) (if ((set pair) result) " <---" ""))))
    (println (format "Approximate rope length so goat can graze half the field: %.4f" rope-len))))

(defn -main
  [& _]
  (print "\nRandom Trials:\n")
  (run-simulation! (random-points!))
  (print "\nHalton Trials:\n")
  (run-simulation! (halton-points)))
