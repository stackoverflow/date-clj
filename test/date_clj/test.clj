(ns date-clj.test
  (:use [date-clj])
  (:use [clojure.test])
  (:import (java.util Locale)))

(def july-15-2000 (date :day 15 :month 6 :year 2000
                        :hour 10 :minute 30 :second 20 :millisecond 500))

(deftest date-and-parts-tests
  (let [date1 (date :day 1 :month :july :year 1980)
        date2 (date :week-day :tuesday :month 4
                    :hour 3 :minute 12 :second 23 :millisecond 890)]
    (is (= (year (date 0)) 1969) "date - wrong timestamp constructor")
    (is (= (day date1) 1) "date - wrong day set")
    (is (= (month date1) 6) "date - wrong keyword month set")
    (is (= (year date1) 1980) "date - wrong year set")
    (is (= (hours date1) 0) "date - wrong default hour set")
    (is (= (minutes date1) 0) "date - wrong default minute set")
    (is (= (seconds date1) 0) "date - wrong default second set")
    (is (= (milliseconds date1) 0) "date - wrong default millisecond set")
    (is (= (week-day date2) 3) "date - wrong week day set")
    (is (= (month date2) 4) "date - wrong month set")
    (is (= (hours date2) 3) "date - wrong hour set")
    (is (= (minutes date2) 12) "date - wrong minute set")
    (is (= (seconds date2) 23) "date - wrong second set")
    (is (= (milliseconds date2) 890) "date - wrong millisecond set")))

(deftest set-date-tests
  (let [date1 (set-date july-15-2000 :year 2003 :day 17 :month :august :hour 14)]
    (is (= (year date1) 2003) "set-date - wrong year")
    (is (= (day date1) 17) "set-date - wrong day")
    (is (= (month date1) 7) "set-date - wrong month")
    (is (= (hours date1) 14) "set-date - wrong hour")))

;; tests from-now subtract and back too because it's a call to add
(deftest add-tests
  (let [add1 (add july-15-2000 4 :days)
        add2 (add july-15-2000 2 :months)
        add3 (add july-15-2000 5 :years)
        add4 (add july-15-2000 1 :week)
        add5 (add july-15-2000 15 :hours)
        add6 (add july-15-2000 10 :minutes)
        add7 (add july-15-2000 14 :seconds)
        add8 (add july-15-2000 345 :milliseconds)]
    (is (= (day add1) 19) "add - wrong day add")
    (is (= (month add2) 8) "add - wrong month add")
    (is (= (year add3) 2005) "add - wrong year add")
    (is (= (day add4) 22) "add - wrong day add")
    (is (= (hours add5) 1) "add - wrong hours add")
    (is (= (day add5) 16) "add - wrong day add")
    (is (= (minutes add6) 40) "add - wrong minutes add")
    (is (= (seconds add7) 34) "add - wrong seconds add")
    (is (= (milliseconds add8) 845))) "add - wrong milliseconds add")

(deftest following-tests
  (is (= (year (following july-15-2000 :year)) 2001) "following - wrong next year")
  (is (= (month (following july-15-2000 :month)) 7) "following - wrong next month")
  (is (= (day (following july-15-2000 :week)) 22) "following - wrong next week")
  (is (= (day (following july-15-2000 :day)) 16) "following - wrong next day")
  (is (= (hours (following july-15-2000 :hour)) 11) "following - wrong next hour")
  (is (= (minutes (following july-15-2000 :minute)) 31) "following - wrong next minute")
  (is (= (seconds (following july-15-2000 :second)) 21) "following - wrong next second")
  (is (= (milliseconds (following july-15-2000 :millisecond)) 501) "following - wrong next millisecond")
  (is (= (day (following july-15-2000 :sunday)) 16) "following - wrong next week day")
  (is (= (day (following july-15-2000 :monday)) 17) "following - wrong next week day")
  (is (= (day (following july-15-2000 :tuesday)) 18) "following - wrong next week day")
  (is (= (day (following july-15-2000 :wednesday)) 19) "following - wrong next week day")
  (is (= (day (following july-15-2000 :thursday)) 20) "following - wrong next week day")
  (is (= (day (following july-15-2000 :friday)) 21) "following - wrong next week day")
  (is (= (day (following july-15-2000 :saturday)) 22) "following - wrong next week day")
  (is (= (month (following july-15-2000 :january)) 0) "following - wrong next month")
  (is (= (year (following july-15-2000 :january)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :february)) 1) "following - wrong next month")
  (is (= (year (following july-15-2000 :february)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :march)) 2) "following - wrong next month")
  (is (= (year (following july-15-2000 :march)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :april)) 3) "following - wrong next month")
  (is (= (year (following july-15-2000 :april)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :may)) 4) "following - wrong next month")
  (is (= (year (following july-15-2000 :may)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :june)) 5) "following - wrong next month")
  (is (= (year (following july-15-2000 :june)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :july)) 6) "following - wrong next month")
  (is (= (year (following july-15-2000 :july)) 2001) "following - wrong next month")
  (is (= (month (following july-15-2000 :august)) 7) "following - wrong next month")
  (is (= (month (following july-15-2000 :september)) 8) "following - wrong next month")
  (is (= (month (following july-15-2000 :october)) 9) "following - wrong next month")
  (is (= (month (following july-15-2000 :november)) 10) "following - wrong next month")
  (is (= (month (following july-15-2000 :december)) 11) "following - wrong next month"))

