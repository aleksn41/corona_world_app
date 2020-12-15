# API

Hier wird  das API-Management und dessen Funktion in der Applikation vorgestellt.

# Features

  - Ruft verschiedene APIs auf, um die angefragten Daten (Infizierte, Tode, etc.) zu erhalten
  - cached (bei aktivierter Einstellung) Daten zwischen um die Abrufzeit zu verringern

## Methoden

  - getData -> Parameter: Länder, Art der angefragten Daten (Gesund, Infiziert, etc.), Zeitraum[] (LocalDateTime); Rückgabe: Liste der angefragten Länder angereichert mit deren Daten
  - enableCache
  - disableCache
  - enableLongTermStorage
  - disableLongTermStorage
  - saveData -> Parameter: Länder, Art der angefragten Daten (Gesund, Infiziert, etc.), Zeitraum[] (LocalDateTime); Rückgabe: void (speichert Statistik im Langzeitspeicher ab)
  - deleteDataIfSaved -> Parameter: Länder, Art der angefragten Daten (Gesund, Infiziert, etc.), Zeitraum[] (LocalDateTime); Rückgabe: void (löscht einen Eintrag aus dem Langzeitspeicher, falls er existiert)
  - getSavedData -> 
  
## Aufgerufene APIs

  - https://docs.corona-api.org/#introduction
  - https://documenter.getpostman.com/view/10808728/SzS8rjbc
  - https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/dd4580c810204019a7b8eb3e0b329dd6_0/geoservice
  - https://apify.com/covid-19
  - https://github.com/javieraviles/covidAPI
  - https://rapidapi.com/api-sports/api/covid-193

# Beschreibung

### getData
Diese Funktion holt sich die angefragten Daten und speicher sich den Zeitpunkt der Anfrage (um schlau zu cachen) -> wenn ein Nutzer häufiger in kurzen Zeiträumen die gleichen Daten anfragt werden diese gecached. 

### enable/disableCache
Diese Funktionen aktivieren oder deaktivieren die Cachefunktion, wie sie eben beschrieben wurde.

### enable/disableLongTermStorage
Diese Funktionen aktivieren oder deaktivieren die Langzeitspeicherfunktionen, welche häufig angefragte Daten permanent (bzw. über den Lebenszyklus einer einzigen App-Sitzung hinaus) speichert

### saveData 
Diese Funktion speichert die Anfrage, die ihr übergeben wird im Langzeitspeicher ab. Sie wird vo außerhalb aufgerufen, wenn bestimmte Grenzen überschritten wurden.

### getSavedData
Diese Funktion holt alle gespeicherten Daten und gibt diese als Liste zurück.
