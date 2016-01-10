# Admin App ( Fragment ) 
***
[Sources directlink](https://github.com/SharedKnowledge/SharkOISIndoor/tree/master/adminapp/app/src/main/java)

### Allgemein  
Die App scannt nach Broadcasts von umliegenden [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Geräten. Ist in einem dieser Broadcasts eine Geo-Information gespeichert, zeigt die App die folgenden Werte auf dem Display an.   
* Latitude  
* Longitude  
* Altitude    
Ansonsten wird ein Hinweis ausgegeben, dass es sich bei den gefundenen Informationen um keine Geo-Informationen handelt.  
Des weiteren kann die App zwischen nahen und fernen [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Geräten unterscheiden. Es werden immer nur die Informationen, des am nächsten liegenden [BLE](https://en.wikipedia.org/wiki/Bluetooth_low_energy) Gerätes angezeigt und verarbeitet.  

### Berechtigungen
Die App verwendet folgende Berechtigungen:  
* [BLUETOOTH](https://developer.android.com/reference/android/Manifest.permission.html#BLUETOOTH)
* [BLUETOOTH_ADMIN](https://developer.android.com/reference/android/Manifest.permission.html#BLUETOOTH_ADMIN)
* [ACCESS_COARSE_LOCATION](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION)  
