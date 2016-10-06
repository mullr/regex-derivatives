(ns regex-derivatives.live-test
  (:require [regex-derivatives.live :refer :all]
            [clojure.test :refer :all]))

(deftest nullable?-test
  (are [r val] (= val (nullable? r))
    :epsilon nil
    :null nil

    [:val 42] nil
    [:val :some-keyword] nil

    [:star [:val 42]] nil
    [:star :epsilon] nil
    [:star :null] nil

    [:alt [:star [:val \a]]
          [:val 42]] nil

    [:alt [:val 42]
          [:val \a]] nil

    [:cat [:star [:val \a]]
          [:val 42]] nil

    [:cat [:star [:val \a]]
          [:star [:val 42]]] nil))

(deftest derivative-test
  (are [r c r'] (= r' (derivative r c))
    [:val \a] \a, nil
    [:val \a] \b, nil

    :epsilon \a, nil
    :null \a, nil

    [:opt [:val \a]] \a, nil
    [:opt [:val \a]] \b, nil

    [:star [:val \a]] \a, nil
    [:star [:val \a]] \b, nil

    [:alt [:val \a] [:val \b]] \a, nil
    [:alt [:val \a] [:val \b]] \b, nil

    [:cat [:val \a] [:val \b]] \a, nil

    [:cat [:star [:val \a]]
          [:val \b]]           \a, nil

    [:cat [:star [:val \a]]
          [:val \b]]           \b, nil
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


