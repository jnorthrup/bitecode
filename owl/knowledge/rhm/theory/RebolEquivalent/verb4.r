; 1999/6/11

REBOL []

grammar: [
        (print "begin m_svo")
                m_svo
        (print ["subject:" subject]
         print ["verb   :" verb]
         print ["object :" object]
         print "end m_svo")
]
m_svo: [
        copy subject toverbs
        #" " copy verb to #" " #" "
        copy object to end
]

verbs: [ "isa" | "ise" | "is" | "has" | "do" | "isin" ] 
toverbs: [ to " isa " | to " ise " | to " is " | to " has " | to " do " | to " isin " ] 
;;; spaces are to ensure matching complete verb

test: [
        "concept isa genus"
        "concept ise unit"
        "the big dog do bark loudly"
        "Dick McCullough isin the phonebook"
]

foreach sentence test [
        print ["sentence:" sentence]
        parse/all sentence grammar
]



