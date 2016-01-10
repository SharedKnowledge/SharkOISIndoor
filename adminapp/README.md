# Admin App ( Fragment ) 
* Allgemein
* Berechtigungen
* Benutzung
* Was noch fehlt
  
***
[Sources directlink](https://github.com/SharedKnowledge/SharkOISIndoor/tree/master/adminapp/app/src/main/java)

## Allgemein  
Die App scannt nach Broadcasts von umliegenden [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Geräten. Ist in einem dieser Broadcasts eine Geo-Information gespeichert, zeigt die App die folgenden Werte auf dem Display an.   
* Latitude  
* Longitude  
* Altitude    

Ansonsten wird ein Hinweis ausgegeben, dass es sich bei den gefundenen Informationen um keine Geo-Informationen handelt. Des weiteren kann die App zwischen nahen und fernen [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Geräten unterscheiden. Es werden immer nur die Informationen, des am nächsten liegenden [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Gerätes angezeigt und verarbeitet.  
Generell wird auch die verwendete Android-Version erkannt, auf der die Adminapp(Fragment) läuft und auf entsprechende Versionsunterschiede im Bereich Scanning und Berechtigungen Rücksicht genommen. 

## Berechtigungen
Die App verwendet folgende Berechtigungen:  
* [BLUETOOTH](https://developer.android.com/reference/android/Manifest.permission.html#BLUETOOTH)
* [BLUETOOTH_ADMIN](https://developer.android.com/reference/android/Manifest.permission.html#BLUETOOTH_ADMIN)
* [ACCESS_COARSE_LOCATION](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION)
* [ACCESS_WIFI_STATE](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_WIFI_STATE)
* [ACCESS_NETWORK_STATE](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_NETWORK_STATE)


Ist die App auf einem Gerät mit Android Marshmellow installiert, wird das neue Berechtigungssystem verwendet. Das bedeutet, dass der Nutzer spätestens, wenn er einen Scan zum ersten Mal starten will, vom System gefragt wird, ob die Adminapp auf den Standort ( [ACCESS_COARSE_LOCATION](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION) ) zugreifen darf. Er kann das ablehen oder er stimmt zu. Lehnt er ab, kann kein Scan gestartet werden. 

## Benutzung

### Erster Start der App
![alt tag](http://i.imgur.com/2eU6VAK.png) ![alt tag](http://i.imgur.com/6s9A3GY.png) ![alt tag](http://i.imgur.com/PcSRuHG.png)  
Ab Android Marshmellow, wird beim ersten Start der App ein Hinweis auf das  Verwenden von GPS-Daten gegeben. Das Berechtigungssystem ab Android Marshmellow weist den Benutzer darauf hin und lässt ihn entscheiden, ob die Adminapp die Berechtigung erhalten darf oder nicht. Wird eine frühere Version verwendet, dann wird dieser Hinweis nicht gegeben, da die App die Berechtigung mit der Installation erhalten hat.  
  
Sollte der Benutzer Android Marshmellow verwenden und seine Standpunktortung deaktiviert haben, erhält er einen Hinweis darauf, diesen anzuschalten. Bestätigt er dies, gelangt er in die Systemeinstellungen, wo er manuell den Service aktivieren muss. Es ist nicht möglich unter Android diesen Vorgang zu automatisieren. Lehnt er die manuelle Aktivierung ab, kann kein Scan gestartet werden.  
  
Im Anschluss, sollte der Benutzer sein Bluetooth deaktiviert haben, erhält er einen Hinweis darauf, dass sein Bluetooth derzeit deaktiviert ist. Es ist ihm möglich aus dem Dialogfenster heraus, das Bluetooth "sicher" zu aktivieren. Das bedeutet, dass der Programmierer das Androidsystem die Aktivierung vornehmen lässt. Lehnt er die Aktivierung ab, kann kein Scan gestartet werden.  

Generell werden diese drei Statusuntersuchungen bei jedem Scanstart unternommen.  
### Einen Scan starten und stoppen
![alt tag](http://i.imgur.com/niEz7vc.png) ![alt tag](http://i.imgur.com/PTUNlYY.png)  
Einen Scan kann man starten, wenn folgende Dinge erfüllt sind:  
* Bluetooth ist aktiviert
* Standpunktortung ist aktiviert ( Ab Android Marshmellow )
* Standpunktordung-Berechtigung erhalten ( Ab Android Marshmellow )  
  
Sind diese ein oder drei Dinge erfüllt, muss nur auf das graue Bluetooth-Symbol in der oberen Mitte der App getappt werden. Nach Aktivierung des Scans, färbt sich das Symbol blau.  
Deaktiviert werden kann der Scan, in dem erneut auf das Bluetooth-Symbol geklickt wird.  
  
Der Scan nach Geräten ist ein ziemlich stromintensiver Vorgang und sollte sobald er nicht mehr benötigt wird wieder deaktiviert werden. Der Scan wird automatisch unterbrochen wenn:  
* Ein Shark-Upload statt findet
* Die App in den Hintergrund verschoben wird
* Die Systemeinstellungen zur Standortermittlung aufgerufen werden ( Ab Android Marshmellow )
  
Der Scan wird automatischer wieder gestartet wenn: 
* Die App aus dem Hintergrund wieder hervorgerufen wird und ein Scan vor dem Minimieren statt fand
  
### Informationen an Shark senden
![alt tag](http://i.imgur.com/y7xAOr1.png) ![alt tag](http://i.imgur.com/H6Jxw3W.png) ![alt tag](http://i.imgur.com/74iFDCa.png)
Informationen können an Shark gesenden werden, wenn folgende Dinge erfüllt sind: 
* Ein Scan wurde gestartet 
* Es besteht eine Verbingung zu einem Access Point ( Internet )
* Es wurden Geo-Informationen in einem Broadcast gefunden
* Es wurde ein Thema vergeben
* Es wurde ein Autor vergeben
* Es wurde ein Inhalt vergeben  


Ist einer der letzten vier Dinge nicht erfüllt, wird an entsprechender Stelle ein Warnhinweis gegeben. Der Benutzer kann diesen anklicken und weitere informationen dazu erhalten. Die Warnhinweise im oberen Bereich der App, dort wo die Geo-Information der Beacons angezeigt werden, verschwinden sobald die ersten Daten eingetroffen sind.  
Infromationen, was alles an Shark gesendet wird, erhält der Benutzer durch Tappen auf die drei Punkte im obren rechten Bereich und durch das Wählen von "Information zur App".  
Hinweis zur Internetverbindung: Es wird zwar geprüft ob das Smartphone mit einem Access Point verbunden ist, jedoch nicht ob dieser auch Uploads zulässt.

## Was noch fehlt
### Blesh-Beacons sind nicht sehr geeignet
Mit den getesteten (Blesh)Beacons ist es nur möglich Geo-Informationen mit insgesammt 10 Zeichen zu speichern. Es muss eine Funktion implementiert werden, die es ermöglicht durch Umrechnung in ein Zahlensystem zur Basis 256 diese Hürde zu umgehen. Des weiteren wird ABGERATEN die Blesh-App zur Beschriftung der Beacons zu verwenden, da diese sobald sie Daten zum Beschreiben der Beacons erhält diese direkt in eine [Google Short URL](https://goo.gl/) zu konvertieren versucht. 
Eine genauere Erläuterung zu dem Problem finden Sie hier: [PASTEBIN-Chatauszug der Entwickler](http://pastebin.com/56wYsa1j)  
Wird eine andere App zur Beschriftung der Blesh-Beacons verwendet, dann versagt die Blesh-App beim lesen des Beacon-Inhaltes und stürzt ab. 

### Sharkupload hinzufügen
Momentan gibt es noch keine fertige Uploadfunktion, um die ausgelesen Beacon-Informationen an Shark zu senden. Die derzeit asynchron implementierte Version des Uploaders zählt zu Testzwecken von -100000 bis 1000000 hoch um eine "schwere Arbeit" zu simulieren. 
