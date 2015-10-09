SPI Sample Application
======================

This example demonstrates the usage of the SPI API by monitoring the
communication with a slave device. The application allows reading, writing and
transferring data to the slave device.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and launch the application.
* Establish remote target connection to your Digi hardware before running this
  application.
* An external SPI device is required to run this example. Another possibility is
  to short RX and TX lines of the SPI socket so what is written in the SPI
  interface (TX line) is read (RX line).

Demo setup
----------

Make sure the hardware is set up correctly:

* The device is powered on.
* The device is connected directly to the PC by the micro USB cable.
* If RX and TX lines of the Development Board SPI socket are not shorted, an
  external SPI slave address must be connected to this socket.
	   
Demo run
--------

The example is already configured, so all you need to do is to build and 
launch the project.
  
Once application starts, configure all the SPI parameters (at the beginning
they are all set to their default values):

* **Interface and device**: All the possible interface/device combinations are
  listed to choose one. Changing this option during normal operation resets all
  the other parameters to their default values.
* **Clock polarity and phase**: Each of them can be set to `0` or `1` according
  to SPI standard definition.
* **Special modes** (_CS High_, _LSB First_, _3-wire_, _Loop_, _No CS_, and
  _Ready_): They are all individually selectable according to SPI standard
  definition. They might not be supported. If that is the case, the mode is
  discarded and the console displays a message.
* **Bits per word**: The size of each transfer word.
* **Speed**: The maximum transfer speed in Hz.
* **Read length**: The amount of transfer words to receive during read process.

The application shows two fields named **Data to send** and **Received data**
that correspond to the data to send to the slave device and the data received
from it, respectively. Three buttons take control of these operations:

* **Read data** button: Reads from the slave device the amount of words
  specified by the _Read length_ parameter and shows them in the _Received data_
  field. If no data has been read, this field is empty. In case of error, the
  console displays a message.
* **Transfer data** button: Writes in the slave device the data introduced in
  the _Data to send_ field while, at the same time, reads from the slave device
  data with the same length as the written data. If no data has been read,
  this field is empty. In case of error, the console displays a message.
* **Write data** button: Writes in the slave device the data introduced in the
  _Data to send_ field. In case of error, the console displays a message.

Compatible with
---------------

* ConnectCore Wi-i.MX51
* ConnectCore Wi-i.MX53
* ConnectCore 6 Adapter Board
* ConnectCore 6 SBC
* ConnectCore 6 SBC v2
* ConnectCore 6 SBC v2

License
---------

This software is open-source software. Copyright Digi International, 2014-2015.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, you can obtain
one at http://mozilla.org/MPL/2.0/.