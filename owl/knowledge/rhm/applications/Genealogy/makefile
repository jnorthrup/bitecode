# KEHOME/knowledge/applications/Genealogy/makefile
# Aug/29/2005
# Feb/8/2007 new directory structure

#=================================#
# all zip files in this directory #
#=================================#

KEHOME	= /home/ke
FAMILY	= .
BINDIR	= $(FAMILY)/bin
GEDDIR	= $(FAMILY)/ged
RELDIR	= $(FAMILY)/rel
RPTDIR	= $(FAMILY)/report

#DB	= $(KEHOME)/db
DB	= $(FAMILY)/db
VIEW	= test
DBDIR	= $(DB)/$(VIEW)

TMP	= /tmp
ZIP	= zip
UNZIP	= unzip

KE	= ke -q

RELATION_FILE = \
	$(RELDIR)/address.rel	\
	$(RELDIR)/aka.rel	\
	$(RELDIR)/birth.rel	\
	$(RELDIR)/child.rel	\
	$(RELDIR)/death.rel	\
	$(RELDIR)/divorce.rel	\
	$(RELDIR)/marriage.rel	\
	$(RELDIR)/note.rel	\
	$(RELDIR)/phone.rel	\
	$(RELDIR)/school.rel	\
	$(RELDIR)/work.rel

RELATION_TABLE = \
	$(DBDIR)/r_address.pag	\
	$(DBDIR)/r_aka.pag	\
	$(DBDIR)/r_birth.pag	\
	$(DBDIR)/r_child.pag	\
	$(DBDIR)/r_death.pag	\
	$(DBDIR)/r_divorce.pag	\
	$(DBDIR)/r_marriage.pag	\
	$(DBDIR)/r_note.pag	\
	$(DBDIR)/r_phone.pag	\
	$(DBDIR)/r_school.pag	\
	$(DBDIR)/r_work.pag

SUMMARY_TABLE = \
	$(DBDIR)/Person.pag	\
	$(DBDIR)/Family.pag


all: \
	relation_table  \
	summary_table   \
	family.zip

install: \
	family

clean:
	rm -f $(FAMILY)/*.zip $(DB)/*.zip $(GEDDIR)/*.zip
	rm -f *.out */*.out */*/*.out */*/*/*.out */*/*/*/*.out
	rm -f *.err */*.err */*/*.err */*/*/*.err */*/*/*/*.err
	rm -f *.log */*.log */*/*.log */*/*/*.log */*/*/*/*.log

#============================#
# build GDBM relation tables #
#============================#
relation_table: $(RELATION_TABLE)
	> relation_table

$(RELATION_TABLE): $(RELATION_FILE) $(RELDIR)/family.ku
	cd $(RELDIR); $(KE) family.ku

summary_table: $(SUMMARY_TABLE)
	> summary_table

$(SUMMARY_TABLE): $(RELATION_TABLE) $(BINDIR)/Person.mkr
	cd $(RELDIR); $(KE) ../bin/Person.mkr

#======================#
# build download files #
#======================#
family.zip: family.ged clean
	mkzip family

family.ged: $(RELDIR)/family.ku $(RELATION_FILE) $(BINDIR)/ged.mkr
	cd $(RELDIR); $(KE) ../bin/ged.mkr
	mv $(RELDIR)/$@ $(FAMILY)/
	chmod 664 $@

gdbm.zip: clean
	cd $(DB); mkzip gdbm
	mv $(DB)/$@ $(FAMILY)/

#========================#
# install download files #
#========================#
family:
	$(UNZIP) family.zip

gdbm:
	mv gdbm.zip $(DB)/
	cd $(DB); $(UNZIP) gdbm.zip
	mv $(DB)/gdbm.zip $(FAMILY)/
ged:
	mv ged.zip $(GEDDIR)/
	cd $(GEDDIR); $(UNZIP) ged.zip
	mv $(GEDDIR)/ged.zip $(FAMILY)/

#=================================#
# generate family history reports #
#=================================#
reports: \
	$(RPTDIR)/r_aka.txt	\
	$(RPTDIR)/r_phone.txt	\
	$(RPTDIR)/r_address.txt	\
	$(RPTDIR)/surname.rel	\
	$(RPTDIR)/phonebook.rel	\
	$(RPTDIR)/email.txt	\
	$(RPTDIR)/phone.txt	\
	$(RPTDIR)/person.list

$(RPTDIR)/email.txt: $(RELDIR)/phone.rel $(BINDIR)/email.mkr
	cd $(RELDIR); $(KE) ../bin/email.mkr
	mv $(RELDIR)/email.txt $@
	#deletecomment $(RELDIR)/email.out | grep -v existent >$@

$(RPTDIR)/phone.txt: $(RELDIR)/phone.rel $(BINDIR)/phone.mkr
	cd $(RELDIR); $(KE) ../bin/phone.mkr
	mv $(RELDIR)/phone.txt $@
	#deletecomment $(RELDIR)/phone.out | grep -v existent >$@

$(RPTDIR)/r_phone.txt: $(RELDIR)/phone.rel $(BINDIR)/r_phone.mkr
	cd $(RELDIR); $(KE) ../bin/r_phone.mkr

$(RPTDIR)/r_address.txt: $(RELDIR)/address.rel $(BINDIR)/r_address.mkr
	cd $(RELDIR); $(KE) ../bin/r_address.mkr

$(RPTDIR)/person.list: $(RELATION_FILE) $(BINDIR)/everybody.mkr
	cd $(RELDIR); $(KE) ../bin/everybody.mkr
	mv $(RELDIR)/person.list $@
	sort -o $@ $@

$(RPTDIR)/phonebook.rel: $(RELDIR)/family.ku $(RELATION_FILE) $(BINDIR)/phonebook.mkr
	cd $(RELDIR); $(KE) ../bin/phonebook.mkr
	cp $(RELDIR)/phonebook.rel $@

$(RPTDIR)/surname.rel: $(RELDIR)/family.ku $(RELATION_FILE) $(BINDIR)/surname.mkr
	cd $(RELDIR); $(KE) ../bin/surname.mkr
	cp $(RELDIR)/surname.rel $@

#=======================#
# do consistency checks #
#=======================#
check:
	check.out	\
	checkval.out

check.out: $(RELDIR)/family.ku $(RELATION_FILE) $(BINDIR)/check.mkr
	cd $(RELDIR); $(KE) ../bin/check.mkr

checkval.out: $(RELDIR)/family.ku $(RELATION_FILE) $(BINDIR)/checkval.mkr
	cd $(RELDIR); $(KE) ../bin/checkval.mkr


#====================#
# reformat relations #
#====================#
##aka.rel: newaka.rel
##	cp newaka.rel aka.rel
##
##newaka.txt: newaka.rel gdbmdump
##	gdbmdump newaka
##
##newaka.rel: name.ku newaka.mkr 
##	$(KE) newaka.mkr
