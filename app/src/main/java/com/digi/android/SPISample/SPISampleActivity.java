/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.SPISample;

import android.app.Activity;
import java.io.IOException;
import android.os.Bundle;
import android.spi.SPI;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This application demonstrates the use of the SPI API by monitoring the
 * communication with a slave device. Users can configure all the SPI parameters
 * (at the beginning they are all set to their default values):
 * <ul>
 * <li>Interface and device: All the possible interface/device combinations are
 * listed to choose one. Changing this option during normal operation resets all
 * the other parameters to their default values.</li>
 * <li>Clock polarity and phase: Each of them can be set to 0 or 1 according to
 * SPI standard definition.</li>
 * <li>Special modes ("CS High", "LSB First", "3-wire", "Loop", "No CS" and
 * "Ready"): They are all individually selectable according to SPI standard
 * definition. They might not be supported. If that is the case, the mode is
 * discarded and the console displays a message.</li>
 * <li>Bits per word: The size of each transfer word.</li>
 * <li>Speed: The maximum transfer speed in Hz.</li>
 * <li>Read length: The amount of transfer words to receive during read process.</li>
 *</ul>
 *
 * <p>The application shows two fields named "Data to send" and "Received data"
 * that correspond to data to send to the slave device and data received from
 * it, respectively. Three buttons take control of these operations:</p>
 * <ul>
 * <li>"Read data" button: Reads from the slave device the amount of words
 * specified by the _Read length_ parameter and shows them in the "Received
 * data" field. If no data has been read, this field is empty. In case of error,
 * the console displays a message.</li>
 * <li>"Transfer data" button: Writes in the slave device the data introduced in
 * the "Data to send" field while, at the same time, reads from the slave device
 * data with the same length as the written data. If no data has been read,
 * this field is empty. In case of error, the console displays a message.</li>
 * <li>"Write data" button: Writes in the slave device the data introduced in
 * the "Data to send" field. In case of error, the console displays a message.</li>
 * </ul>
 */
public class SPISampleActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {
	private static final String TAG = "SPISample";

	private final int DEFAULT_MODE = 0;           // Default mode: 0
	private final int DEFAULT_WORD_SIZE = 8;      // Default word size: 8 bits per word
	private final int DEFAULT_MAX_SPEED = 500000; // Default maximum speed: 500 KHz
	private final int DEFAULT_READ_LENGTH = 10;	  // Default number of bytes to read: 10.
	private final String SEND_DATA_HINT = "Enter data to be sent...";
	
	private int mInterface = -1;
	private int mDevice = -1;
	private Spinner interfaceSelector, clkpol, clkpha;
	private CheckBox cshigh, lsbfirst, threewire, loop, nocs, ready;
	private EditText wordsize, maxspeed, readlength, sendData, receiveData;
	private SPI mSPI;
	private boolean openedSPI = false;
	
	private int currentMode = DEFAULT_MODE;
	private int currentWordSize = DEFAULT_WORD_SIZE;
	private int currentMaxSpeed = DEFAULT_MAX_SPEED;
	private int currentReadLength = DEFAULT_READ_LENGTH;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Instance the elements from layout
        interfaceSelector = (Spinner)findViewById(R.id.interface_selector);
        clkpol = (Spinner)findViewById(R.id.CPOL_selector);
        clkpha = (Spinner)findViewById(R.id.CPHA_selector);
        cshigh = (CheckBox)findViewById(R.id.CS_HIGH);
        lsbfirst = (CheckBox)findViewById(R.id.LSB_FIRST);
        threewire = (CheckBox)findViewById(R.id.THREE_WIRE);
        loop = (CheckBox)findViewById(R.id.LOOP);
        nocs = (CheckBox)findViewById(R.id.NO_CS);
        ready = (CheckBox)findViewById(R.id.READY);
        wordsize = (EditText)findViewById(R.id.word_size);
        maxspeed = (EditText)findViewById(R.id.max_speed);
        readlength = (EditText)findViewById(R.id.read_length);
		Button readButton = (Button)findViewById(R.id.read_button);
		Button transferButton = (Button)findViewById(R.id.transfer_button);
		Button writeButton = (Button)findViewById(R.id.write_button);
        sendData = (EditText)findViewById(R.id.send_data);
        receiveData = (EditText)findViewById(R.id.receive_data);

