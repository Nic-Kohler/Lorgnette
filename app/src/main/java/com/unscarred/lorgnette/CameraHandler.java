package com.unscarred.lorgnette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


class CameraHandler
{
	private Context                 Base_Context;
	private CoreClass               Core;

	public  CameraDeprecated        Camera_Deprecated;

	public  boolean                 Is_Portrait;
	public  boolean                 Is_Front_Facing = false;
	public  boolean                 Take_Picture_On_Focus = false;
	public  boolean                 Has_AirView = false;
	public  int                     Camera_Orientation_Degrees = 0;
	public  int                     Flash_Mode_Index = 0;


	CameraHandler(Context context, CoreClass core)
	{
		Base_Context = context;
		Core = core;

		Camera_Deprecated = new CameraDeprecated(Base_Context, Core);
		//Camera_Deprecated.Init_Camera_Deprecated();
	}

	public void Take_Picture()
	{
		Log.d("Nic Says", "*** PICTURE WAS TAKEN ***");

		Camera_Deprecated.Camera_Device.takePicture(Camera_Deprecated.Shutter_Callback,
		                                            Camera_Deprecated.Raw_Picture_Callback,
		                                            Camera_Deprecated.Picture_Callback);
	}


	public void Get_Supported_Focus_Modes()
	{
		Camera.Parameters camera_parameters = Camera_Deprecated.Camera_Device.getParameters();
		List<String> supported_focus_modes = camera_parameters.getSupportedFocusModes();
		String camera_direction;

		if(Is_Front_Facing)
			camera_direction = "Front";
		else
			camera_direction = "Back";

		Log.d("Nic Says", "=== Supported Focus Modes for " + camera_direction + " Camera ===");

		for(int i = 0; i < supported_focus_modes.size(); i++)
			Log.d("Nic Says", "=== Mode " + (i + 1) + ": " + supported_focus_modes.get(i));

		Log.d("Nic Says", "==========================================");
	}

	public void Get_Supported_Flash_Modes()
	{
		Camera.Parameters camera_parameters = Camera_Deprecated.Camera_Device.getParameters();
		List<String> supported_focus_modes = camera_parameters.getSupportedFlashModes();
		String camera_direction;

		if(Is_Front_Facing)
			camera_direction = "Front";
		else
			camera_direction = "Back";

		Log.d("Nic Says", "=== Supported Flash Modes for " + camera_direction + " Camera ===");

		for(int i = 0; i < supported_focus_modes.size(); i++)
			Log.d("Nic Says", "=== Mode " + (i + 1) + ": " + supported_focus_modes.get(i));

		Log.d("Nic Says", "==========================================");
	}

	public int Get_Degrees_From_Rotation(int rotation)
	{
		int degrees = 0;

		switch(rotation)
		{
			case Surface.ROTATION_0:    degrees = 0;	break;
			case Surface.ROTATION_90:	degrees = 90;	break;
			case Surface.ROTATION_180:	degrees = 180;	break;
			case Surface.ROTATION_270:	degrees = 270;	break;
		}

		return degrees;
	}

	public Bitmap Get_Resized_Bitmap(Bitmap bm, int newHeight, int newWidth)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	}

	public void Swap_Camera()
	{
		if(Is_Front_Facing)
			Is_Front_Facing = false;
		else
			Is_Front_Facing = true;

		Camera_Deprecated.Release_Camera_Deprecated();
		Camera_Deprecated.Init_Camera_Deprecated();

		Core.Preview_View.removeAllViews();
		Core.Preview_View.addView(Camera_Deprecated.Surface_View);
		Core.Preview_View.invalidate();
	}
}

