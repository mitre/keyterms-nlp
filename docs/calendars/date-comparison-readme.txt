NanoLP Calendars Module - README
--------

POC: 				Nick Becker (nbecker@mitre.org)
Last Updated: 	7 Mar 2017

--------

Basic Usage
----
1. Comparing two date strings

To compare two date strings, simply pass the strings to the static method DateCompare.compare(): 

	double score = DateCompare.compare("1971-01-31", "January 1970"); 

The order of the parameters does not matter. This comparison method currently works best with Gregorian dates, but also accepts dates from the Hijri calendar. The following date formats (separated into "Format Families") are currently accepted: 

- Format Family 1 - 
20170113

- Format Family 2 - 
2017-01-13
2017/01/13
2017.01.13
2017 01 13

- Format Family 3 - 
01-13-2017
01/13/2017
01.13.2017
01 13 2017

- Format Family 4 - 
January 13, 2017
Jan 13, 2017

- Format Family 5 - 
13 January 2017
13 January, 2017
13 Jan. 2017
13 Jan 2017
13 Jan., 2017
13 Jan, 2017

- Format Family 6 - 
01-13
01/03
01.03
01 13
13-01
13/01
13.01
13 01

- Format Family 7 - 
January 13
Jan. 13
Jan 13

- Format Family 8 - 
13 January
13 Jan.
13 Jan

- Format Family 9 - 
2017-01
2017/01
2017.01
2017 01

- Format Family 10 - 
01-2017
01/2017
01.2017
01 2017

- Format Family 11 - 
January 2017
January, 2017
Jan. 2017
Jan 2017
Jan., 2017
Jan, 2017
