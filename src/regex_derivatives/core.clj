(ns regex-derivatives.core
  (:require [clojure.core.match :as cm]))

(defn nullable? [r]
  (cm/match [r]
    [:epsilon] true
    [:null] false
    [[:val _]] false
    [[:opt _]] true
    [[:star _]] true
    [[:alt a b]] (or (nullable? a) (nullable? b))
    [[:cat a b]] (and (nullable? a) (nullable? b))))

(defn derivative [r val]
  (cm/match [r]
    [:epsilon] :null
    [:null] :null
    [[:val a]] (if (= val a) :epsilon :null)
    [[:opt a]] [:opt (derivative a val)]
    [[:star a]] [:cat (derivative a val) [:star a]]
    [[:alt a b]] [:alt (derivative a val) (derivative b val)]
    [[:cat a b]] (if (nullable? a)
                   [:alt [:cat (derivative a val) b]
                         (derivative b val)]
                   [:cat (derivative a val) b])))

(defn matches? [r coll]
  (nullable? (reduce derivative r coll)))
