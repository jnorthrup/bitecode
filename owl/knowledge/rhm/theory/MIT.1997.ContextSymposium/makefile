# 4:00 am 12/15/97

TEXT	= \
	outline.txt \
	intro.txt \
	pok.txt \
	krdesign.txt \
	egrammar.txt \
	fca.txt \
	I.ku \
	italy.ku \
	ke.txt \
	more.txt \
	ref.txt

all: rhm97.rtf pok.rtf


pok.rtf: pok.txt
	cp $^ $@

rhm97.rtf: rhm97.txt
	cp $^ $@

rhm97.txt: $(TEXT)
	cat $^ >$@
