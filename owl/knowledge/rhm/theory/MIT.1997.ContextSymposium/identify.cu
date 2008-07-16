# 11:50 am 8/30/97
#==========#
# identify #
#==========#
knowledge := man identify existent
action ise \
	sense,
	perceive,
	partition,
	measure,
	choose,
	define,
	remember
sense ise see,hear,touch,taste,smell
#perceive ise sense,remember
identify  is action with a_event = \
			"sense:1;
			perceive:2;
			partition:3;
			measure:4;
			choose:5;
			define:6"
name	 is  word

sensation	:= man sense     entity
percept		:= man perceive  entity
unit-hierarchy	:= man partition percept  # view
characteristic	:= man measure   entity
name		:= man choose    symbol
definition	:= man define    name

name means unit

truth := man identify fact-of-reality
