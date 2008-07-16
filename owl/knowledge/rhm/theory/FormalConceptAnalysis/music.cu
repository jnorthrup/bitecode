# 7:30 pm 12/28/97

# Stumme example
# ICCS'97 page 324
# interaction with Knowledge Explorer 1.5

# general context
#================
at view = tabula_rasa
existent ise* ?
# see the file "general.txt"

# musical instrument context
#===========================
at view = tabula_rasa  at view = v_music
instrument isa object
creator, knowledge_engineer isa man
at space=cplace,time=ctime creator do create instrument
at space=here,time=now knowledge_engineer do identify instrument

# two instruments
#----------------
kythara, aulos isa instrument
sound isa attribute

kythara has sound=chord
aulos has sound=wind

# another instrument
#-------------------
aeols_harp isa instrument
aeols_harp has sound="chord, wind"

instrument ise* ?

# more attributes & parts
#------------------------
cplace, ctime isa attribute
bow, vibrating_part isa part

kythara, aulos has cplace=Greece, ctime=ancient
aulos, aeols_harp haspart vibrating_part

# differentiate with respect to sound
#====================================
at view = v_music  at view = instrument/sound
!isd instrument with sound

instrument ise* ?

# differentiate with respect to cspace,ctime
#===========================================
at view=v_music  at view="instrument/cspace,ctime"
!isd instrument with cspace,ctime

instrument ise* ?
