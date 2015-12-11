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

package com.digi.android.spi;

import android.app.Activity;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.spi.SPI;
import android.spi.SPIManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.NoSuchInterfaceException;
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
 * SPI sample application.
 *
 * <p>This example demonstrates the usage of the SPI API by monitoring the
 * communication with a slave device. The application allows reading, writing and
 * transferring data to the slave device.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class SPISampleActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {

	// Constants.
	private static final String TAG = "SPISample";

	private final int DEFAULT_MODE = 0;				// Default mode: 0
	private final int DEFAULT_WORD_SIZE = 8;		// Default word size: 8 bits per word
	private final int DEFAULT_MAX_SPEED = 500000;	// Default maximum speed: 500 KHz
	private final int DEFAULT_READ_LENGTH = 10;		// Default number of bytes to read: 10.
	private final String SEND_DATA_HINT = "Enter data to be sent...";

	// Variables.
	private int mInterface = -1;
	private int mDevice = -1;
	private Spinner interfaceSelector, clkpol, clkpha;
	private CheckBox cshigh, lsbfirst, threewire, loop, nocs, ready;
	private EditText wordsize, maxspeed, readlength, sendData, receiveData;
	private SPIManager spiManager;
	private SPI mSPI;
	private boolean isSPIopen = false;

	private int currentMode = DEFAULT_MODE;
	private int currentWordSize = DEFAULT_WORD_SIZE;
	private int currentMaxSpeed = DEFAULT_MAX_SPEED;
	private int currentReadLength = DEFAULT_READ_LENGTH;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get the SPI manager.
		spiManager = (SPIManager) getSystemService(SPI_SERVICE);

		// Instantiate the elements from layout.
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

		// Show the available interfaces in the spinner.
		String[] interfaces = spiManager.listInterfaces();
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, interfaces);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		interfaceSelector.setAdapter(adapter);
		if (interfaceSelector.getItemAtPosition(0) != null) {
			interfaceSelector.setSelection(0);
		}
		
		// Show initial values.
		wordsize.setText(String.valueOf(currentWordSize));
		maxspeed.setText(String.valueOf(currentMaxSpeed));
		readlength.setText(String.valueOf(currentReadLength));
		sendData.setHint(SEND_DATA_HINT);
		receiveData.setEnabled(false);

		// Set event listeners.
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
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if (wordsize.getText().length() == 0)
					return;
				currentWordSize = Integer.parseInt(wordsize.getText().toString());
				Log.v(TAG, "Word size has changed to " + currentWordSize);
			}
		});
		maxspeed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if (maxspeed.getText().length() == 0)
					return;
				currentMaxSpeed = Integer.parseInt(maxspeed.getText().toString());
				Log.v(TAG, "Maximum speed has changed to " + currentMaxSpeed);
			}
		});
		readlength.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
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

	@Override
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

	@Override
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
					mSPI.setMode(newMode);
					currentMode = newMode;
					Log.v(TAG, "Clock polarization has changed: new mode is " + currentMode);
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
					mSPI.setMode(newMode);
					currentMode = newMode;
					Log.v(TAG, "Clock phase has changed: new mode is " + currentMode);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
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
			mSPI.setMode(newMode);
			currentMode = newMode;
			Log.v(TAG, "New mode is " + currentMode);
		} catch (IOException e) {
			e.printStackTrace();
			Log.v(TAG, "Error setting new mode: keeping previous mode.");
			Toast toast = Toast.makeText(getApplicationContext(), "Mode not supported in the current interface.", Toast.LENGTH_LONG);
			toast.show();
			buttonView.setChecked(false);
		}
	}
	
	private void updateInterface() {
		// Recognize the new interface.
		String selectedInterface = interfaceSelector.getSelectedItem().toString();
		Log.v(TAG, "Selected interface is " + selectedInterface);
		int newInterface = Integer.valueOf(selectedInterface.split("\\.")[0]);
		int newDevice = Integer.valueOf(selectedInterface.split("\\.")[1]);
		
		// Close the old interface if necessary.
		if (isSPIopen && (newInterface != mInterface) && (newDevice != mInterface)) {
			try {
				mSPI.close();
				Log.v(TAG, "Closed SPI configuration: interface = " + mInterface + " & device = " + mDevice);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Create a new object for the new interface and open it.
		if ((newInterface != mInterface) && (newDevice != mDevice)) {
			mInterface = newInterface;
			mDevice = newDevice;
			mSPI = spiManager.createSPI(mInterface, mDevice);
			try {
				mSPI.open(DEFAULT_MODE, DEFAULT_WORD_SIZE, DEFAULT_MAX_SPEED);
				isSPIopen = true;
				Log.v(TAG, "New SPI configuration: interface " + mInterface + " & device = " + mDevice);
			} catch (IOException  |NoSuchInterfaceException e) {
				Toast.makeText(this, "Error opening interface: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} else {
			Log.v(TAG, "Selected SPI interface is already open.");
		}
	}
	
	private void cleanOptions() {
		// Sets all options to default after changing the interface.
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
			byte[] rxData = mSPI.read(currentReadLength);
			Log.v(TAG, "Data received correctly: " + currentReadLength + " bytes.");
			receiveData.setText(new String(rxData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void transferData() {
		byte[] rxData;
		byte[] txData = sendData.getText().toString().getBytes();
		try {
			rxData = mSPI.transfer(txData, currentMaxSpeed, currentWordSize);
			Log.v(TAG, "Data transferred correctly.");
			receiveData.setText(new String(rxData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeData() {
		byte[] txData = sendData.getText().toString().getBytes();
		try {
			mSPI.write(txData);
			Log.v(TAG, "Data sent correctly.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}