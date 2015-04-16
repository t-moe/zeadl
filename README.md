# ZEADL extendable android data logger
A project for the embedded android module at bfh.

##Description: 
ZEADL is a Datalogger for Android Devices. It offers the possibility to measure several values and show them in a handy graph view, or a table. If necessary, the table and the graph can be saved on your device for further utilisation. It runs perfectly on the BFH-Cape combined with the development board BeagleBoneBlack.

##### Contributors:
* maaga1
* langt1
* luckk1

## Terminology
* SensorGroup: A group of SensorChannels with a common Unit for Y-Axis and a common Sample-Rate.
* SensorChannel: A Single Channel of a SensorGroup. Each Channel is a separate line in the diagramms

##Navigation
###Main View
The App starts at the main view, we call it "Overview". Here you see an overview over all active Sensor(group)s.
<img src="https://cloud.githubusercontent.com/assets/11633618/7190148/4181fba6-e484-11e4-8c0b-6312bb698713.png" width="100" />

You can activate/deactivate SensorGroups by using the menu in the top left corner. 
<img src="https://cloud.githubusercontent.com/assets/11633618/7190311/7e05f4f0-e485-11e4-984e-731a1f5e387d.png" width="100" />

<img src="https://cloud.githubusercontent.com/assets/11633618/7190975/43ea131e-e48a-11e4-9b09-1a3c627ab0b7.png" width="100" />

Once you've activated some SensorGroups you will see their Graphs in the Overview.
To see details of a Sensor Group you can simply click on it in the Overview. This will take you to the "Detail View".

The Detail-View has three Tabs: Graph, Table and Settings. You may return from the DetailView by pressing the "back" button.

Note: The app is not fully terminated if you press the "back" button multiple times. Zeadl runs as Service (see Notificationbar) until you terminate it via Menu->Exit.

###Detail View
####Graph
In the Graph you see the actual measurements by channel. You can zoom and pan the Graph.
![DetailGraph](https://cloud.githubusercontent.com/assets/11633618/7190134/388355ae-e484-11e4-9b46-b59dd39cf5d3.png" width="100" />

####Table
The next tab visualizes your data in a table view. Each channel has it's own column.
The row beginns with the time of each measurement, followed by the channels added to the measurement.
<img src="https://cloud.githubusercontent.com/assets/11633618/7190147/3fe44bc8-e484-11e4-91d9-b16ed251ad55.png" width="100" />

If you like to save the table or the Graph for further utilisation, press "Save Table" or "Save Graph" in the menu.If you want to clear the actual view, tipe "Clear Data" in the same menu.
<img src="https://cloud.githubusercontent.com/assets/11633618/7190139/3ccecff8-e484-11e4-9150-23682d21204f.png" width="100" />

####Settings
The last tab shows the settings of the channel data. The first element ist the samplerate slider, where you are able to change the measurement-time in samples/h, samples/min or samples/sec. Left is the minimal samplerate (few measurements per time), right is the maximal samplerate (as much as possible). 
<img src="https://cloud.githubusercontent.com/assets/11633618/7190141/3ea4560e-e484-11e4-85fd-30bf51477006.png" width="100" />

Note: High sample rates will generate a high load on the device. You'll run out of memory eventually.

A list of the channels is below the samplerate settings. This list contians a checkbox and a color pick for each channel.

##How to Install
There are two ways to install this app on your device; you can build and run either beaglebone or emulator build. You can switch your flavor by choosing your build variant in "Build Variants":
<img src="https://cloud.githubusercontent.com/assets/11633618/7190150/42f6b2c4-e484-11e4-9071-693b22db8575.png" width="100" />

After your choice, you can run the Android Studio Project on your device!

##How to Extend
- Subclass SensorGroup and SensorChannel.
- The sensor groups has to instantiate all your SensorChannel instances.


##Tipps'n Tricks
- Zeadl runs as service until you terminate it via Menu->Exit


##Known Issues
- High sample rates affect the responsability of the GUI.


