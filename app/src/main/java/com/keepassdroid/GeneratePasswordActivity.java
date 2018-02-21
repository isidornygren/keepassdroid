/*
 * Copyright 2010 Tolga Onbay, Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.keepassdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepass.KeePass;
import com.android.keepass.R;
import com.keepassdroid.password.PasswordGenerator;

public class GeneratePasswordActivity extends LockCloseActivity {

	public static void Launch(Activity act) {
		Intent i = new Intent(act, GeneratePasswordActivity.class);
		
		act.startActivityForResult(i, 0);
	}

	private SeekBar.OnSeekBarChangeListener lengthSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// Set the length of the password
			TextView length = (TextView) findViewById(R.id.password_length);
			length.setText(String.valueOf(progress + 8));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// Generate a new password on seekbar drop
			fillPassword();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_password);
		setResult(KeePass.EXIT_NORMAL);

		// Select length of password dependent on the seekbar
		SeekBar seekBar = (SeekBar) findViewById(R.id.lengthSeekBar);
		seekBar.setOnSeekBarChangeListener(lengthSeekBarListener);
		seekBar.setProgress(8); // Default seekbar value 8+8 = 16

		Button genPassButton = (Button) findViewById(R.id.generate_password_button);
        genPassButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fillPassword();
			}
		});
        
        Button acceptButton = (Button) findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				EditText password = (EditText) findViewById(R.id.password);
				
				Intent intent = new Intent();
				intent.putExtra("com.keepassdroid.password.generated_password", password.getText().toString());
				
				setResult(EntryEditActivity.RESULT_OK_PASSWORD_GENERATOR, intent);
				
				finish();
			}
		});
        
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				
				finish();
			}
		});
        
        // Pre-populate a password to possibly save the user a few clicks
        fillPassword();
	}
	
	private void fillPassword() {
		EditText txtPassword = (EditText) findViewById(R.id.password);
		txtPassword.setText(generatePassword());
	}
	
    public String generatePassword() {
    	String password = "";
    	
    	try {
    		int length = Integer.valueOf(((TextView) findViewById(R.id.password_length)).getText().toString());

    		((CheckBox) findViewById(R.id.cb_uppercase)).isChecked();
        	
        	PasswordGenerator generator = new PasswordGenerator(this);
       	
	    	password = generator.generatePassword(length,
	    			((CheckBox) findViewById(R.id.cb_uppercase)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_lowercase)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_digits)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_specials)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_specials)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_specials)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_specials)).isChecked(),
	    			((CheckBox) findViewById(R.id.cb_specials)).isChecked());
    	} catch (NumberFormatException e) {
    		Toast.makeText(this, R.string.error_wrong_length, Toast.LENGTH_LONG).show();
		} catch (IllegalArgumentException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
    	
    	return password;
    }
}
