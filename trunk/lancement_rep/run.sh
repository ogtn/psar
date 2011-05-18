#!/bin/bash

make

path="/users/Etu8/2502338/workspace/psar/lancement_rep/node.jar"
pathOut="/users/Etu8/2502338/workspace/psar/lancement_rep/comm/"
nbSleep=1

echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/1.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/2.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/3.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/4.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/5.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/6.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/7.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/8.txt"
echo -e "\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n"  >> "$pathOut/9.txt"

#ssh ari-31-312-01 "killall java"
#sleep $nbSleep
#ssh ari-31-312-01 "$path" 1000 1515 ari-31-312-01 > "$pathOut/1.txt" &
#sleep $nbSleep

#ssh ari-31-312-11 "killall java"
#sleep $nbSleep
ssh ari-31-312-11 "$path" 1000 1515 ari-31-312-11 > "$pathOut/1.txt" &
sleep $nbSleep

#ssh ari-31-312-02 "killall java"
#sleep $nbSleep
ssh ari-31-312-02 "$path" 2000 1515 ari-31-312-02 1000 1515 ari-31-312-11 > "$pathOut/2.txt" &
sleep $nbSleep

#ssh ari-31-312-03 "killall java"
#sleep $nbSleep
ssh ari-31-312-03 "$path" 3000 1515 ari-31-312-03 1000 1515 ari-31-312-11 > "$pathOut/3.txt" &
sleep $nbSleep

#ssh ari-31-312-04 "killall java"
#sleep $nbSleep
ssh ari-31-312-04 "$path" 4000 1515 ari-31-312-04 1000 1515 ari-31-312-11 > "$pathOut/4.txt" &
sleep $nbSleep

##ssh ari-31-312-05 "killall java"
#sleep $nbSleep
#ssh ari-31-312-05 "$path" 5000 1515 ari-31-312-05 1000 1515 ari-31-312-01 > "$pathOut/5.txt" &
#sleep $nbSleep

#ssh ari-31-312-06 "killall java"
#sleep $nbSleep
ssh ari-31-312-06 "$path" 6000 1515 ari-31-312-06 1000 1515 ari-31-312-11 > "$pathOut/6.txt" &
sleep $nbSleep

#ssh ari-31-312-07 "killall java"
#sleep $nbSleep
ssh ari-31-312-07 "$path" 7000 1515 ari-31-312-07 1000 1515 ari-31-312-11 > "$pathOut/7.txt" &
sleep $nbSleep

##ssh ari-31-312-08 killall java
#sleep $nbSleep
#ssh ari-31-312-08 "$path" 8000 1515 ari-31-312-01 > "$pathOut/8.txt" &
##sleep $nbSleep

##ssh ari-31-312-09 killall java
#sleep $nbSleep
#ssh ari-31-312-09 "$path" 9000 1515 ari-31-312-01 > "$pathOut/9.txt" &
##sleep $nbSleep

##ssh ari-31-312-10 killall java
#sleep $nbSleep
#ssh ari-31-312-10 "$path" 10000 1515 ari-31-312-01 > "$pathOut/10.txt" &
##sleep $nbSleep

##ssh ari-31-312-11 killall java
#sleep $nbSleep
#ssh ari-31-312-11 "$path" 11000 1515 ari-31-312-01 > "$pathOut/11.txt" &
##sleep $nbSleep

#ssh ari-31-312-12
#ssh ari-31-312-13
#ssh ari-31-312-14
#ssh ari-31-312-15

#awk < /tmp/in '{print $2}' > /tmp/out 
#netstat -tanpu | grep java
#sleep $nbSleep

