Ich habe versucht meinen Code ausführlich zu kommentieren, damit man ihn besser verstehen kann. Hier eine Zusammenfassung der drei Komponenten:

-Cloud Controller:
Der Cloud Controller kümmert sich um die wichtigsten Sachen. Er hat 2 Listen, eine Liste beinhaltet die User, die andere die Nodes, und verändert diese
abhängig von den Befehlen die die User eingeben. Es gibt viele Runnable Objekte die parallel laufen, diese sind:
-- ClientListener: erzeugt einen neuen Thread (ClientHandler) immer wenn sich ein neuer Client verbindet.
-- ClientHandler: ist wohl die wichtigste Klasse. Kümmert sich um alle Requests die von den Usern kommen, und leitet sie auch an Nodes weiter falls nötig.
-- NodeListener: empfängt UDP Nachrichten von den Nodes und aktualisiert ihren Status.
-- NodeIsAliveChecker: kontrolliert regelmäßig ob ein Node offline gegangen ist.

-Node:
Es gibt hier die Methode createLogFile, die eine Logdatei erstellt und speichert. Das AlivePacketSender Objekt ist ein Runnable Objekt, das separat in einem
eigenen Thread läuft um immer wieder dem Cloud Controller "Lebenszeichen" zu schicken. CloudControllerListener ist auch ein Runnable Objekt, wird auch in einem eigenen 
Thread ausgeführt und wartet immer auf einen Request vom Controller um einen Ausdruck zu berechnen, erzeugt die Logdatei, und schickt anschließend dem Controller das 
Ergebnis dieser Berechnung zurück.

-Client:
Hier gibt es nicht viel zu sagen. Der Client hat eine Methode sendRequest, die nur dazu dient, dem Cloud Controller einen Befehl zu schicken und 
die Antwort des Controllers zurückzugeben. Natürlich war es nicht nötig, das in einem separaten Thread zu machen, da sonst nichts anderes
gleichzeitig ausgeführt werden muss(die Shell rennt in einem Thread, weil sie das Runnable Interface implementiert).


Ich habe noch folgende Annahmen getroffen, was den compute Befehl angeht: 
- Der User führt den compute Befehl aus, und in einem Augenblick geht ein Node offline (nur dieser Node kann eine im Ausdruck vorhandene Operation durchführen), 
dem User werden dann keine Credits abgezogen, bei den Nodes wird das Usage schon erhöht, und zwar für alle die eine Operation erfolgreich durchführen konnten.
- Der User führt den compute Befehl aus, und in einem Augenblick geht ein Node offline, aber es gibt mindestens einen anderen Node der diese Operation unterstützt, 
dann wird weiter gemacht.
- Gibt es eine Division durch 0, dann werden dem User Credits für jede Operation abgezogen, auch das Usage der Nodes erhöht sich, das ganze bis Division durch 0 auftritt.
- Dem User werden also nur dann keine Credits abgezogen, wenn wenn es keinen Node für eine notwendige Operation gibt, bei den Nodes wird das Usage immer erhöht, wenn 
eine einzelne Operation erfolgreich durchgeführt werden konnte.

Um mein Programm besser zu testen, habe ich noch zusätzliche Tests geschrieben(sind also unter src/test/resources zu finden).