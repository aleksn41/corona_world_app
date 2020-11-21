# Map

Hier wird die Map und deren Einbindung in die Applikation beschrieben.

# Features

  - Anzeigen einer Map durch Erstellen einer HTML-Datei mit Google Charts -> stärkere Einfärbung der Länder je nach Infizierungsgrad
  - Tooltips mit genaueren Informationen zum jeweiligen Land
  - Tooltiplink zur Statistik (Javaskript-Schnittstelle (OnClickListener -> https://stackoverflow.com/questions/11752199/can-i-customize-the-tooltip-text-in-a-google-geochart-chart))

## Methoden

  - getMap -> Parameter: Ausnahmen von Ländern, welche ignoriert werden sollen; Rückgabe: String (HTML) der Map-Datei zum Anzeigen in einer WebView
