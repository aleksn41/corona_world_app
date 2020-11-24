# Backlog/Verlauf
Dieses Dokument beschreibt das Design des Backlogs/Verlaufs
# Funktionalität
Dieses Modul soll dem Nutzer erlauben seine Statistik-Anfragen anzusehen oder zu löschen.
Es werden bis zu 1000 Anfragen abgespeichert.
Es können über eine Schnittstelle Daten über den Verlauf der Anfragen gestellt aber auch neue Anfragen eingetragen werden. Mehr Infos dazu bei Schnittstellen

# Implementierung
Das Modul Backlog/Verlauf enthält eine statische Methode, die eine Anfrage erhält und diese permanent in den App spezifischen Daten abspeichert.
Beim Starten der App werden diese Daten aus dem internen Speicher geladen und bei jedem neuen Eintrag geupdatet.

# Schnittstellen
Es existieren folgende Schnittstellen in diesem Modul:

- Eintrag einer Anfrage (addRequest)
- Holen einer Liste von Anfragen aus dem Verlauf (getRequests)

Mehr Infos zu den Schnittstellen gibt es [hier](/Documentation/History)