;; tests was? and will-be? too because they are aliases
(deftest is?-tests
  (is (is? july-15-2000 15 2000) "is? - wrong day and/or year check")
  (is (is? july-15-2000 :leap-year) "is? - wrong leap year check")
  (is (is? july-15-2000 :weekend) "is? - wrong weekend check")
  (is (not (is? july-15-2000 :sunday)) "is? - wrong week day check")
  (is (not (is? july-15-2000 :monday)) "is? - wrong week day check")
  (is (not (is? july-15-2000 :tuesday)) "is? - wrong week day check")
  (is (not (is? july-15-2000 :wednesday)) "is? - wrong week day check")
  (is (not (is? july-15-2000 :thursday)) "is? - wrong week day check")
  (is (not (is? july-15-2000 :friday)) "is? - wrong week day check")
  (is (is? july-15-2000 :saturday) "is? - wrong week day check")
  (is (not (is? july-15-2000 :january)) "is? - wrong month check")
  (is (not (is? july-15-2000 :february)) "is? - wrong month check")
  (is (not (is? july-15-2000 :march)) "is? - wrong month check")
  (is (not (is? july-15-2000 :april)) "is? - wrong month check")
  (is (not (is? july-15-2000 :may)) "is? - wrong month check")
  (is (not (is? july-15-2000 :june)) "is? - wrong month check")
  (is (is? july-15-2000 :july) "is? - wrong month check")
  (is (not (is? july-15-2000 :august)) "is? - wrong month check")
  (is (not (is? july-15-2000 :september)) "is? - wrong month check")
  (is (not (is? july-15-2000 :october)) "is? - wrong month check")
  (is (not (is? july-15-2000 :november)) "is? - wrong month check")
  (is (not (is? july-15-2000 :december)) "is? - wrong month check")
  (is (is? (following :year) :new-year) "is? - wrong new year check"))

(deftest after?-tests
  (is (after? july-15-2000 (subtract july-15-2000 1 :hour)))
  (is (after? july-15-2000 (subtract july-15-2000 1 :day))))

(deftest before?-tests
  (is (before? july-15-2000 (add july-15-2000 1 :hour)))
  (is (before? july-15-2000 (add july-15-2000 1 :day))))

(deftest between?-tests
  (is (between? july-15-2000
                (subtract july-15-2000 1 :hour)
                (add july-15-2000 1 :hour)))
  (is (between? july-15-2000
                (subtract july-15-2000 1 :day)
                (add july-15-2000 1 :day))))

(deftest days-in-month-tests
  (is (= (days-in-month july-15-2000) 31))
  (is (= (days-in-month (february july-15-2000)) 29)))

