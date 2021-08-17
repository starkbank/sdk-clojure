(ns starkbank.utils.page)
(declare has-repeated-values?)
(declare get-iterations)
(declare check-repeated-ids)
(declare fetch-page)
(declare extend-ids)
(declare get-page)

(defn get-ids
  [function, iterations, options]
    (get-page function iterations [] [] options))

(defn- get-page
  [function, iterations, ids, entities, options]
    (if (and (<= iterations 0) (= 0 (count entities)))
      (check-repeated-ids function, iterations, ids, entities, options)
      (if (= 0 (count entities))
        (fetch-page function, iterations, ids, entities, options)
        (extend-ids function, iterations, ids, entities, options))))

(defn- check-repeated-ids
  [function, iterations, ids, entities, options]
    (if (has-repeated-values? ids) 
      (throw (Exception. "Repeated IDs"))
      ids))

(defn- has-repeated-values? [seq]
  (not (empty? (for [[id freq] (frequencies seq) :when (> freq 1)] id))))

(defn- fetch-page
  [function, iterations, ids, entities, options]
    (def page (function options))
    (def new-cursor (get page :cursor))
    (def new-entities (get (first (dissoc page :cursor)) 1))
    (get-page function (get-iterations iterations new-cursor) ids new-entities (assoc options :cursor new-cursor)))

(defn- get-iterations [iterations, cursor]
  (if (nil? cursor) 0 (- iterations 1)))

(defn- extend-ids
  [function, iterations, ids, entities, options]
    (def all-ids (map (fn [entity] (:id entity)) entities))
    (get-page function iterations (concat ids all-ids) [], options))
