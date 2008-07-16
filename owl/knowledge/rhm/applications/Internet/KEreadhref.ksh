# KEHOME/bin/KEreadref.ksh
# rhm Oct/5/2002

# readhref file.html ...
# recursive read of href URLs from HTML file
# NOTE: need visit count to avoid infinite loops

for fin in $*; do
    fout=${fin%.html}.href
    html2href.ksh "$fin"

    ke.exe <<-EOF
	do read from "$fout" done
	hreflist := "$fin" has href
	every f in $hreflist {
		! readhref.ksh $f done
	}
	exit
    EOF

done