        // Show the available interfaces in the spinner
        String[] interfaces = SPI.listInterfaces();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, interfaces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interfaceSelector.setAdapter(adapter);
        if (interfaceSelector.getItemAtPosition(0) != null) {
        	interfaceSelector.setSelection(0);
        }
        
        // Show initial values
        wordsize.setText(String.valueOf(currentWordSize));
        maxspeed.setText(String.valueOf(currentMaxSpeed));
        readlength.setText(String.valueOf(currentReadLength));
        sendData.setHint(SEND_DATA_HINT);
        receiveData.setEnabled(false);

        // Set event listeners
        interfaceSelector.setOnItemSelectedListener(this);
        clkpol.setOnItemSelectedListener(this);
        clkpha.setOnItemSelectedListener(this);
        cshigh.setOnCheckedChangeListener(this);
        lsbfirst.setOnCheckedChangeListener(this);
        threewire.setOnCheckedChangeListener(this);
        loop.setOnCheckedChangeListener(this);
        nocs.setOnCheckedChangeListener(this);
        ready.setOnCheckedChangeListener(this);
        wordsize.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void afterTextChanged(Editable s) {
				if (wordsize.getText().length() == 0)
					return;
				currentWordSize = Integer.parseInt(wordsize.getText().toString());
				Log.v(TAG, "Word size has changed to " + currentWordSize);
			}
		});
        maxspeed.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void afterTextChanged(Editable s) {
				if (maxspeed.getText().length() == 0)
					return;
				currentMaxSpeed = Integer.parseInt(maxspeed.getText().toString());
				Log.v(TAG, "Maximum speed has changed to " + currentMaxSpeed);
			}
		});
        readlength.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				if (readlength.getText().length() == 0)
					return;
				currentReadLength = Integer.parseInt(readlength.getText().toString());
				Log.v(TAG, "Read length has changed to " + currentReadLength);
			}
		});
        readButton.setOnClickListener(this);
        transferButton.setOnClickListener(this);
        writeButton.setOnClickListener(this);
                
    }

	/*public void OnDestroy() {
		super.onDestroy();
	}*/

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.read_button:
			readData();		
			break;
		case R.id.transfer_button:
			transferData();
			break;
		case R.id.write_button:
			writeData();
			break;
		}
	}

	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		int newMode = currentMode;
		switch (parent.getId()) {
		case R.id.interface_selector:
			updateInterface();	
			cleanOptions();
			break;
		case R.id.CPOL_selector:
				if (pos == 0) {
					newMode = currentMode & Integer.parseInt("11111110", 2);
				} 
				if (pos == 1) {
					newMode = currentMode | Integer.parseInt("00000001", 2);					
				} 
				try {
					if (mSPI.setMode(newMode) == 0) {
						currentMode = newMode;
						Log.v(TAG, "Clock polarization has changed: new mode is " + currentMode);						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			break;
		case R.id.CPHA_selector:
				if (pos == 0) {
					newMode = currentMode & Integer.parseInt("11111101", 2);
				} 
				if (pos == 1) {
					newMode = currentMode | Integer.parseInt("00000010", 2);
				}
				try {
					if (mSPI.setMode(newMode) == 0) {
						currentMode = newMode;
						Log.v(TAG, "Clock phase has changed: new mode is " + currentMode);						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			break;		
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int newMode = currentMode;
		switch (buttonView.getId()) {
		case R.id.CS_HIGH:
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("00000100", 2);
			} else {
				newMode = currentMode & Integer.parseInt("11111011", 2);
			}
			Log.v(TAG, "CS HIGH has changed.");
			break;
		case R.id.LSB_FIRST:
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("00001000", 2);
			} else {
				newMode = currentMode & Integer.parseInt("11110111", 2);
			}
			Log.v(TAG, "LSB FIRST has changed.");			
			break;
		case R.id.THREE_WIRE:		
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("00010000", 2);
			} else {
				newMode = currentMode & Integer.parseInt("11101111", 2);
			}
			Log.v(TAG, "3-WIRE has changed.");
			break;
		case R.id.LOOP:			
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("00100000", 2);
			} else {
				newMode = currentMode & Integer.parseInt("11011111", 2);
			}
			Log.v(TAG, "LOOP has changed.");
			break;
		case R.id.NO_CS:
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("01000000", 2);
			} else {
				newMode = currentMode & Integer.parseInt("10111111", 2);
			}
			Log.v(TAG, "NO CS has changed.");
			break;
		case R.id.READY:		
			if (isChecked) {
				newMode = currentMode | Integer.parseInt("10000000", 2);
			} else {
				newMode = currentMode & Integer.parseInt("01111111", 2);
			}
			Log.v(TAG, "READY has changed.");
			break;			
		}

		try {
			if (mSPI.setMode(newMode) == 0) {
				currentMode = newMode;
				Log.v(TAG, "New mode is " + currentMode);				
			} else {
				Log.v(TAG, "Error setting new mode: keeping previous mode.");
				Toast toast = Toast.makeText(getApplicationContext(), "Mode not supported in the current interface.", Toast.LENGTH_LONG);
				toast.show();
				buttonView.setChecked(false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateInterface() {	
		// Recognize the new interface
		String selectedInterface = interfaceSelector.getSelectedItem().toString();
		Log.v(TAG, "Selected interface is " + selectedInterface);
		int newInterface = Integer.valueOf(selectedInterface.substring(0, 5));
		int newDevice = Integer.valueOf(selectedInterface.substring(6));
		
		// Close the old interface if necessary
		if ( openedSPI && (newInterface != mInterface) && (newDevice != mInterface) ) {
			mSPI.close();
			Log.v(TAG, "Closed SPI configuration: interface = " + mInterface + " & device = " + mDevice);
		}

		// Create a new object for the new interface and open it
		if ( (newInterface != mInterface) && (newDevice != mDevice) ) {
			mInterface = newInterface;
			mDevice = newDevice;
			mSPI = new SPI(mInterface, mDevice);
			try {
				mSPI.open(DEFAULT_MODE, DEFAULT_WORD_SIZE, DEFAULT_MAX_SPEED);
				openedSPI = true;
				Log.v(TAG, "New SPI configuration: interface " + mInterface + " & device = " + mDevice);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else {
			Log.v(TAG, "Selected SPI interface is already opened.");
		}
	}
	
	private void cleanOptions() {
		// Sets all options to default after changing the interface
		clkpol.setSelection(0);
		clkpha.setSelection(0);
		cshigh.setChecked(false);
		lsbfirst.setChecked(false);
		threewire.setChecked(false);
		loop.setChecked(false);
		nocs.setChecked(false);
		ready.setChecked(false);
		currentMode = DEFAULT_MODE;
		currentWordSize = DEFAULT_WORD_SIZE;
		currentMaxSpeed = DEFAULT_MAX_SPEED;
		wordsize.setText(String.valueOf(currentWordSize));
        maxspeed.setText(String.valueOf(currentMaxSpeed));
        sendData.setText("");
        sendData.setHint(SEND_DATA_HINT);
        receiveData.setText("");
        Log.v(TAG, "Application restored to default values.");
	}
	
	private void readData() {
		try {
			byte[] rx_data = mSPI.read(currentReadLength);
			Log.v(TAG, "Data received correctly: " + currentReadLength + " bytes.");
			String str_rx_data = new String(rx_data);
			receiveData.setText(str_rx_data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void transferData() {
		byte[] rx_data;
		byte[] tx_data = sendData.getText().toString().getBytes();
		try {
			rx_data = mSPI.transfer(tx_data, currentMaxSpeed, currentWordSize);
			Log.v(TAG, "Data transferred correctly.");
			String str_rx_data = new String(rx_data);
			receiveData.setText(str_rx_data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeData() {
		byte[] tx_data = sendData.getText().toString().getBytes();
		try {
			mSPI.write(tx_data);
			Log.v(TAG, "Data sent correctly.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}