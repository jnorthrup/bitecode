REBOL [
	Title:	"KR grammar"
	Author:	"Richard H. McCullough"
	Email:	rhm@cdepot.net
	Date:	1999-May-28
	File:	%grammar.r
	Purpose:{
		Define (simplified) KR grammar for REBOL parse function.
	}
]

grammar: [
	(print "begin parse")
	some [proposition]
	(print "end parse")
]

quantifier: [
	a some the
	no
	either all
]
verb: [
	isa ise ism isc isd isi
	is
	has haspart
	do changes
	isin
]
preposition: [
	at
	of with
	from to
]
conjunction: [
	"," ";"
	and or not
	means causes
	every done
	if then else fi
	iff
	implies
	supports
]


proposition: [
	'at context "{" sentence "}" |
	'at context |
	sentence
]
sentence: [
	clause conjunction sentence |
	clause
]

clause: [
	assignment |
	command |
	question |
	statement |
	'every sentence 'do sentence 'done |
	'if sentence 'then sentence 'else sentence 'fi |
	'either sentence 'or sentence |
	'all sentence 'and sentence
]

assignment: [
	variable nvop value
]

command: [
	'! cmdname arglist prepphrase |
	hierarchy-block |
	relation-block
]

hierarchy-block: [
	'!begin 'hierarchy hname
	hphrase
	'!end 'hierarchy hname
	]
relation-block: [
	'!begin 'relation rname
	 rphrase
	 '!end 'relation rname
	]

statement: [
	"x" "isa" "y" |
	subject verb object prepphrase |
	subject verb object |
	product ":=" statement
]

question: [
	statement "?" |
	statement	; with internal "?"
]


word: "xyz"
hname: word
rname: word
nvop: "=" | "+=" | "-="

phrase: word word
nvphrase: phrase nvop phrase
prepphrase: preposition nvphrase
hphrase: "/" phrase
rphrase: phrase ";" phrase	; infon

context: nvphrase
product: phrase
subject: phrase
object: [
	nvphrase
	nvphrase "(" phrase ")"
]
