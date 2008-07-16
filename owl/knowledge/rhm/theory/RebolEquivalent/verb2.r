REBOL [
	Title: "subject verb object grammar"
	Author: "Richard H. McCullough"
	Email: rhm@cdepot.net
	Date: 1999-May-29
	File: %verb2.r
	Purpose: {
		Parse sentence into wordlist;
		match verblist against wordlist.
	}
]

verblist: [
	"isa" "ise"
	"is"
	"has" "do" "isin"
]

test: [
	"concept isa genus"
	"concept ise unit"
	"the big dog do bark loudly"
	"Dick McCullough isin the phonebook"
]

foreach sentence test [
    print ["sentence:" sentence]
    wordlist: parse sentence none
    N: length? wordlist
    for i 1 N 1 [
	wd: pick wordlist i
	foreach verb verblist [
	    if wd = verb [
		subject: make block! 0
		k: i - 1
		for j 1 k 1 [append subject pick wordlist j]
		object: make block! 0
		k: i + 1
		for j k N 1 [append object pick wordlist j]
		print ["subject:" subject]
		print ["verb:" verb]
		print ["object:" object]
		break
		break
	    ] ; end if
	] ; end foreach verb
    ] ; end for i
] ; end foreach sentence
