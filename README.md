# ZEADL extendable android data logger
A project for the embedded android module at bfh.

##Description: 
ZEADL is a Datalogger for Android Devices. It offers the possibility to measure several values and show them in a handy graph view, or a table. If necessary, the table and the graph can be saved on your device for further utilisation.

##### Contributors:
* maad
* lati
* luke

## Terminology
* SensorGroup: A group of SensorChannels with a common Unit for Y-Axis and a common Sample-Rate.
* SensorChannel: A Single Channel of a SensorGroup. Each Channel is a separate line in the diagramms

##Navigation
The App starts at the main view, we call it "Overview". Here you see an overview over all active Sensor(group)s. You can activate/deactivate SensorGroups by using the menu in the top left corner. Once you've activated some SensorGroups you will see their Graphs in the Overview.

To see details of a Sensor Group you can simply click on it in the Overview. This will take you to the "Detail View".

The Detail-View has three Tabs: Graph, Table and Settings. You may return from the DetailView by pressing the "back" button.

Note: The app is not fully terminated if you press the "back" button multiple times. Zeadl runs as Service (see Notificationbar) until you terminate it via Menu->Exit.

##Detail View
###Graph
At the Graph you see the actual measurements by graph. If you like to save the current view, press "Save Graph" in the menu in the top right corner. If you want to clear the actual view, tipe "Clear Data" in the same menu.

###Table
The next tab visualizes your data in a table view. Each channel has it's own column.
The row beginns with the time of each measurement, followed by the channels added to the measurement.

If you like to save the table for further utilisation, press "Save Table" in the menu.

###Settings
The last tab shows the settings of the channel data. The first element ist the samplerate slider, where you are able to change the measurement-time in samples/h, samples/min or samples/sec. Left is the minimal samplerate (few measurements per time), right is the maximal samplerate (as much as possible). 

Note: High sample rates will generate a high load on the device. You'll run out of memory eventually.

##How to Install
- Beagle bone: Use the provided apk
- Emulator: Uncomment jni build.gradle file in the app folder. Uncomment all SensorChannels that use the I2C class. Alternative: Use the ARM-Emulator which supports our jni libs.

##How to Extend
- Sublass SensorGroup and SensorChannel.
- The sensor groups has to instantiate all your SensorChannel instances.


##Tipps'n Tricks
- Zeadl runs as service until you terminate it via Menu->Exit


##Known Issues
- High sample rates affect the resposabilty of the GUI.


