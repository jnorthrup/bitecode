# 5:30 pm 1998/2/10
#debug=EVENT

# Prediger example
# ICCS'97 page 332
# interaction with Knowledge Explorer 1.5

# general context
#================
at view = tabula_rasa
existent ise* ?
# see the file "general.txt"

# leisure context
#================
at view = tabula_rasa  at view = v_leisure
hfocus = [person, leisure_activity, book, event, r_leisure]
action ise leisure_activity, evaluate
object ise importance, book
person do leisure_activity
importance := person do evaluate leisure_activity
importance has vtype=integer
leisure_activity ise \
	sports_activity,
	read_book,
	home_hobby,
	visit_event
sports_activity ise \
	sail,surf,ski,
	walk,hike,jog,track_and_field
book ise specific,general,entertaining
home_hobby ise \
	house_and_garden,
	kitchen,
	art_and_music,
	family_and_social
event ise \
	culture,
	sports,
	politics

r_leisure is relation with \
	format = "person:1; importance:2; sports_activity:3; book:4; home_hobby:5; event:6",
	meaning ="$2 := $1 do evaluate leisure_activity;
		$1 do $3;
		$1 do read_book $4;
		$1 do $5;
		$1 do visit_event $6"
!read leisure.rel with cname=r_leisure

# stereotype view
#================
# NOTE: since stereotypes are not disjoint,
# this view is a concept-lattice, not a concept-hierarchy
at view = person/stereotype
hfocus = [person, r_stereotype]
person ise \
	family_man,
	sports_fan,
	intellectual,
	practitioner
family_man   is person with "family_and_social"
sports_fan   is person with "visit_event sports"
intellectual is person with "visit_event politics or culture","read_book special"
practitioner is person with "read_book entertaining or not art_and_music",
				"visit_event sports"

r_stereotype is relation with \
	format = "person:1; person:2",
	meaning = "$1 isd $2"
!read stereo.rel with cname=r_stereotype

#===========#
# questions #
#===========#
# what books are read by the people who visit sporting events?
? do visit_event sports; $ do read_book ?

# what books are read by the people who visit cultural events?
? do visit_event culture; $ do read_book ?

# how do "intellectual"s evaluate leisure_activity?
? isa intellectual; $ do evaluate ?

# how do "sports_fan"s evaluate leisure_activity?
? isa sports_fan; $ do evaluate ?

# how do "family_man"s evaluate leisure_activity?
? isa family_man; $ do evaluate ?
