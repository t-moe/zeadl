# ZEADL extendable android data logger
A project for the embedded android module at bfh.

####Description: 
ZEADL is a Datalogger for Android Devices. It offers the possibility to measure several values and show them in a handy graph view, or a table. If necessary, the table and the graph can be safed on your device for further utilisation.

##Content:
	- Navigation
	- Channel Info
	- Settings
	- Howto Install
	- Howto Extend
	- Tipps'nTricks
	- Known Issues


##Navigation
The App starts at the main view, we call it channel overview. At this place, you can add and remove several data channels, by typing "Select Groups" in the menu (on the top-right side), where you can simply add (checked) or remove (unchecked) the signal channels.
Notice: You handle the Sensor Groups, which are determined by the genius developer team ;) More Info at "Channel Info".
Once you have added a channel, you see his graph. By tiping on it, you get directly to the Detail View of the channel. This is a completely new view, where you can switch between 3 Tabs: Graph, Table and Settings.

####Graph
At the Graph you see the actual measurements by graph. If you like to save the current view, tipe "Save Graph" in the menu on the top-right side. If you want to clear the actual view, tipe "Clear Data" in the same menu.

####Table
The next tab visualize your data in a table view. The table is built up each channel (column) and measurement time (row).
The row beginns with the time of each measurement, followed by the channels added to the measurement.

If you like to save the table for further utilisation, tipe "Save Table" in the menu on the top-right side.

####Settings
The last tab shows the settings of the channel data. It is leaded by the samplerate bar, where you are able to change the measurements per time, calculated in 1/h, 1/min or 1/sec. Left is the minimal samplerate (few measurements per time), right is the maximal samplerate (as much as possible). You can change your samplerate as you want, the software handles the setup of the samplerate. Notice: Only if you leave the bar (take your finger away from the bar/touchscreen) the samplerate will be added. This enables you to set the samplerate properly.

##Channel Info
As mentioned, you dont have single channels, in case you have channel groups, called sensor groups. This makes it easy to compare equal channels. The settings affects the whole sensor group. Also, all the channels contained in the sensor group are shown in the graph and the table.

##Settings


##Howto Install


##Howto Extend


##Tipps'n Tricks


##Known Issues


