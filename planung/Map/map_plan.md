# Map

Hier wird die Map und deren Einbindung in die Applikation beschrieben.

# Features

  - Anzeigen einer Map durch Erstellen einer HTML-Datei mit Google Charts -> stärkere Einfärbung der Länder je nach Infizierungsgrad
  - Tooltips mit genaueren Informationen zum jeweiligen Land
  - Tooltiplink zur Statistik

## Methoden

  - getMap -> Parameter: Ausnahmen von Ländern, welche ignoriert werden sollen; Rückgabe: String (HTML) der Map-Datei zum Anzeigen in einer WebView
