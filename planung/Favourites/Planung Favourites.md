# Favoriten
Dieses Dokument beschreibt das Design der Favoriten
# Funktionalität
Dieses Modul soll dem Nutzer erlauben seine Statistik-Anfragen zu favorisieren oder entfavorisieren.
Es werden bis zu 100 Anfragen abgespeichert.
Es können über eine Schnittstelle Daten über die Favoriten erlangt werden oder Anfragen favorisiert oder entfavorisiert. Mehr Infos dazu bei Schnittstellen

# Implementierung
Das Modul Favoriten enthält eine statische Methode, die eine Anfrage favorisieren oder entfavorisieren lässt. Die Daten werden permament im internen Speicher gespeichert.
Beim Starten der App werden diese Daten aus dem internen Speicher geladen und bei jedem neuen Eintrag geupdatet.

# Schnittstellen
Es existieren folgende Schnittstellen in diesem Modul:

- Favorisieren einer Anfrage (favRequest)
- Entfavorisieren einer Anfrage (unfavRequest)
- Holen einer Liste von Favoriten (getRequests)

Mehr Infos zu den Schnittstellen gibt es [hier](/Documentation/Favourites)
