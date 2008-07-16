REBOL [
	Title:	"concept datatype"
	Author:	"Richard H. McCullough"
	Email:	rhm@cdepot.net
	Date:	14-May-1999
	File:	%concept.r
	Purpose:{
		Define concept object.
		Define cprint function, to print concept.
		Define newid function, to create unique name.
		Define newconcept function, to create new concept.
		Define newperson function, to create new person.
	}
]

concept: make object! [
	iname:	"new concept"	; unique internal name
	mark:	0		; visit count
	ctype:	"existent"	; concept type
	gtype:	""		; group type
	ptype:	""		; part type
	mtype:	""		; measurement type

	class:	"existent"	; isa*
	genus:	[]		; isa
	unit:	[]		; ise
	whole:	[]		; ism
	part:	[]		; isc or haspart

	alias:	"new concept"	; is
	echar:	[]		; is of with
	attr:	[]		; has
	act:	[]		; do with
	eobj:	[]		; changes from to
	rel:	[]		; isin
	fact:	""		; unique fact name
]


cprint: function [ name ] [cname] [
	cname: name/iname
	print [ cname "/iname:"   cname      ]
	print [ cname "/mark: "   name/mark  ]
	print [ cname "/ctype:"   name/ctype ]
	print [ cname "/gtype:"   name/gtype ]
	print [ cname "/ptype:"   name/ptype ]
	print [ cname "/mtype:"   name/mtype ]

	print [ cname "/class:"   name/class ]
	print [ cname "/genus:"   name/genus ]
	print [ cname "/unit: "   name/unit  ]
	print [ cname "/whole:"   name/whole ]
	print [ cname "/part: "   name/part  ]

	print [ cname "/alias:"   name/alias ]
	print [ cname "/echar:"   name/echar ]
	print [ cname "/attr: "   name/attr  ]
	print [ cname "/act:  "   name/act   ]
	print [ cname "/eobj: "   name/eobj  ]
	print [ cname "/rel:  "   name/rel   ]
	print [ cname "/fact: "   name/fact  ]
]

lastid: 0
newid: func [name] [
	lastid: lastid + 1
	rejoin [name "_" lastid]
]

newconcept: func [name aka type] [
	make concept [
		iname: newid name
		alias: reduce [name "," aka]
		ctype: type
	]
]

newperson: function [name aka] [pname] [
	pname: newconcept name aka "entity"
	pname/class: "person"
	pname/genus: "person"
	return pname
]


; test
Dick: make concept [
	iname: "Dick"
	ctype: [entity]
	class: [entity person]
	genus: [person]
	alias: ["Dick" "," "Richard H. McCullough"]
	attr: [sex = male]
	act: [walk = walk_123]
	rel: [phonebook]
]
cprint dick

Bob: newconcept "Bob" "Robert B. McCullough" "person"
bob/genus: "person"
cprint Bob

Sue: newperson "Sue" "Elizabeth Sue McCullough"
Sue/attr: "sex = female"
cprint Sue
