# ZEADL extendable android data logger
A project for the embedded android module at bfh.

##Description: 
ZEADL is a Datalogger for Android Devices. It offers the possibility to measure several values and show them in a handy graph view, or a table. If necessary, the table and the graph can be safed on your device for further utilisation.

##### Contributors:
* maad
* lati
* luke

## Terminology
* SensorGroup: A group of SensorChannels with a common Unit for Y-Axis and a common Sample-Rate.
* SensorChannel: A Single Channel of a SensorGroup. Each Channel is a separate line in the diagramms

##Navigation
The App starts at the main view, we call it " Overview". Here you see an overview over all acitive Sensor(group)s. You can activate/deactivate SensorGroups by using the menu in the top left corner. Once you've activated some SensorGroups you will see their Graphs in the Overview.

To see details of a Sensor Group you can simply click on it in the Overview. This will take you to the "Detail View".

The Detail-View has three Tabs: Graph, Table and Settings. You may return from the DetailView by pressing the "back" button.

Note: The app is not fully terminated if you press the "back" button multiple times. Zeadl runs as Service (see Notificationbar) until you terminate it via Menu->Exit.

##Detail View
###Graph
At the Graph you see the actual measurements by graph. If you like to save the current view, tipe "Save Graph" in the menu on the top-right side. If you want to clear the actual view, tipe "Clear Data" in the same menu.

###Table
The next tab visualize your data in a table view. The table is built up each channel (column) and measurement time (row).
The row beginns with the time of each measurement, followed by the channels added to the measurement.

If you like to save the table for further utilisation, tipe "Save Table" in the menu on the top-right side.

###Settings
The last tab shows the settings of the channel data. It is leaded by the samplerate bar, where you are able to change the measurements per time, calculated in 1/h, 1/min or 1/sec. Left is the minimal samplerate (few measurements per time), right is the maximal samplerate (as much as possible). You can change your samplerate as you want, the software handles the setup of the samplerate. Notice: Only if you leave the bar (take your finger away from the bar/touchscreen) the samplerate will be added. This enables you to set the samplerate properly.


##Howto Install


##Howto Extend


##Tipps'n Tricks


##Known Issues


