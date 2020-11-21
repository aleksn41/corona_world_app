# Map

Hier wird die Map und deren Einbindung in die Applikation beschrieben.

# Features

  - Anzeigen einer Map durch Erstellen einer HTML-Datei mit Google Charts -> stärkere Einfärbung der Länder je nach Infizierungsgrad
  - Tooltips mit genaueren Informationen zum jeweiligen Land
  - Tooltiplink zur Statistik (Javaskript-Schnittstelle (OnClickListener -> https://stackoverflow.com/questions/11752199/can-i-customize-the-tooltip-text-in-a-google-geochart-chart))

## Methoden

  - getMap -> Parameter: Ausnahmen von Ländern, welche ignoriert werden sollen; Rückgabe: String (HTML) der Map-Datei zum Anzeigen in einer WebView
  
## Schnittstellenbeschreibung (Tooltiplink)

Da Google Charts an sich keine custom Tooltips unterstützt muss ein Workaround genutzt werden, indem eine HTML-Datei als Daten-Tabellen übergeben werden. In dieser HTML-File wird dann ein OnClickListener registriert, welcher ein an das außen anliegende Javascript ein Signal weitergibt, sollte der Nutzer auf ein Element des Tooltips klicken. Diese Javascript gibt dann über eine durch JavaInterface in die Javascript-Datei integrierte Klasse ein Event weiter, welches die UI auf die zugehörige Statistik-Seite verweist.
