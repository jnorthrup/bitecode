# KEHOME/bin/readref.ksh
# rhm Oct/5/2002

# readhref file.html ...
# recursive read of href URLs from HTML file
# NOTE: need visit count to avoid infinite loops

for fin in $*; do
    fout=${fin%.html}.href
    echo "# INFO: readhref: processing $fin" >&2
    html2href.ksh "$fin"

    echo "# INFO: readhref: processing $fout" >&2
    readhref.ksh $( cat "$fout" | sed 's/^.*+= "//' | sed 's/"$//' )

done
