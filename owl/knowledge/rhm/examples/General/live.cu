# 5:30 pm 1998/2/10
# new syntax Jul/24/2005

set hfocus = [person,live,event,relation];

# minimal characterization: 1 action/event
# at space,time { person do live=living done; };
# living do change od person
#	from space=birthplace,time=birthday,alive
#	to   space=deathplace,time=deathday,dead
# done;

# more useful characterization: 2 action/event
person do live=[birth,death] done;
birth do change od person to space=birthplace,time=birthday,alive done;
death do change od person to space=deathplace,time=deathday,dead done;


life is phenomenon with
	p_event = [birth,death];


# useful relation
#================
r_live is relation with
	format=[time:1; time:2; person:3],
	meaning={$3 has birthday=$1;
		at time=$2 {$3 do die;};
	};

do read from r_live.rel done;
