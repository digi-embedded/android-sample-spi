SPI Sample Application
======================

This example demonstrates the usage of the SPI API by monitoring the 
communication with a slave device. The application allows reading, 
writing and transferring data to the slave device.

Demo requeriments
-----------------

To run this example you will need:
    - One compatible device to host the application.
    - Network connection between the device and the host PC in order to
      transfer and launch the application.
    - Establish remote target connection to your Digi hardware before running
      this application.
    - An external SPI device is required to run this example. Another
      possibility is to short RX and TX lines of the SPI socket so
      what is written in the SPI interface (TX line) is read (RX line).

Demo setup
----------

Make sure the hardware is set up correctly:
    - The device is powered on.
    - The device is connected directly to the PC or to the Local 
      Area Network (LAN) by the Ethernet cable.
    - If RX and TX lines of the Development Board SPI socket are not 
	  shorted, an external SPI slave address must be connected to
      this socket.
	   
Demo run
--------

The example is already configured, so all you need to do is to build and 
launch the project.
  
Once application starts, you will be able to configure all the SPI 
parameters (although at the beginning they are all set to their default
values):
    - Interface and device: all the possible interface/device combinations 
      are listed so that you can choose one. Changing this option during
      normal operation will reset all the other parameters to their 
      default values.
    - Clock polarity and phase: each of them can be set to 0 or 1
      according to SPI standard definition.
    - Special modes ("CS High", "LSB First", "3-wire", "Loop", "No CS" 
      and "Ready"). They are all individually selectable according to SPI
      standard definition. They might not be supported. If that is the 
      case, the mode will be discarded and a message will be shown in the
      console.
    - Bits per word: the size of each transfer word.
    - Speed: the maximum transfer speed in Hz.
    - Read length: the amount of transfer words to receive during read 
      process.

The application shows two fields named "Data to send" and "Received data"
that correspond to the data to be sent to the slave device and data 
received from it, respectively. Three buttons take control of this
operations:
    - "Read data" button: Reads from the slave device the amount of words
      specified by the "Read length" parameter and shows them in the 
      "Received data" field. If no data has been read, nothing will be 
      shown in this field. In case of error, a message will be shown in 
      the console.
    - "Transfer data" button: Writes in the slave device the data 
      introduced in the "Data to send" field while, at the same time, 
      reads from the slave device data with the same length as the written
      data. If no data has been read, nothing will be shown in the
      "Received data" field. In case of error, a message will be shown in
      the console.
    - "Write data" button: Writes in the slave device the data introduced
      in the "Data to send" field. In case of error, a message will be
      shown in the console.

Tested on
---------

ConnectCore Wi-i.MX51
ConnectCore Wi-i.MX53
ConnectCore 6 Adapter Board
ConnectCore 6 SBC
ConnectCore 6 SBC v2
ConnectCore 6 SBC v2