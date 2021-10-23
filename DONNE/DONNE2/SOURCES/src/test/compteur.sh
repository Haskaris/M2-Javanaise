#!/bin/bash
pwd
trap 'kill $0 $1 $2 $3 $4 $5' SIGINT
cheminGerant='..\compteur\Gerant.class'
cheminCoord='..\jvn\JvnCoordImpl.class'

echo $cheminGerant
java $cheminCoord &
$0=$!
java $cheminGerant &
$1=$!
java $cheminGerant &
$2=$!
java $cheminGerant &
$3=$!
java $cheminGerant &
$4=$!
java $cheminGerant &
$5=$!