/**
 * Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.spi;

import android.app.Activity;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.digi.android.spi.SPI;
import com.digi.android.spi.SPIBitOrder;
import com.digi.android.spi.SPIChipSelect;
import com.digi.android.spi.SPIClockMode;
import com.digi.android.spi.SPIConfig;
import com.digi.android.spi.SPIManager;
import com.digi.android.util.NoSuchInterfaceException;

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
public class SPISampleActivity extends Activity implements OnClickListener {

	// Constants.
	private static final String TAG = "SPISample";

	private static final int DEFAULT_WORD_LENGTH = 8;			// Default word length: 8 bits per word
	private static final int DEFAULT_CLOCK_FREQUENCY = 500000;	// Default maximum speed: 500 KHz
	private static final int DEFAULT_READ_LENGTH = 10;			// Default number of bytes to read: 10.

	private static final String SEND_DATA_HINT = "Enter data to be sent...";

	// Variables.
	private Spinner interfaceSelector, clockMode, chipSelect, bitOrder;
	private EditText wordLength, clockFrequency, readLength, sendData, receiveData;
	private Button openButton, closeButton, readButton, writeButton, transferButton;

	private SPIManager spiManager;
	private SPI mSPI;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get the SPI manager.
		spiManager = new SPIManager(this);

		// Instantiate the elements from layout.
		interfaceSelector = (Spinner)findViewById(R.id.interface_selector);
		clockMode = (Spinner)findViewById(R.id.clock_mode);
		chipSelect = (Spinner)findViewById(R.id.chip_select);
		bitOrder = (Spinner)findViewById(R.id.bit_order);
		wordLength = (EditText)findViewById(R.id.word_length);
		clockFrequency = (EditText)findViewById(R.id.clock_frequency);
		readLength = (EditText)findViewById(R.id.read_length);
		openButton = (Button)findViewById(R.id.open_button);
		closeButton = (Button)findViewById(R.id.close_button);
		readButton = (Button)findViewById(R.id.read_button);
		transferButton = (Button)findViewById(R.id.transfer_button);
		writeButton = (Button)findViewById(R.id.write_button);
		sendData = (EditText)findViewById(R.id.send_data);
		receiveData = (EditText)findViewById(R.id.receive_data);

		// Show the available interfaces in the spinner.
		ArrayList<String> interfaces = new ArrayList<String>();
		int[] nInterfaces = spiManager.listInterfaces();
		for (int i = 0; i < nInterfaces.length; i++) {
			int[] devices = spiManager.listSlaveDevices(nInterfaces[i]);
			for (int j = 0; j < devices.length; j++)
				interfaces.add(String.format("%d.%d", nInterfaces[i], devices[j]));
		}

		interfaceSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
				interfaces.toArray(new String[interfaces.size()])));
		if (interfaceSelector.getItemAtPosition(0) != null) {
			interfaceSelector.setSelection(0);
		}

		// Show the available clock modes in the spinner.
		clockMode.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPIClockMode.values()));

		// Show the available chip select levels in the spinner.
		chipSelect.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPIChipSelect.values()));

		// Show the available bit orders in the spinner.
		bitOrder.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPIBitOrder.values()));
		
		// Show initial values.
		wordLength.setText(String.valueOf(DEFAULT_WORD_LENGTH));
		clockFrequency.setText(String.valueOf(DEFAULT_CLOCK_FREQUENCY));
		readLength.setText(String.valueOf(DEFAULT_READ_LENGTH));
		sendData.setHint(SEND_DATA_HINT);
		receiveData.setEnabled(false);

		// Set event listeners.
		openButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		readButton.setOnClickListener(this);
		transferButton.setOnClickListener(this);
		writeButton.setOnClickListener(this);

		// Hide the keyboard on startup.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.open_button:
				openInterface();
				break;
			case R.id.close_button:
				closeInterface();
				break;
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

	private void openInterface() {
		try {
			// Recognize the new interface.
			String selectedInterface = interfaceSelector.getSelectedItem().toString();
			int newInterface = Integer.valueOf(selectedInterface.split("\\.")[0]);
			int newDevice = Integer.valueOf(selectedInterface.split("\\.")[1]);

			// Close the old interface if necessary.
			if (mSPI != null && mSPI.isInterfaceOpen())
				mSPI.close();

			// Create a new object for the new interface and open it.
			mSPI = spiManager.createSPI(newInterface, newDevice);
			mSPI.open(new SPIConfig((SPIClockMode) clockMode.getSelectedItem(),
					(SPIChipSelect) chipSelect.getSelectedItem(), (SPIBitOrder) bitOrder.getSelectedItem(),
					Integer.parseInt(clockFrequency.getText().toString()), Integer.parseInt(wordLength.getText().toString())));
			openButton.setEnabled(false);
			closeButton.setEnabled(true);
			readButton.setEnabled(true);
			writeButton.setEnabled(true);
			transferButton.setEnabled(true);
			interfaceSelector.setEnabled(false);
			clockMode.setEnabled(false);
			chipSelect.setEnabled(false);
			bitOrder.setEnabled(false);
			wordLength.setEnabled(false);
			clockFrequency.setEnabled(false);
			readLength.setEnabled(false);
		} catch (NoSuchInterfaceException | IOException e) {
			Toast.makeText(this, "Error opening interface: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void closeInterface() {
		try {
			if (mSPI != null && mSPI.isInterfaceOpen())
				mSPI.close();
			openButton.setEnabled(true);
			closeButton.setEnabled(false);
			readButton.setEnabled(false);
			writeButton.setEnabled(false);
			transferButton.setEnabled(false);
			interfaceSelector.setEnabled(true);
			clockMode.setEnabled(true);
			chipSelect.setEnabled(true);
			bitOrder.setEnabled(true);
			wordLength.setEnabled(true);
			clockFrequency.setEnabled(true);
			readLength.setEnabled(true);
		} catch (IOException e) {
			Toast.makeText(this, "Error closing interface: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private void readData() {
		try {
			byte[] rxData = mSPI.read(Integer.parseInt(readLength.getText().toString()));
			Log.v(TAG, "Data received correctly: " + Integer.parseInt(readLength.getText().toString()) + " bytes.");
			receiveData.setText(new String(rxData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void transferData() {
		byte[] rxData;
		byte[] txData = sendData.getText().toString().getBytes();
		try {
			rxData = mSPI.transfer(txData);
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