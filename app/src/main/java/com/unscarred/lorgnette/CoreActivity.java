package com.unscarred.lorgnette;

import java.io.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.LinearLayout;

import net.bgreco.DirectoryPicker;

public class CoreActivity extends Activity
{
	public          Context     Base_Context;
	public  static  CoreClass   Core;


	@Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

	    setContentView(R.layout.main);

	    LinearLayout Main_Layout = (LinearLayout)findViewById(R.id.main_layout);
	    WebView Loader_Img = (WebView)findViewById(R.id.parent_web_view);

	    Loader_Img.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	    Loader_Img.loadUrl("file:///android_asset/loader_dialog/loader_dialog.html");
	    Loader_Img.setBackgroundColor(Color.parseColor("#00000000"));

	    Base_Context = getApplicationContext();
	    Core = new CoreClass(Base_Context, Main_Layout, Activity_Handler);

	    Core.Show_Preview_Layout();
    }

	@Override public void onResume()
	{
		super.onResume();

		if(Core.Camera_Handler.Camera_Deprecated.Camera_Device == null)
			Core.Camera_Handler.Camera_Deprecated.Init_Camera_Deprecated();
	}

	@Override protected void onPause()
	{
		super.onPause();

		if(Core.Camera_Handler.Camera_Deprecated.Camera_Device != null)
			Core.Camera_Handler.Camera_Deprecated.Release_Camera_Deprecated();
	}

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Core.File_Handler.Destination_Folder = (String)extras.get(DirectoryPicker.CHOSEN_DIRECTORY);

            try
            {
                FileOutputStream fos = new FileOutputStream(CoreActivity.this.getFilesDir().getPath() + "/settings");
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                osw.write(Core.File_Handler.Destination_Folder);
                osw.flush();
                osw.close();
                fos.close();
            }
            catch(FileNotFoundException e)
            {
                System.err.println("Error in CoreActivity: onActivityResult:");
                System.err.println("Caught FileNotFoundException: " + e.getMessage());
            }
            catch(IOException e)
            {
	            System.err.println("Error in CoreActivity: onActivityResult:");
                System.err.println("Caught IOException: " + e.getMessage());
            }

	        Core.Set_Settings_Location_Text_View();
        }
    }

	public Handler Activity_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			switch(message.getData().getInt("action"))
			{
				case 0:
					Intent intent = new Intent(Base_Context, DirectoryPicker.class);
					intent.putExtra(DirectoryPicker.ONLY_DIRS, true);
					intent.putExtra(DirectoryPicker.START_DIR, "/storage/");

					startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
					break;
			}
		}
	};
}