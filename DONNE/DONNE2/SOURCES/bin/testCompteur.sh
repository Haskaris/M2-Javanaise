#! /bin/bash

(trap 'kill 0' SIGINT;

java jvn.JvnCoordImpl &
P0=$!

sleep 1
java compteur.Gerant "Gerant1" &
P1=$!
java compteur.Gerant "Gerant2" &
P2=$!
java compteur.Gerant "Gerant3" &
P3=$!
java compteur.Gerant "Gerant4" &
P4=$!
java compteur.Gerant "Gerant5" &
P5=$!
wait $P0 $P1 $P2 $P3 $P4 $P5
)
