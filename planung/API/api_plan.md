# API

Hier wird  das API-Management und dessen Funktion in der Applikation vorgestellt.

# Features

  - Ruft verschiedene APIs auf, um die angefragten Daten (Infizierte, Tode, etc.) zu erhalten
  - chached (bei aktivierter Einstellung) Daten zwischen um die Abrufzeit zu verringern

## Methoden

  - getData -> Parameter: Länder, Art der angefragten Daten (Gesund, Infiziert, etc.); Rückgabe: Liste der angefragten Länder angereichert mit deren Daten
  - enableCache
  - disableCache