# 8:00 am 12/26/97
# new syntax Jul/24/2005

hfocus = "person,live,marry,relation"

event isc
	abortion,
	adoption,
	birth,
	conception,
	death,
	e_divorce,
	marriage,
	living;
action isc
	abort,
	adopt,
	bear,
	conceive,
	die,
	divorce,
	marry,
	live;
phenomenon isc
	life;

life is phenomenon with
	p_event = [abortion,adoption,birth,conception,death,divorce,marriage,wedding,living],
	p_cause =
		{marriage  => adoption;
		marriage   => conception;
		conception => birth;
		abortion   => not birth;
		birth      => living;
		death      => divorcing;};

live isall birth:1, death:2;
