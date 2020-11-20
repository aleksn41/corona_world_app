# UI Design
Hier wird die UI der App beschrieben.

Die App nutzt eine Activity namens main_activity, die mehrere Fragments anzeigt.

Innerhalb der main_activity werden die drei Hauptmodule:

- **Weltkarte**
- **Statistik**
- **Verlauf**

angezeigt, wobei das jeweilige Modul über eine **Navigationsleiste** ausgewählt werden kann.
Oben rechts im Bildschirm können die **Einstellungen** der App aufgerufen werden.

# Konzeption der main_activity
Die main_activity wird über eine Navigationsleiste an der unteren Hälfte des Bildschirms gesteuert.
In der Mitte des Bildschirm wird das jeweilige Modul angezeigt, was ausgewählt wurde.
Dies ist das Design der main_aktivity:
![alt text](https://github.com/aleksn41/corona_world_app/raw/ui/planung/Dark%20Mode%20Hauptbildschirm.png =250x)

Alle Sketches/Bilder können unter [diesem Link](/planung) gefunden werden.

# Modul Weltkarte
Die Weltkarte wird in einem Webview über Geochart in einer Heatmap einzeigen, welche Länder wie viel Corona haben.
Dabei wird für jedes Land ein Tooltip erstellt, welches beim Anklicken des Landes mehr Informationen zur Coronalage des Landes anzeigt, und auf das Statistik-Modul für mehr Information über einen Link verweist.
Unten rechts wird noch eine kleine Gesamtübersicht über die Coronalage innerhalb der ganzen Welt angezeigt.

# Modul Statistik
Im dem Modul Statistik sollen Statistiken über die Coronalage angefertigt werden können, die der User sich selbst auswählt.
Die Auswahl der Statistik wird über mehrere Dropdowns ermöglicht, die Auswahlmöglichkeiten wie "Land", "Statistik-Typ", etc. anbieten. Der User kann nun die Auswahl favorisieren, sodass er diese nicht mehrmals eingeben muss, falls er diese Statistik öfters betrachtet, oder sich die Statistik mit der angegeben Auswahl zeigen lassen. Falls er sich das zeigen lassen will, wird die Statisik geöffnet.

# Modul Verlauf
In dem Modul Verlauf werden die Favoriten aus der Statistik-Auswahl angezeigt und alle Statistik-Auswahlen die der User ausgewählt hat (es ist begrenzt wie viele angezeigt bzw. gespeichert werden). Dieser können ausgewählt werden, um sie zu löschen oder um sie in der Statistik anzuzeigen.

#Navigation
Die Navigation sorgt dafür, dass der User durch die App navigieren kann.
Um die Hauptmodule zu erreichen, kann der Nutzer die Navigationsleiste unten am Bildschirm nutzen.
Um die Einstellungen zu öffnen, kann der Nutzer die 3 Punkte oben rechts anklicken.
Der Nutzer ist in der Lage Mithilfe der Zurücktaste das anzuzeigen, was er vorher getan hat.

#Einstellungen
Zeigt eine Liste von Einstellungen, die angeklickt werden können (steht noch nicht fest welche diese sind)

#Verschiedene Größen von Layouts:
Layout wird an die Große des Handys angepasst. Genau Pläne stehen noch nicht fest.

