#!/bin/bash

echo "-1"

make

path="/users/Etu8/3069618/Desktop/lancement_rep/node.jar"
pathOut="/users/Etu8/3069618/Desktop/lancement_rep/comm/"
nbSleep=3

echo "0"

ssh ari-31-312-01 "killall java"
sleep $nbSleep
ssh ari-31-312-01 "$path" 1000 1515 ari-31-312-01 > "$pathOut/1.txt" &
sleep $nbSleep

echo "1"

ssh ari-31-312-02 "killall java"
sleep $nbSleep
ssh ari-31-312-02 "$path" 2000 1515 ari-31-312-02 1000 1515 ari-31-312-01 > "$pathOut/2.txt" &
sleep $nbSleep

echo "2"

#ssh ari-31-312-03 killall java
#ssh ari-31-312-03 "$path" 3000 1515 ari-31-312-01 > "$pathOut/3.txt" &
#sleep $nbSleep

#ssh ari-31-312-04 killall java
#ssh ari-31-312-04 "$path" 4000 1515 ari-31-312-01 > "$pathOut/4.txt" &
#sleep $nbSleep

#ssh ari-31-312-05 killall java
#ssh ari-31-312-05 "$path" 5000 1515 ari-31-312-01 > "$pathOut/5.txt" &
#sleep $nbSleep

#ssh ari-31-312-06 killall java
#ssh ari-31-312-06 "$path" 6000 1515 ari-31-312-01 > "$pathOut/6.txt" &
#sleep $nbSleep

#ssh ari-31-312-07 killall java
#ssh ari-31-312-07 "$path" 7000 1515 ari-31-312-01 > "$pathOut/7.txt" &
#sleep $nbSleep

#ssh ari-31-312-08 killall java
#ssh ari-31-312-08 "$path" 8000 1515 ari-31-312-01 > "$pathOut/8.txt" &
#sleep $nbSleep

#ssh ari-31-312-09 killall java
#ssh ari-31-312-09 "$path" 9000 1515 ari-31-312-01 > "$pathOut/9.txt" &
#sleep $nbSleep

#ssh ari-31-312-10 killall java
#ssh ari-31-312-10 "$path" 10000 1515 ari-31-312-01 > "$pathOut/10.txt" &
#sleep $nbSleep

#ssh ari-31-312-11 killall java
#ssh ari-31-312-11 "$path" 11000 1515 ari-31-312-01 > "$pathOut/11.txt" &
#sleep $nbSleep

#ssh ari-31-312-12
#ssh ari-31-312-13
#ssh ari-31-312-14
#ssh ari-31-312-15

#awk < /tmp/in '{print $2}' > /tmp/out 
#netstat -tanpu | grep java

