# 12:30 pm 12/22/97
# new syntax Jul/24/2005

#=======================================#
# Simple Examples of Change Unit (chit) #
#=======================================#
do read from cu/live.cu done;
do read from cu/move.cu done;

#set debug = ACTION;
#set debug = EVENT;

blob	isa	entity;
blob	isc	Blob1,Blob2;
animal	isc	dog;
person	isc	Dick,Bob;
dog	isc	Reno;
object	isc	car;

hit	isa	action;
run	isa	move;

space	isc
	frontyard,backyard,
	driveway,garage;

at space=home {	Dick do live done; };

at time=1700 {	Blob1 do hit Blob2 done; };

Reno do run
	from space=frontyard,time="10:00 am 2/24/97"
	to   space=backyard, time="10:01 am 2/24/97"
	done;

at time=6pm {	Bob do move car
			from space=driveway
			to   space=garage
		done; };

set hfocus	= [newword,blob,animal,event];
