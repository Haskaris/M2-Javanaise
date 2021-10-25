# M2-Javanaise

Javanaise 1 : OK <br>
Javanaise 2 : OK <br>
Test : 
 - test_compteur.sh : Lance 5 gérants. Chaque gérant va incrémenter de 10 un compteur partagé (50 au total - 49 affiché). Bug (on ne sait pas si c'est un bug de synchro ou un bug de programme et on souhaiterais si possible une réponse) avec 5 gérants, fonctionne avec 4 gérants.

# Lancer
## Tests
Pour lancer le test (le coordinateur n'est pas requis) :
 - Se placer dans le répertoire DONNE/DONNE2/SOURCES/bin
 - Si le fichier n'a pas les droits d'execution, se donner les droits de l'executer (chmod +x test_compteur.sh)
 - ``./test_compteur.sh``

## IRC
Il faut lancer le coordinateur et autant de client IRC que souhaité

Pour lancer le coordinateur :
 - Se placer dans le répertoire DONNE/DONNE2/SOURCES/bin
 - ``java jvn.JvnCoordImpl``

Pour lancer un client IRC :
 - Se placer dans le répertoire DONNE/DONNE2/SOURCES/bin
 - ``java irc.Irc`` ou ``java irc.IrcProxy``
