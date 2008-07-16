# 5:30 pm 1998/2/10
# new syntax Jul/24/2005

# at space,time { subject do move od object from space to space done; };
subject do move=moving od object done;
moving do change od object
	from space=initial
	to space=final
done;



# useful relation
#================
r_move is relation with
	format = [entity:1; entity:2; space:3; space:4],
	meaning = {$1 do move od $2
		from space=$3
		to   space=$4
		done;};

do read from r_move.rel done;

set hfocus = [subject,move,event,relation];
