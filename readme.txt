Ich habe versucht meinen Code ausf�hrlich zu kommentieren, damit man ihn besser verstehen kann. Hier eine Zusammenfassung der drei Komponenten:

-Cloud Controller:
Der Cloud Controller k�mmert sich um die wichtigsten Sachen. Er hat 2 Listen, eine Liste beinhaltet die User, die andere die Nodes, und ver�ndert diese
abh�ngig von den Befehlen die die User eingeben. Es gibt viele Runnable Objekte die parallel laufen, diese sind:
-- ClientListener: erzeugt einen neuen Thread (ClientHandler) immer wenn sich ein neuer Client verbindet.
-- ClientHandler: ist wohl die wichtigste Klasse. K�mmert sich um alle Requests die von den Usern kommen, und leitet sie auch an Nodes weiter falls n�tig.
-- NodeListener: empf�ngt UDP Nachrichten von den Nodes und aktualisiert ihren Status.
-- NodeIsAliveChecker: kontrolliert regelm��ig ob ein Node offline gegangen ist.

-Node:
Es gibt hier die Methode createLogFile, die eine Logdatei erstellt und speichert. Das AlivePacketSender Objekt ist ein Runnable Objekt, das separat in einem
eigenen Thread l�uft um immer wieder dem Cloud Controller "Lebenszeichen" zu schicken. CloudControllerListener ist auch ein Runnable Objekt, wird auch in einem eigenen 
Thread ausgef�hrt und wartet immer auf einen Request vom Controller um einen Ausdruck zu berechnen, erzeugt die Logdatei, und schickt anschlie�end dem Controller das 
Ergebnis dieser Berechnung zur�ck.

-Client:
Hier gibt es nicht viel zu sagen. Der Client hat eine Methode sendRequest, die nur dazu dient, dem Cloud Controller einen Befehl zu schicken und 
die Antwort des Controllers zur�ckzugeben. Nat�rlich war es nicht n�tig, das in einem separaten Thread zu machen, da sonst nichts anderes
gleichzeitig ausgef�hrt werden muss(die Shell rennt in einem Thread, weil sie das Runnable Interface implementiert).


Ich habe noch folgende Annahmen getroffen, was den compute Befehl angeht: 
- Der User f�hrt den compute Befehl aus, und in einem Augenblick geht ein Node offline (nur dieser Node kann eine im Ausdruck vorhandene Operation durchf�hren), 
dem User werden dann keine Credits abgezogen, bei den Nodes wird das Usage schon erh�ht, und zwar f�r alle die eine Operation erfolgreich durchf�hren konnten.
- Der User f�hrt den compute Befehl aus, und in einem Augenblick geht ein Node offline, aber es gibt mindestens einen anderen Node der diese Operation unterst�tzt, 
dann wird weiter gemacht.
- Gibt es eine Division durch 0, dann werden dem User Credits f�r jede Operation abgezogen, auch das Usage der Nodes erh�ht sich, das ganze bis Division durch 0 auftritt.
- Dem User werden also nur dann keine Credits abgezogen, wenn wenn es keinen Node f�r eine notwendige Operation gibt, bei den Nodes wird das Usage immer erh�ht, wenn 
eine einzelne Operation erfolgreich durchgef�hrt werden konnte.

Um mein Programm besser zu testen, habe ich noch zus�tzliche Tests geschrieben(sind also unter src/test/resources zu finden).