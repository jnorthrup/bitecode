; from Jon 1999/6/1
; m_parse recurses until it finds a verb
; it will cause problems if you plan to use gerunds
; recurse fails at about 200 iterations

REBOL []

m_svo: [
        (print "begin m_svo" subject: make string! [])  
                m_parse
        (print ["subject:" subject]
         print ["verb   :" verb]
         print ["object :" object]
         print "end m_svo")
]
m_parse: [
        [copy verb verbs        copy object to end ] |
        [ copy temp-sub thru #" " (subject: append subject temp-sub ) m_parse ] 
]

verbs: [ "isa " | "ise " | "isin " | "is " | "has " | "do " ] 
;;; space is just to avoid "do" matching "dog"

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

                parse sentence m_svo

                ;parse/all sentence grammar
        ;]
]



