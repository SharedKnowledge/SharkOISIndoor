# Admin App ( Fragment ) 
***
[Sources directlink](https://github.com/SharedKnowledge/SharkOISIndoor/tree/master/adminapp/app/src/main/java)

Es gibt noch nicht viel zu sagen. Die App funktioniert soweit, dass das Layout alle wichtigen Informationen bietet, die gefragt waren. Die Funktionen dahinter sind größtenteils noch nicht implementiert.  
### Implementiert
* Auswerten der Geoinformationen aus einer URL
* Auswerten der Geoinformationen aus einem String
* Scannen nach Bluetooth Geräten via einer eigenen [Broadcastreceiver](https://developer.android.com/reference/android/content/BroadcastReceiver.html) Klasse 
* Layout ( Siehe Screenshot )

### Noch nicht Implementiert
* Scannen nach Bluetooth LE Geräten 
* Erkennen von unseren Beacons
* Auslesen von Daten aus Bluetooth ( LE ) Geräten
* Speichern von Daten auf Bluetooth LE Geräten
* Implemtierung einer Sharkfunktion 

### Hinweis zur Klasse [BeaconManager](https://github.com/SharedKnowledge/SharkOISIndoor/blob/master/adminapp/app/src/main/java/bluetoothmanager/BeaconManager.java) 
Der Manager ist noch nicht fertig implementiert. Bisher kann er nur nach normalen Bluetooth Geräten suchen und diese auf Log-Ebene anzeigen lassen.  Auszug aus adb:  
> I/Beacon-Manager: Enabling  
I/Beacon-Manager: The local Bluetooth adapter has finished the device discovery process.  
I/Beacon-Manager: Discovering beacons returns: true  
I/Beacon-Manager: Name: GT-I9300 Addr: 98:52:B1:2F:72:3D 

( Suchvorgang wird erneut gestatet, um sicher zustellen, dass wir ohne Unterbrechnung alle Geräte in der Nähe sehen, bis wir die App beenden )
> I/Beacon-Manager: The local Bluetooth adapter has finished the device discovery process.  
I/Beacon-Manager: Discovering beacons returns: true  
I/Beacon-Manager: Name: GT-I9300 Addr: 98:52:B1:2F:72:3D  
I/AdminMain: Pause.  
I/AdminMain: Stop.  
I/Beacon-Manager: Aborting  

Falls die Klasse später verwendet werden soll, bitte folgendes im [App Manifest](https://developer.android.com/guide/topics/manifest/manifest-intro.html) innerhalb des `application`-Teils einfügen:  
`<receiver`  
`           android:name="bluetoothmanager.BeaconManager"`  
`           android:enabled="false" >`  
`           <intent-filter>`  
`               <action android:name="android.bluetooth.device.action.FOUND"/>`  
`               <action android:name="android.bluetooth.adapter.action.DISCOVERY_STARTED" />`  
`               <action android:name="android.bluetooth.adapter.action.DISCOVERY_FINISHED" />`  
`               <action android:name="android.bluetooth.adapter.action.STATE_CHANGED" />`  
`               <action android:name="android.bluetooth.adapter.action.REQUEST_ENABLE"/>`  
`           </intent-filter>` 
`           </receiver>` 

**Aufgrund eines Fehlers in der Supportbibliothek von Android, muss dieser zwingend in der späteren MainActivity gestartet werden. Bitte dazu die Kommentare der Klasse lesen. **

***

![alt screenshot](http://i.imgur.com/gyND5Q9.png)
