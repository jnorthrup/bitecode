# KEHOME/knowledge/ApplicationInternet/addressbook2rel.ksh
# Jun/6/1997

# addr2rel
#=========
# convert from Netscape address.htm
#    <DT><A HREF="url" ALIASID="y" NICKNAME="z">title</A>
#    <DT><A HREF="url" ALIASOF="y">title</A>
# to Knowledge Explorer r_email.rel
#    email; nickname; person; elist

grep "HREF=" | grep -v "ALIASOF=" |
sed '
	s/<DT><A//
	s/HREF="//
	s/ALIASID="[0-9]*"//
	s/NICKNAME="/; /
	s/>/; /
	s/<.A>./;/
	s/"//g
    ' |
sort -u
