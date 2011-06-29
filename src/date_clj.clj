(ns ^{:author "Islon Scherer"}
  date-clj
  "A date.js like library aimed to be simple and fun."
  (:import (java.util Date GregorianCalendar Calendar Locale)
           (java.text SimpleDateFormat ParseException)))

(def ^{:private true} days-of-the-week
  [:sunday :monday :tuesday :wednesday :thursday :friday :saturday])

(def ^{:private true} months-of-the-year
  [:january :february :march :april :may :june
   :july :august :september :october :november :december])

(def *locale* (Locale/getDefault))

(declare date following add)

;; helpers

(defn- as-calendar
  "Returns a calendar for this date."
  [date]
  (let [cal (Calendar/getInstance)]
    (.setTime cal date)
    cal))

;; private functions

(defn- and-fn
  "The and macro as a function, more or less."
  [& args]
  (every? boolean args))

(defn- keyword2calendar-field
  "Returns a Calendar field for a keyword."
  [key]
  (cond (or (= key :milliseconds) (= key :millisecond))
        (Calendar/MILLISECOND)
        (or (= key :seconds) (= key :second))
        (Calendar/SECOND)
        (or (= key :minutes) (= key :minute))
        (Calendar/MINUTE)
        (or (= key :hours) (= key :hour))
        (Calendar/HOUR_OF_DAY)
        (or (= key :days) (= key :day))
        (Calendar/DATE)
        (or (= key :week) (= key :weeks))
        (Calendar/WEEK_OF_MONTH)
        (or (= key :months) (= key :month))
        (Calendar/MONTH)
        (or (= key :years) (= key :year))
        (Calendar/YEAR)))

(defn- keyword2day-of-the-week
  "Returns the day of the week (number) for this keyword"
  [key]
  (cond (= key :sunday) 1
        (= key :monday) 2
        (= key :tuesday) 3
        (= key :wednesday) 4
        (= key :thursday) 5
        (= key :friday)  6
        (= key :saturday) 7))

(defn- keyword2month
  "Returns the month (number) for this keyword"
  [key]
  (cond (= key :january) 0
        (= key :february) 1
        (= key :march) 2
        (= key :april) 3
        (= key :may) 4
        (= key :june) 5
        (= key :july) 6
        (= key :august) 7
        (= key :september) 8
        (= key :october) 9
        (= key :november) 10
        (= key :december) 11))

(defn- week-day-checker
  "Checks if the week day of the date is equals the argument."
  [date week-day]
  (= (.get (as-calendar date) Calendar/DAY_OF_WEEK) (keyword2day-of-the-week week-day)))

(defn- month-checker
  "Checks if the month of the date is equals the argument."
  [date month]
  (= (.get (as-calendar date) Calendar/MONTH) (keyword2month month)))

(defn- day-checker
  "Checks if the day of the date is equals the argument."
  [date day]
  (= (.get (as-calendar date) Calendar/DATE) day))

(defn- year-checker
  "Checks if the year of the date is equals the argument."
  [date year]
  (= (.get (as-calendar date) Calendar/YEAR) year))

(defn- leap-year-checker
  "Checks if the year of the date is a leap year."
  [date]
  (let [cal (as-calendar date)]
    (.isLeapYear cal (.get cal Calendar/YEAR))))

(defn- new-year-checker
  "Checks if the date is a new year."
  [date]
  (let [cal (as-calendar date)
        now (Calendar/getInstance)]
    (not= (.get cal Calendar/YEAR) (.get now Calendar/YEAR))))