(deftest names-tests
  (binding [*locale* (Locale. "en" "US")]
    (let [ns-m (names :months :long)
          ns-m-short (names :months :short)
          ns-w (names :week-days :long)
          ns-w-short (names :week-days :short)]
      (is (= '("January" "February" "March" "April" "May" "June"
               "July" "August" "September" "October" "November" "December") ns-m)
          "names - wrong month long names")
      (is (= '("Sunday" "Monday" "Tuesday" "Wednesday"
               "Thursday" "Friday" "Saturday") ns-w) "names - wrong week days long names")
      (is (= '("Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec") ns-m-short)
          "names - wrong month short names")
      (is (= '("Sun" "Mon" "Tue" "Wed" "Thu" "Fri" "Sat") ns-w-short)
          "names - wrong week days short names")))
  (binding [*locale* (Locale. "pt" "BR")]
    (let [ns-m (names :months :long)
          ns-m-short (names :months :short)
          ns-w (names :week-days :long)
          ns-w-short (names :week-days :short)]
      (is (some #{"Julho"} ns-m) "names - wrong months long names")
      (is (some #{"Jan"} ns-m-short) "names - wrong months short names")
      (is (some #{"Segunda-feira"} ns-w) "names - wrong week days long names")
      (is (some #{"Dom"} ns-w-short) "names - wrong week days short names"))))

(deftest months-tests
  (let [ms (months july-15-2000)]
    (is (= (count ms) 12) "months - wrong count")
    (is (= (month (nth ms 0)) 0) "months - month not present")
    (is (= (month (nth ms 1)) 1) "months - month not present")
    (is (= (month (nth ms 2)) 2) "months - month not present")
    (is (= (month (nth ms 3)) 3) "months - month not present")
    (is (= (month (nth ms 4)) 4) "months - month not present")
    (is (= (month (nth ms 5)) 5) "months - month not present")
    (is (= (month (nth ms 6)) 6) "months - month not present")
    (is (= (month (nth ms 7)) 7) "months - month not present")
    (is (= (month (nth ms 8)) 8) "months - month not present")
    (is (= (month (nth ms 9)) 9) "months - month not present")
    (is (= (month (nth ms 10)) 10) "months - month not present")
    (is (= (month (nth ms 11)) 11) "months - month not present")))

(deftest specific-months-tests
  (is (= (month (january)) 0) "january - wrong month")
  (is (= (month (february)) 1) "february - wrong month")
  (is (= (month (march)) 2) "march - wrong month")
  (is (= (month (april)) 3) "april - wrong month")
  (is (= (month (may)) 4) "may - wrong month")
  (is (= (month (june)) 5) "june - wrong month")
  (is (= (month (july)) 6) "july - wrong month")
  (is (= (month (august)) 7) "august - wrong month")
  (is (= (month (september)) 8) "september - wrong month")
  (is (= (month (october)) 9) "october - wrong month")
  (is (= (month (november)) 10) "november - wrong month")
  (is (= (month (december)) 11) "december - wrong month"))

(deftest weeks-tests
  (let [ws (weeks july-15-2000)]
    (is (= (count ws) 5) "weeks - wrong count")))

(deftest week-days-seq-tests
  (let [ss (sundays july-15-2000)
        ms (mondays july-15-2000)
        tus (tuesdays july-15-2000)
        ws (wednesdays july-15-2000)
        ts (thursdays july-15-2000)
        fs (fridays july-15-2000)
        sas (saturdays july-15-2000)]
    (is (every? #(= (week-day %) 1) ss) "sundays - wrong week day in seq")
    (is (every? #(= (week-day %) 2) ms) "mondays - wrong week day in seq")
    (is (every? #(= (week-day %) 3) tus) "tuesdays - wrong week day in seq")
    (is (every? #(= (week-day %) 4) ws) "wednesdays - wrong week day in seq")
    (is (every? #(= (week-day %) 5) ts) "thursdays - wrong week day in seq")
    (is (every? #(= (week-day %) 6) fs) "fridays - wrong week day in seq")
    (is (every? #(= (week-day %) 7) sas) "saturdays - wrong week day in seq")))

(deftest week-days-tests
  (is (= (week-day (sunday)) 1) "sunday - wrong week day")
  (is (= (week-day (monday)) 2) "monday - wrong week day")
  (is (= (week-day (tuesday)) 3) "tuesday - wrong week day")
  (is (= (week-day (wednesday)) 4) "wednesday - wrong week day")
  (is (= (week-day (thursday)) 5) "thursday - wrong week day")
  (is (= (week-day (friday)) 6) "friday - wrong week day")
  (is (= (week-day (saturday)) 7) "saturday - wrong week day")
  (is (= (day (yesterday)) (dec (day (today)))) "yesterday - wrong day")
  (is (= (day (tomorrow)) (inc (day (today)))) "yesterday - wrong day"))

(deftest parse-date-tests
  (let [format "yyyy/MM/dd HH:mm:ss"
        dt (parse-date "2000/07/15 10:30:20" format)]
    (is (= (year dt) (year july-15-2000)) "parse-date - wrong year")
    (is (= (month dt) (month july-15-2000)) "parse-date - wrong month")
    (is (= (week-day dt) (week-day july-15-2000)) "parse-date - wrong week")
    (is (= (day dt) (day july-15-2000)) "parse-date - wrong day")
    (is (= (hours dt) (hours july-15-2000)) "parse-date - wrong hour")
    (is (= (minutes dt) (minutes july-15-2000)) "parse-date - wrong minute")
    (is (= (seconds dt) (seconds july-15-2000)) "parse-date - wrong second")))

(deftest format-date-tests
  (let [format "yyyy/MM/dd HH:mm:ss"
        dt-str (format-date july-15-2000 format)]
    (is (= dt-str "2000/07/15 10:30:20") "format-date - wrong date")))