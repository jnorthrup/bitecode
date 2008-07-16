REBOL [
	Title: "subject verb object grammar"
	Author: "Richard H. McCullough"
	Email: rhm@cdepot.net
	Date: 1999-May-28
	File: %verb1.r
	Purpose: {
		Experiment with parse function.
	}
]

;=======================================================
verblist: [
	"isa" "ise"
	"is"
	"has" "do" "isin"
]

;do [
;=======================================================
grammar: [
	m_svo
]

m_svo: [
	(print "begin m_svo")
		m_subject
		m_verb
		m_object
	(print "end m_svo")
]
m_subject: [
	[
	copy subject to "isin"	|
	copy subject to "isa"	|
	copy subject to "ise"	|
	copy subject to "is"	|
	copy subject to "has"	|
	copy subject to "do"
	]
	;subject: m_phrase
	(print ["subject:" subject])
]
m_verb: [
	[
	copy verb thru "isin"	|
	copy verb thru "isa"	|
	copy verb thru "ise"	|
	copy verb thru "is"	|
	copy verb thru "has"	|
	copy verb thru "do"
	]
	;any verblist
	(print ["verb:" verb])
]
m_object: [
	;copy object to end
	object: m_phrase
	(print ["object:" object])
]
m_phrase: [
	;some m_word
	m_word
]

;=======================================================
m_word: [some wordchar]

letters:  charset "abcdefghijklmnopqrstuvwxyz"
digits:   charset "0123456789"
wordchar: charset "abcdefghijklmnopqrstuvwxyz0123456789"
;=======================================================
;] ; end do

;=======================================================
test: [
	"concept isa genus"
	"concept ise unit"
	"the big dog do bark loudly"
	"Dick McCullough isin the phonebook"
]

foreach sentence test [
	print ["sentence:" sentence]
	;foreach mverb verblist [
		;m_verb: mverb
		parse sentence grammar
		;parse/all sentence grammar
	;]
]
