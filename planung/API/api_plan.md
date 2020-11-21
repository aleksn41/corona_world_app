# API

Hier wird  das API-Management und dessen Funktion in der Applikation vorgestellt.

# Features

  - Ruft verschiedene APIs auf, um die angefragten Daten (Infizierte, Tode, etc.) zu erhalten
  - cached (bei aktivierter Einstellung) Daten zwischen um die Abrufzeit zu verringern

## Methoden

  - getData -> Parameter: Länder, Art der angefragten Daten (Gesund, Infiziert, etc.), Zeitraum[] (LocalDateTime); Rückgabe: Liste der angefragten Länder angereichert mit deren Daten
  - enableCache
  - disableCache
  
## Aufgerufene APIs

  - https://docs.corona-api.org/#introduction
  - https://documenter.getpostman.com/view/10808728/SzS8rjbc
  - https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/dd4580c810204019a7b8eb3e0b329dd6_0/geoservice
  - https://apify.com/covid-19
  - https://github.com/javieraviles/covidAPI
  - https://rapidapi.com/api-sports/api/covid-193
