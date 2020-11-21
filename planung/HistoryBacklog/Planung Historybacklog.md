# Backlog/Verlauf
Dieses Dokument beschreibt das Design des Backlogs/Verlaufs
# Funktionalität
Dieses Modul soll dem Nutzer erlauben seine Statistik-Anfragen anzusehen zu favorisieren/entfavorisieren oder zu löschen.
Außerdem kann in dem Backlog ein Callback über ein Interface registered werden, welches dem Callback Informationen darüber gibt, welche Anfragen favorisiert, entfavorisiert, öfter als eine bestimmte Grenze oder weniger als ein bestimmte Grenze aufgerufen wurden (wird beim Api-Management genutzt, um zu entscheiden welche Daten zwischengespeichert werden müssen, um die Nutzer Erfahrung zu verbessern). Es werden bis zu 100 Favoriten und 1000 Anfragen abgespeichert.

# Implementierung
Das Modul Backlog/Verlauf enthält eine statische Methode, die eine Anfrage erhält und diese permanent in den App spezifischen Daten abspeichert.
Außerdem existiert eine statische Methode, mit der eine Anfrage favorisiert werden kann.
Beim starten der App werden diese Daten aus dem internen Speicher geladen und bei jedem neuen Eintrag geupdatet.

# Schnittstellen
An diesem Modul kann ein Callback registriert werden, welches entweder gerufen wird, wenn eine Anfrage mehr oder weniger als eine bestimmte Grenze aufgerufen wurde, wobei die Grenze von dem bestimmt werden kann, der das Callback registriert. Es werden derzeit alle Anfragen in Betracht gezogen, jedoch werden evtl. nach Änderung des Designs nur die Anfragen des jetzigen und letzen Monats in Betracht gezogen.