(defn- weekend-checker
  "Checks if the date is a weekend."
  [date]
  (let [week-day (.get (as-calendar date) Calendar/DAY_OF_WEEK)]
    (boolean (some #{week-day} [1 7]))))

(defn- date-checker
  "Checks a date against an argument."
  [date arg]
  (cond (some #{arg} days-of-the-week)
        (week-day-checker date arg)
        (some #{arg} months-of-the-year)
        (month-checker date arg)
        (and (number? arg) (<= arg 31) (> arg 0))
        (day-checker date arg)
        (and (number? arg))
        (year-checker date arg)
        (= arg :leap-year)
        (leap-year-checker date)
        (= arg :new-year)
        (new-year-checker date)
        (= arg :week-day)
        (not (weekend-checker date))
        (= arg :weekend)
        (weekend-checker date)))

(defn- month-from-arg
  "Gets the month from a number or keyword argument."
  [arg]
  (if (number? arg)
    arg
    (keyword2month arg)))

(defn- week-day-from-arg
  "Gets the week day from a number or keyword argument."
  [arg]
  (if (number? arg)
    arg
    (keyword2day-of-the-week arg)))

(defn- next-week-day
  "Returns the next week day from the date."
  [date week-day]
  (let [cal (as-calendar date)
        cwday (.get cal Calendar/DAY_OF_WEEK)
        days (if (> week-day cwday)
               (- week-day cwday)
               (- 7 (- cwday week-day)))]
    (add date days :days)))

(defn- next-month
  "Returns the next month from the date."
  [date month]
  (let [cal (as-calendar date)
        mon (keyword2month month)
        cmon (.get cal Calendar/MONTH)
        months (if (> mon cmon)
                 (- mon cmon)
                 (- 12 (- cmon mon)))]
    (add date months :months)))

(defn- set-date-from-map
  "Sets a java.util.Date from a map of parameters and using defaults."
  [date m]
  (let [day (if (:day m) (:day m) 1)
        month (if (:month m) (month-from-arg (:month m)) 0)
        year (if (:year m) (:year m) (.get (as-calendar date) Calendar/YEAR))
        week-day (if (:week-day m) (week-day-from-arg (:week-day m)) nil)
        hour (if (:hour m) (:hour m) 0)
        minute (if (:minute m) (:minute m) 0)
        second (if (:second m) (:second m) 0)
        millisecond (if (:millisecond m) (:millisecond m) 0)
        cal (Calendar/getInstance)]
    (.setTime cal date)
    (.set cal Calendar/YEAR year)
    (.set cal Calendar/MONTH month)
    (.set cal Calendar/DATE day)
    (.set cal Calendar/HOUR_OF_DAY hour)
    (.set cal Calendar/MINUTE minute)
    (.set cal Calendar/SECOND second)
    (.set cal Calendar/MILLISECOND millisecond)
    (if week-day
      (next-week-day (.getTime cal) week-day)
      (.getTime cal))))

(defn- set-date-from-map-no-defaults
  "Sets a java.util.Date from a map of parameters without defaults."
  [date m]
  (let [week-day (if (:week-day m) (week-day-from-arg (:week-day m)) nil)
        cal (as-calendar date)]
    (when (:day m)
      (.set cal Calendar/DATE (:day m)))
    (when (:month m)
      (.set cal Calendar/MONTH (month-from-arg (:month m))))
    (when (:year m)
      (.set cal Calendar/YEAR (:year m)))
    (when (:hour m)
      (.set cal Calendar/HOUR_OF_DAY (:hour m)))
    (when (:minute m)
      (.set cal Calendar/MINUTE (:minute m)))
    (when (:second m)
      (.set cal Calendar/SECOND (:second m)))
    (when (:millisecond m)
      (.set cal Calendar/MILLISECOND (:millisecond m)))
    (if week-day
      (next-week-day (.getTime cal) week-day)
      (.getTime cal))))

(defn- week-days
  "Get all the specified week-day of the month."
  [date week-day]
  (let [cal (as-calendar date)
        month (.get cal Calendar/MONTH)]
    (.set cal Calendar/DATE 1)
    (loop [wdays []]
      (if (not= month (.get cal Calendar/MONTH))
        wdays
        (let [dt (.getTime cal)
              right-day (= week-day (.get cal Calendar/DAY_OF_WEEK))]
          (.add cal Calendar/DATE 1)
          (recur (if right-day (conj wdays dt) wdays)))))))

;; public functions

(defn date
  "Initialize a new date (defaults to now).
Possible parameters are a timestamp or a list specifying the parts.
List paramenters are :millisecond, :second, :hour, :day, :week-day, :month, :year.
:month and :week-day accept 2 forms: a number or a keyword like :april, :friday, etc.
Eg.: (date) -> now
     (date 1234567891231)
     (date :day 1 :month 10 :year 1945) -> september 1st 1945
     (date :week-day :sunday :month :april) -> the first sunday of april of the current year
     (date :hour 16 :minute 12 :second 45) -> january 1st of the current year 16:12:45.
P.S.: In the list version some parameters have a default if not supplied:
day = 1, month = 0 (january), year = current year, hour = 0, minute = 0, second = 0,
millisecond = 0.
P.S.2: Month starts with 0 (january) and week-day starts with 1 (sunday) unless used some
specific locale."
  ([] (date (System/currentTimeMillis)))
  ([timestamp] (Date. (long timestamp)))
  ([p1 p2 & args]
     (set-date-from-map (Date.) (apply hash-map p1 p2 args))))

(defn today
  "The current day and current hour."
  []
  (date))

(defn set-date
  "Set the date based on the list of parameters.
Paramenters are :millisecond, :second, :hour, :day, :week-day, :month, :year.
:month and :week-day accept 2 forms: a number or a keyword like :april, :friday, etc.
Eg.: (set-date (today) :day 1 :month 10 :year 1945)
     (set-date (today) :week-day :sunday :month :april)
     (set-date (following :april) :hour 16 :minute 12 :second 45).
P.S.: Month starts with 0 (january) and week-day starts with 1 (sunday) unless used some
specific locale."
  [date & {:as args}]
  (set-date-from-map-no-defaults date args))

(defn add
  "Add some time to this date.
The parameters are in the form number :timescale
Eg.: (-> (following :month) (add 1 :day 10 :years))."
  [date & args]
  (when (odd? (count args))
    (throw (RuntimeException. "Paramenters cannot be odd")))
  (let [cal (as-calendar date)
        parts (partition 2 args)]
    (doseq [part parts]
      (.add cal (keyword2calendar-field (second part)) (first part)))
    (.getTime cal)))

(defn from-now
  "An specified time from now.
Eg.: (from-now 1 :day 5 :months 10 :years)."
  [& args]
  (apply add (today) args))

(defn subtract
  "Like add, but subtracts the amount specified from the date.
Eg.: (-> (february) (subtract 3 :days 1 :year))"
  [date & args]
  (apply add date (map #(if (number? %) (- %) %) args)))

(defn back
  "Like from-now but to the past.
Eg.: (back 1 :day 5 :months 10 :years)."
  [& args]
  (apply subtract (today) args))

(defn following
  "Gets a date in the future according to the argument.
Possible arguments are: :millisecond, :second, :minute, :hour, :day, :week, :month, :year,
:sunday .. :saturday, :january .. :december.
Default date: now."
  ([time] (following (today) time))
  ([date time]
     (cond (some #{time} days-of-the-week)
           (next-week-day date (week-day-from-arg time))
           (some #{time} months-of-the-year)
           (next-month date time)
           :else
           (let [cal (as-calendar date)]
             (cond (= time :millisecond)
                   (.add cal (Calendar/MILLISECOND) 1)
                   (= time :second)
                   (.add cal (Calendar/SECOND) 1)
                   (= time :minute)
                   (.add cal (Calendar/MINUTE) 1)
                   (= time :hour)
                   (.add cal (Calendar/HOUR) 1)
                   (= time :day)
                   (.add cal (Calendar/DATE) 1)
                   (= time :week)
                   (.add cal (Calendar/WEEK_OF_MONTH) 1)
                   (= time :month)
                   (.add cal (Calendar/MONTH) 1)
                   (= time :year)
                   (.add cal (Calendar/YEAR) 1))
             (.getTime cal)))))

(defn is?
  "Check the date against the arguments.
Possible arguments are: :sunday .. :saturday, :january .. :december, 1 .. 31 (day check),
< 1 and > 32 (year check), :leap-year, :new-year, :week-day, :weekend.
Eg.: (-> (today) (is? :sunday :january 1945))
     (-> (following :year) (is? :new-year :leap-year :weekend))."
  [date & args]
  (apply and-fn (map #(date-checker date %) args)))

(defn was?
  "An alias to is?"
  [date & args]
  (apply is? date args))

(defn will-be?
  "An alias to is?"
  [date & args]
  (apply is? date args))

(defn after?
  "Returns true if date1 came after date2."
  [date1 date2]
  (> (.getTime date1) (.getTime date2)))

(defn before?
  "Returns true if date1 came before date2."
  [date1 date2]
  (< (.getTime date1) (.getTime date2)))

(defn between?
  "Returns true if date is between start and end."
  [date start end]
  (and (after? date start)
       (before? date end)))

(defn days-in-month
  "Returns the number of days in the date's month."
  [date]
  (.getActualMaximum (as-calendar date) Calendar/DAY_OF_MONTH))

;; time parts

(defn milliseconds
  "The milliseconds part of this date."
  [date]
  (.get (as-calendar date) Calendar/MILLISECOND))

(defn seconds
  "The seconds part of this date."
  [date]
  (.get (as-calendar date) Calendar/SECOND))

(defn minutes
  "The minutes part of this date."
  [date]
  (.get (as-calendar date) Calendar/MINUTE))

(defn hours
  "The hours part of this date."
  [date]
  (.get (as-calendar date) Calendar/HOUR_OF_DAY))

(defn day
  "The day part of this date."
  [date]
  (.get (as-calendar date) Calendar/DATE))

(defn week-day
  "The week day part of this date. 0 = sunday, 7 = saturday."
  [date]
  (.get (as-calendar date) Calendar/DAY_OF_WEEK))

(defn month
  "The month part of this date. january = 0."
  [date]
  (.get (as-calendar date) Calendar/MONTH))

(defn year
  "The year part of this date"
  [date]
  (.get (as-calendar date) Calendar/YEAR))

;; helpers

(defn months
  "A sequence of all the months of the date's year."
  [date]
  (let [cal (as-calendar date)]
    (.set cal Calendar/MONTH 0)
    (.set cal Calendar/DATE 1)
    (.set cal Calendar/HOUR_OF_DAY 0)
    (.set cal Calendar/MINUTE 0)
    (.set cal Calendar/SECOND 0)
    (.set cal Calendar/MILLISECOND 0)
    (let [dt (.getTime cal)]
      [dt
       (add dt 1 :month)
       (add dt 2 :months)
       (add dt 3 :months)
       (add dt 4 :months)
       (add dt 5 :months)
       (add dt 6 :months)
       (add dt 7 :months)
       (add dt 8 :months)
       (add dt 9 :months)
       (add dt 10 :months)
       (add dt 11 :months)])))

(defn january
  "Returns january of the date's year (default: the current year)."
  ([] (january (today)))
  ([date] (-> date months first)))

(defn february
  "Returns february of the current year."
  ([] (february (today)))
  ([date] (-> date months second)))

(defn march
  "Returns march of the current year."
  ([] (march (today)))
  ([date] (-> date months (nth 2))))

(defn april
  "Returns april of the current year."
  ([] (april (today)))
  ([date] (-> date months (nth 3))))

(defn may
  "Returns may of the current year."
  ([] (may (today)))
  ([date] (-> date months (nth 4))))

(defn june
  "Returns june of the current year."
  ([] (june (today)))
  ([date] (-> date months (nth 5))))

(defn july
  "Returns july of the current year."
  ([] (july (today)))
  ([date] (-> date months (nth 6))))

(defn august
  "Returns august of the current year."
  ([] (august (today)))
  ([date] (-> date months (nth 7))))

(defn september
  "Returns september of the current year."
  ([] (september (today)))
  ([date] (-> date months (nth 8))))

(defn october
  "Returns october of the current year."
  ([] (october (today)))
  ([date] (-> date months (nth 9))))

(defn november
  "Returns november of the current year."
  ([] (november (today)))
  ([date] (-> date months (nth 10))))

(defn december
  "Returns december of the current year."
  ([] (december (today)))
  ([date] (-> date months (nth 11))))

(defn weeks
  "A sequence of all the weeks of the date's month."
  [date]
  (let [cal (as-calendar date)]
    (.set cal Calendar/DATE 1)
    (let [dt (.getTime cal)]
      [dt
       (add dt 1 :week)
       (add dt 2 :weeks)
       (add dt 3 :weeks)
       (add dt 4 :weeks)])))

(defn sundays
  "A sequence of all sundays of the date's month."
  [date]
  (week-days date 1))

(defn mondays
  "A sequence of all mondays of the date's month."
  [date]
  (week-days date 2))

(defn tuesdays
  "A sequence of all tuesdays of the date's month."
  [date]
  (week-days date 3))

(defn wednesdays
  "A sequence of all wednesdays of the date's month."
  [date]
  (week-days date 4))

(defn thursdays
  "A sequence of all thursdays of the date's month."
  [date]
  (week-days date 5))

(defn fridays
  "A sequence of all fridays of the date's month."
  [date]
  (week-days date 6))

(defn saturdays
  "A sequence of all saturdays of the date's month."
  [date]
  (week-days date 7))

(defn day-of-current-week
  "Returns a date representing the specified day of the week (:sunday .. :saturday)."
  [day]
  (let [cal (Calendar/getInstance)]
    (.set cal Calendar/HOUR_OF_DAY 0)
    (.set cal Calendar/MINUTE 0)
    (.set cal Calendar/SECOND 0)
    (.set cal Calendar/DAY_OF_WEEK day)
    (.getTime cal)))

(defn sunday
  "Returns sunday of this week."
  []
  (day-of-current-week 1))

(defn monday
  "Returns monday of this week."
  []
  (day-of-current-week 2))

(defn tuesday
  "Returns tuesday of this week."
  []
  (day-of-current-week 3))

(defn wednesday
  "Returns wednesday of this week."
  []
  (day-of-current-week 4))

(defn thursday
  "Returns thursday of this week."
  []
  (day-of-current-week 5))

(defn friday
  "Returns friday of this week."
  []
  (day-of-current-week 6))

(defn saturday
  "Returns saturday of this week."
  []
  (day-of-current-week 7))

(defn yesterday
  "Returns yesterday."
  []
  (let [cal (Calendar/getInstance)]
    (.add cal Calendar/DATE -1)
    (.getTime cal)))

(defn tomorrow
  "Returns tomorrow."
  []
  (let [cal (Calendar/getInstance)]
    (.add cal Calendar/DATE 1)
    (.getTime cal)))

;; parsing and formatting

(defn parse-date
  "Parse a string into a date using the first valid format of the supplied formats.
See java.text.SimpleDateFormat for formats."
  [str & formats]
  (let [s (map (fn [format]
                 (try
                   (let [sdf (SimpleDateFormat. format *locale*)]
                     (.parse sdf str))
                   (catch ParseException e
                     nil)))
               formats)]
    (first (remove nil? s))))

(defn format-date
  "Format a date into a string using the supplied format.
See java.text.SimpleDateFormat for formats."
  [date format]
  (let [sdf (SimpleDateFormat. format *locale*)]
    (.format sdf date)))

(defn names
  "Get a locale specific list of the names of the months or week days
in short or long format (default: long).
Eg.: (names :months :short) -> ('Jan' .. 'Dec')
     (binding [*locale* (Locale/GERMAN)]
       (names :week-days)) -> ('Sonntag' .. 'Samstag').
P.S.: The order of week-days is always sunday to saturday."
  ([field] (names field :long))
  ([field style]
     (let [real-style (cond (and (= style :long) (= field :months))
                            "MMMM"
                            (and (= style :long) (= field :week-days))
                            "EEEE"
                            (= field :months)
                            "MMM"
                            :else
                            "EEE")]
       (if (= field :months)
         (doall (map #(format-date % real-style) [(january) (february) (march)
                                           (april) (may) (june) (july)
                                           (august) (september) (october)
                                           (november) (december)]))
         (doall (map #(format-date % real-style) [(sunday) (monday) (tuesday) (wednesday)
                                           (thursday) (friday) (saturday)]))))))