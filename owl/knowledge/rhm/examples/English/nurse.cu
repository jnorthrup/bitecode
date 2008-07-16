<HTML><XMP>
# KEHOME/knowledge/ExamplesEnglish/nurse.cu
# 1999/7/20
# new syntax Oct/24/2003
# new pronoun syntax Sep/15/2005

# execution time: 0:32 min:sec using 133 MHz pentium, Windows 98

# My Excite news story 12/18/97
#==============================
# Nurse charged with killing patient
# CAIRO (Reuters) - An Egyptian nurse has been charged with
# killing one patient and trying to kill a further 29 to avenge her
# unrequited love for the doctor who was treating them, prosecutors
# said on Wednesday.
# 
# They said Aida Nourredin Mohammed Abu Zeid, 25, also faced
# charges of stealing medicine and drugs and forging hospital
# documents to cover up the missing items.
# 
# Prosecutors said Abu Zeid had confessed to the crimes during
# interrogation. Security sources said the maximum penalty if she
# were found guilty was death by hanging.
# 
# Prosecutors said she worked at the intensive care unit of the
# Alexandria University Hospital for seven years before carrying out
# the alleged crimes.
# 
# They said she injected her victims with substances that caused
# respiratory failure. Investigations showed that all the cases of
# respiratory failure took place during her shift, they said.
# 
# Abu Zeid was in love with the doctor who was treating all the
# patients concerned, but he did not return her affections, they
# added.
# 
# Abu Zeid told local newspapers at the time of her arrest earlier this
# year that the doctors at the hospital were using her as a scapegoat
# for their mistakes.

 # newpage (CONTROL-L)
set hfocus = [newword,newstatement,
	person,chit,s_action,
	variable];

# NOTE:
# subject do kaction od { sentence } done;
#	->  at view=kaction_subject { sentence };

allege is action with ktype=say;

at space=Cairo, time=1997/12/18, view=v_news_story;
#------------------------------------------------#
news_story has
	headline="Nurse charged with killing patient",
	time=1997/12/18,
	source=Reuters;
person isg
	the doctors,
	his patients,
	prosecutors; # group of attorneys
doctor1   isu the doctors;
nurse1    isu person;
patient1  isu his patients;
further29 iss his patients;  # group of 29 patients

doctor1 has patient = his patients;
nurse1  has Egyptian, sex=female;
prosecutors do charge od nurse1 done;
prosecutors do say od {
	nurse1 do kill od patient1
		with purpose=avenge her unrequited love for doctor1
		done;
	nurse1 do try kill od further29
		with purpose=avenge her unrequited love for doctor1
		done;
    } done;
# 
nurse1 is Aida Nourredin Mohammed Abu Zeid;
nurse1 has age=25;
prosecutors do say od {
	nurse1 do steal od medicine,drugs done;
	nurse1 do forge od hospital documents
		with purpose=cover up stealing
		done;
    } done;
# 
nurse1 is Abu Zeid;
at time=t_interrogation;
#---------------------#
prosecutors do interrogate od nurse1 done;
prosecutors do say od {
	Abu Zeid do confess done;
    } done;
at time=1997/12/18;

#----------------#
security sources iss person; # group
#
she is nurse1;
security sources do say od {
	if she has guilty;
	then the maximum penalty is death by hanging;
	fi;
    } done;
# 
prosecutors do say od {
	at space= Alexandria University Hospital/intensive care unit,
	  time=past seven years { nurse1 do work done; };
    } done;
# 
they is prosecutors;
her victims is his patients; # group
they do say od {
	at time=past {
		{she do inject od substances to her victims done;}
		causes
		{her victims has respiratory failure;};
	};
    } done;
they do say od {
	they do investigate done;
	at time=during her shift { her victims has respiratory failure;};
    } done;
# 
doctor1 has sex=male;
he is doctor1;
they do say od {
	she do love od he done;
	he do not love od she done;
    } done;
# 
local newspapers iss person; # group working for companies
Alexandria University Hospital isu space;
the hospital is Alexandria University Hospital;
at time="time of her arrest earlier this year";
#------------------------------------------#
they do arrest od she done;
Abu Zeid do say to local newspapers od {
	the doctors do blame od she
		with purpose=cover up their mistakes
	done;
    } done;


# output
#=======
vdo exec od {events  isall* ?;} done;
vdo exec od {kobject isc*   ?;} done;

</XMP></HTML>
