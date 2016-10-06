(ns regex-derivatives.core-test
  (:require [clojure.test :refer :all]
            [regex-derivatives.core :refer :all]))

(deftest nullable?-test
  (are [r val] (= val (nullable? r))
    :epsilon true
    :null false

    [:val 42] false
    [:val :some-keyword] false

    [:star [:val 42]] true
    [:star :epsilon] true
    [:star :null] true

    [:alt [:star [:val \a]]
          [:val 42]] true

    [:alt [:val 42]
          [:val \a]] false

    [:cat [:star [:val \a]]
          [:val 42]] false

    [:cat [:star [:val \a]]
          [:star [:val 42]]] true))

(deftest derivative-test
  (are [r c r'] (= r' (derivative r c))
    [:val \a] \a, :epsilon
    [:val \a] \b, :null

    :epsilon \a, :null
    :null \a, :null

    [:opt [:val \a]] \a, [:opt :epsilon]
    [:opt [:val \a]] \b, [:opt :null]

    [:star [:val \a]] \a, [:cat :epsilon [:star [:val \a]]]
    [:star [:val \a]] \b, [:cat :null [:star [:val \a]]]

    [:alt [:val \a] [:val \b]] \a, [:alt :epsilon :null]
    [:alt [:val \a] [:val \b]] \b, [:alt :null :epsilon]

    [:cat [:val \a] [:val \b]] \a, [:cat :epsilon [:val \b]]

    [:cat [:star [:val \a]]
          [:val \b]]           \a, [:alt [:cat [:cat :epsilon
                                                     [:star [:val \a]]]
                                               [:val \b]]
                                         :null]

    [:cat [:star [:val \a]]
          [:val \b]]           \b, [:alt [:cat [:cat :null
                                                     [:star [:val \a]]]
                                               [:val \b]]
                                         :epsilon]
    ))

(deftest matches?-test
  (is (matches? [:val \a] "a"))
  (is (not (matches? [:val \a] "ab")))
  (is (matches? [:cat [:val \a] [:val \b]] "ab"))
  (is (matches? [:cat [:star [:val \a]] [:val \b]] "ab"))
  (is (matches? [:cat [:star [:val \a]] [:val \b]] "aaaab"))
  (is (matches? [:cat [:star [:val \a]] [:val \b]] "b"))
  (is (not (matches? [:cat [:star [:val \a]] [:val \b]] "aaaac")))
)
