REBOL [
    Title: "subject verb object grammar"
    Author: "Richard H. McCullough"
    Email: rhm@cdepot.net
    Date: 1999-Jul-1
    File: %svo.r
    Purpose: {
        parse statement
    }
]

;=======================================================
verbs: [
    "isin"  |
    "isa"   |
    "ise"   |
    "is"    |
    "has"   |
    "do"
]

m_verb: [ verbs end ]

;=======================================================
test: [
    "concept isa genus"
    "concept ise unit"
    "the big dog do bark loudly"
    "Dick McCullough isin the phonebook"
]

foreach sentence test [
    print ";==============================================="
    print ["sentence:" sentence]
    subject: make block! 0
    found_verb: false
    object: make block! 0
    wordlist: parse sentence none
    foreach word wordlist [
	either parse word m_verb [
            found_verb: true
            verb: word
        ] [ ; or
    	    either found_verb [
                append object word
            ] [ ; or
                append subject word
    	    ] ; end either found_verb
        ] ; end either parse
    ] ; end foreach word
    print ["subject:" subject]
    print ["verb:" verb]
    print ["object:" object]
    print ";-----------------------------------------------"
] ; end foreach sentence
