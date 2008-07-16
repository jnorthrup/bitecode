# 1999/7/18
# new syntax Jul/24/2005

# execution time: 0:26 min:sec using 133 MHz pentium, Windows 98

# printout control
set hfocus = [newstatement,newword,
	chit,question,s_action];
#set debug = kaction;
#=======================================================#
# sample English sentences                              #
#                                                       #
# Roger C. Schank, "Conceptual Information Processing", #
# North-Holland Publishing Company/American Elsevier    #
# Publishing Company, Inc., 1975.                       #
#=======================================================#

#=================#
# Schank page 118 #
#=================#
# John gave Mary a book.
# John gave Mary a beating.
# John advised Mary to drink the wine.
# John killed Mary by choking Mary.
# Did John give Mary a book ?
# John prevented Mary from buying the book
#	by giving the book to Rita.

# John wants a book.
# John wants Mary.
# John wants to buy a book.
# John wants Mary to buy a book.

advise is action with ktype=say;

at time=past;
#-----------#
John do give od a book to Mary done;
John do beat od Mary done;
John do say to Mary od {
	Mary do drink od the wine done;
	}
done;
{John do choke od Mary done;}  causes
  {Mary do die done;};
set debug = question;
if John do give od a book to Mary done; fi;
set debug = NO;
{John do give od the book to Rita done;}  causes
  {Mary do not buy od the book done;};

at time=now;
#----------#
John do want od a book done;
John do want od Mary done;
John do want od {
	John do buy od a book done;
	}
done;
John do want od {
	Mary do buy od a book done;
	}
done;


#==================#
# Schank page 147  #
#   use of context #
#==================#
# John and Mary were racing
# John beat Mary

# John hated Mary
# John gave Mary a sock

# John was hunting
# John shot a buck

at time=past;
#-----------#
John,Mary do race done;
John do win od race done;

John do hate od Mary done;
John do sock od Mary done;

John do hunt done;
John do shoot od a buck done;


# output
#=======
vdo {events  isall* ?;} done;
vdo {kobject ise*   ?;} done;
