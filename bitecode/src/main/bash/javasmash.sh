#!/bin/bash



{
# parms $1 -- source dir 

cat <<EOF 
//(thinking outloud)
//JavaList.h 
// LIST, MAP, NIO cursor
// handles x,y same
#define(x,y) $[x] .get(x)

//handles mutliple
#define(x,y) $>[x]y .put(x,y)

//nio ByteBuffer relo
#define(x,y) x$>y ((ByteBuffer) x.position(y))
 
//ENUMS
#define(x,y) ^[x] .values()[x]
#define(x,y) ^_[x] .getValue(#x)

#define public 
 class JppOut {
EOF

{
for i in $(find  $1  -iname *.jav*) 
do 
#inline all files into one massive set of innser classes
echo  '#include "'$i'"'
done
}

}| cpp -w -P  |dos2unix >/tmp/jpp.out 

grep </tmp/jpp.out  -v "^import " |grep -v "^package " >/tmp/x.bottom 
grep </tmp/jpp.out  "^import "  >/tmp/x.top

cat <(sort -uf /tmp/x.top ) /tmp/x.bottom |tee /tmp/AttributeInfoHeader