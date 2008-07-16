# KEHOME/knowledge/ApplicationInternet/bookmark2rel.ksh
# Jun/6/1997

# book2rel
#=========
# convert from Netscape bookmark.htm
# to Knowledge Explorer bookmark.rel
#	url; title; keyword

grep "HREF=" |
sed '
	s/ *<DT><A//
	s/HREF="//
	s/ADD_DATE=".*"//
	s/LAST_VISIT=".*"//
	s/LAST_MODIFIED=".*"//
	s/>/; /
	s/<.A>./;/
	s/"//g
    ' |
sort -u
