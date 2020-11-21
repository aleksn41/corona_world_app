# Guide to Entwickeln

Hier wird beschrieben, wie das Projekt "Corona_World_App" entwickelt werden soll.

# Bereiche

Jeder Große Bereich der für die App benötigt wird, wobei die Bereich aus der *plan.svg* entnommen werden können, werden in einem eigenem Branch entwickelt.

## Planung eines Bereich

Jeder Bereich besitzt einen Plan der wie Folgt gegliedert ist:
Jede Aufgabe soll in ein größeres Modul gepackt werden, wobei die Module den Bereich definieren. Jedes Modul kann in eine von 3 Status kategorisiert werden:

- Not Functional
- Bare Bones
- Finished

In jeder dieser Kategorien muss jedes Modul eine Priorität erhalten, nach der die einzelnen Aufgaben in dem Modul abgearbeitet werden.

Es muss außerdem vor Beginn der Programmierung definiert werden, wann ein Modul in einem der 3 Status ist.
## LifeCycle eines Bereichs

Innerhalb eines Bereichs werden die Module wie folgt abgearbeitet:
Zuerst werden alle Module bearbeitet die im Status "Not Functional" sind, wobei das Modul mit der höchsten Priorität zuerst abgearbeitet wird. Dasselbe bei dem Status "Bare Bones". Ein Bereich ist Fertig, wenn all seine Module den Status "Finished" haben.
## Programmieren

Bei der Programmierung eines Moduls muss **vorher** aufgeschrieben werden, welche Funktionen das Modul hat und wie man diese Testen kann.

## Änderung des Status eines Moduls

Der Status eines Moduls kann erst geändert werden, wenn folgende Bedingungen erfüllt sind:

- Das Modul entspricht der Definition des Status
- Alle vorher definierten Tests wurden implementiert und ausgeführt
- Der Merge in dem Main Branch wurde vom anderen Teammitglied überprüft und zugelassen

## Zeitmangel
Sollte Zeitmangel entstehen, dürfen Tests von Modulen entfallen und in besonderen Fällen auch Module nur im Status "Bare Bones" bleiben, solange es die Funktionalität der App nicht zu sehr beeinträchtigt.

## Planung eines Bereichs
Die Module innerhalb eines Bereichs werden vom Bereichsleiter geplant und vom Kollegen des Projekts **vor** Bearbeitung der Module genehmigt.
