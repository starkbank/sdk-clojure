(ns starkbank.utils.date)

(defn future-date
    ([]
        (def date (java.util.Date. (+ (* (+ 1 (rand-int 6)) 86400 1000) (System/currentTimeMillis))))
        (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") date)))

    ([n]
        (def date (java.util.Date. (+ (* n 86400 1000) (System/currentTimeMillis))))
        (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") date))))

(defn future-datetime
    ([]
        (def date (java.util.Date. (+ (* (+ 1 (rand-int 6)) 86400 1000) (System/currentTimeMillis))))
        (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSS") date) "+00:00"))

    ([n]
        (def date (java.util.Date. (+ (* n 86400 1000) (System/currentTimeMillis))))
        (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSS") date) "+00:00")))
