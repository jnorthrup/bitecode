# KEHOME/knowledge/ApplicationInternet/ihreadme.txt
# 1999/2/28
# new syntax Sep/30/2002


#======================#
# Internet Hierarchies #
#======================#

# The files in the KEHOME/knowledge/ApplicationInternet
# directory contain partial internet directory hierarchies
# from several public web sites, and KR programs to edit
# and compare the hierarchies (see edit.htm and compare.htm).
#
# The hierarchy categories are often ad hoc,
# with no definitions (or attributes) available.
# The particular categories chosen vary widely
# between directories. 
#
# Some of practical problems in comparing
# internet hierarchies are discussed below.

 1. The first problem is lexical variations such as 
 capitalization and singular/plural forms. 
 Example: 
    games, Games, Games@ 
    Autos, Automotive 
 This problem can be overcome by folding to lower case 
 and using aliases 
    set kcase = LOWER 
    games is Games, Games@ 
    Autos is Automotive 

2. Another problem is related categories which have been 
combined into a single category. 
Example: 
    Computers & Internet 
This problem can be overcome by separating the categories: 
    directory is begin hierarchy 
    Computers & Internet 
    /    Computers 
    /    Internet 
    end hierarchy directory 

3. The same word may have many different meanings. 
This situation can be represented as a hierarchy with 
the same word occupying multiple positions in the 
hierarchy.  (The "hierarchy" is actually a lattice.) 
Example: 
    Excite 
        Search 
            Horoscopes 
        Channels 
            Horoscopes 
        More Services: 
            Horoscopes 
This situation can be detected using 
    do read from excite.ho done 
    do check od genus done 
and corrected by deleting the more general meaning 
from the hierarchy, or by choosing a different word. 

4. In some situations the same word has very different 
meanings in two hierarchies. 
Example: 
   Excite                    InfoSeek 
      Shopping                  Shopping 
         Bargains                  Gifts 
         Clothes                   music CDs 
         Music                     online malls 
This can be detected by comparing the units of the 
categories using the UNIX diff command 
    !diff with -ib od excite.ho,infoseek.ho out diffei.txt done 

5. There may be contradictions between hierarchies, 
i.e., A isa B in one hierarchy, and A isa not B in another. 
Example: 
   Excite                    InfoSeek 
      Entertainment             Entertainment 
         Movies                    Books 
         Music                     games 
         TV                        great movies 
      Games                        music 
                                   ... 
This can be detected using 
    do read from excite.ho, infoseek.ho done 
    Excite is InfoSeek 
    do check od genus done 

Knowledge Explorer automatically checks for 
explicit contradictions of the form 
    A is B; ...; A is not B 
