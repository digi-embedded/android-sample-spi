SPI Sample Application
======================

This example demonstrates the usage of the SPI API by monitoring the
communication with a slave device. The application allows reading, writing and
transferring data to the slave device.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and launch
  the application.
* An external SPI device is required to run this example. Another possibility is
  to short MISO and MOSI lines of the SPI interface so what is written in the SPI
  interface (MOSI line) is read (MISO line).

Demo setup
----------

Make sure the hardware is set up correctly:

* The device is powered on.
* The device is connected directly to the PC by the micro USB cable.
* If MISO and MOSI lines of the SPI interface are not shorted, an external SPI 
  device must be connected to this interface.

Demo run
--------

The example is already configured, so all you need to do is to build and 
launch the project.
  
Once application starts, configure all the SPI parameters (at the beginning
they are all set to their default values):

* **SPI interface**: All the possible interface and device combinations are
  listed to choose one. Changing this option during normal operation resets all
  the other parameters to their default values.
* **Clock polarity and phase**: Each of them can be set to `0` or `1` according
  to SPI standard definition.
* **Chip select**: Select the behaviour of the Chip select pin (or SS Slave Select)
  They are all individually selectable according to SPI standard definition. They 
  might not be supported. If that is the case, the mode is discarded and the 
  console displays a message.
* **Bit order**: The order in which the bits are transmitted and received.
* **Word length**: The size of each transfer word. Possible values are 7, 8 or 16.
* **Clock frequency**: The maximum transfer speed in Hz.
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

* ConnectCore 6 SBC
* ConnectCore 6 SBC v3

License
-------

Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
