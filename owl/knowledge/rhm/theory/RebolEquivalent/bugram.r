REBOL [
	Title:	"KR grammar"
	Author:	"Richard H. McCullough"
	Email:	rhm@cdepot.net
	Date:	28-May-1999
	File:	%bugram.r
	Purpose:{
		Define (simplified) KR grammar.
		Use bottom up order, so all words are defined.
	}
]

quantifier: [
	'a 'some 'the
	'no
	'either 'all
]
verb: [
	'isa 'ise 'ism 'isc 'isd 'isi
	'is
	'has 'haspart
	'do 'changes
	'isin
]
preposition: [
	'at
	'of 'with
	'from 'to
]
conjunction: [
	"," ";"
	'and 'or 'not
	'means 'causes
	'every 'done
	'if 'then 'else 'fi
	'iff
	'implies
	'supports
]

word: "xyz"
hname: word
rname: word
variable: word
value: word
cmdname: word
nvop: "="
nvop: "+="
nvop: "-="

phrase: word word
nvphrase: phrase nvop phrase
prepphrase: preposition nvphrase
hphrase: "/" phrase
rphrase: phrase ";" phrase	; infon

arglist: phrase

context: nvphrase
product: phrase
subject: phrase
object: nvphrase
object: nvphrase "(" phrase ")"


assignment: variable nvop value

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

command: '! cmdname arglist prepphrase
command: hierarchy-block
command: relation-block

statement: subject verb object prepphrase
statement: product ":=" statement

question: statement "?"
question: statement	; with internal "?"


clause: assignment
clause: command
clause: question
clause: statement

sentence: clause conjunction sentence
sentence: clause

clause: 'every sentence 'do sentence 'done
clause: 'if sentence 'then sentence 'else sentence 'fi
clause: 'either sentence 'or sentence
clause: 'all sentence 'and sentence

proposition: 'at context "{" sentence "}"
proposition: 'at context
proposition: sentence
