# date-clj

A date library for clojure loosely based on [date.js](http://www.datejs.com/) javascript date library.<br/>
date-clj aims to be simple and fun and not a complete and exaustive solution like [joda-time](http://joda-time.sourceforge.net/).
All functions in date-clj are immutable.

## Installation

Add date-clj to you project.clj dependencies:

> [date-clj "1.0.0"]

Use it on your namespace:

> (ns my.namespace
>   (:use [date-clj]))

## Usage

The library always returns a java.util.Date in functions that should return some date or time and every<br/>
function parameter called 'date' receives a java.util.Date too.

### Date creation functions

> ; timestamp creation - January 02 1970<br/>
> (date 100000000)

> ; 2000's Christmas<br/>
> (date :day 25 :month 11 :year 2000)

> ; the first sunday of april of the current year, 1:45 PM<br/>
> (date :week-day :sunday :month :april :hour 13 :minute 45)

> ; the current date set to december of 1900 (set-date is a immutable function, it returns a new date)<br/>
> (-> (today) (set-date :month :december :year 1900))

### Date math functions

> ; christmas of this year plus 1 day, 3 months, 5 years and 10 hours<br/>
> (def xmas (date :day 25 :month :december))<br/>
> (-> xmas (add 1 :day 3 :months 5 :years 10 :hours))

> ; 2 weeks and 50 minutes before xmas<br/>
> (-> xmas (subtract 2 :weeks 50 :minutes))

> ; 1 year and 5 days from now<br/>
> (from-now 1 :year 5 :days)

> ; 3 hours and 200 milliseconds ago<br/>
> (back 3 :hours 200 :milliseconds)

### Relative math functions

> ; next friday from now<br/>
> (following :friday)

> ; the day after xmas<br/>
> (-> xmas (following :day))

> ; next january<br/>
> (following :january)

### Date test functions

> ; is it friday 13? beware!<br/>
> (-> (today) (is? :friday 13))

> ; was 3 years and 5 months ago may 2005?<br/>
> (-> (back 3 :years 5 :months) (was? :may 2005))

> ; will next year be a leap year?<br/>
> (-> (following :year) (will-be? :leap-year))

> ; weekend already?<br/>
> (-> (today) (is? :weekend))

### Date comparison functions

> ; is xmas before today?<br/>
> (-> xmas (before? (today)))

> ; always true<br/>
> (-> (tomorrow) (after? (yesterday)))

> ; always true<br/>
> (-> (today) (between? (yesterday) (tomorrow)))

### Date part functions

> ; 25<br/>
> (day xmas)

> ; 11 (months start in 0)<br/>
> (month xmas)

> ; a list in the form (day-of-the-week hour minute second) for the current time<br/>
> (map #(% (today)) [week-day hours minutes seconds])

### Specific date functions

> ; a date representing february of the current year<br/>
> (february)

> ; may of the following year<br/>
> (-> (following :year) may)

> ; common dates<br/>
> (today)<br/>
> (tomorrow)<br/>
> (yesterday)

> ; the first sunday of april<br/>
> (-> (april) sundays first)<br/>
> ; next month's last friday<br/>
> (-> (following :month) fridays last)

> ; this week's monday<br/>
> (monday)

### Parsing, formatting and displaying functions

date-clj uses java.text.SimpleDateFormat for parsing and formatting.

> ; parsing the string into a date using the first matching format<br/>
> (parse-date "2035.06.23" "yyyy/MM/dd" "yyyy.MM.dd")

> ; shows "12/24 is christmas eve"<br/>
> (-> xmas (subtract 1 :day) (format-date "MM/dd 'is christmas eve'"))

> ; prints the current month's full name in  brazilian portuguese<br/>
> (binding \[\*locale\* (Locale. "pt" "BR")\] (format-date (today) "MMMM"))

> ; a seq of the week day names in german<br/>
> (binding \[\*locale\* (Locale/GERMAN)\] (names :week-days))

> ; a seq of the short month names in the default locale<br/>
> (names :months :short)

## License

Copyright © 2011 Islon Scherer

Distributed under the Eclipse Public License, the same as Clojure.
