package com.unscarred.lorgnette;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;


class SurfaceHolderCallback implements SurfaceHolder.Callback
{
	private Context             Base_Context;
	private CoreClass           Core;


	SurfaceHolderCallback(Context context, CoreClass core)
	{
		Base_Context = context;
		Core = core;
	}


	public void surfaceCreated(SurfaceHolder Surface_Holder)
	{
		Log.d("Nic Says", "SurfaceHolderCallback: surfaceCreated");

		try
		{
			Core.Camera_Handler.Camera_Deprecated.Surface_Holder = Surface_Holder;
			Core.Camera_Handler.Camera_Deprecated.Release_Camera_Deprecated();

			final CameraDeprecated Camera_Deprecated = Core.Camera_Handler.Camera_Deprecated;

			if(Core.Camera_Handler.Is_Front_Facing)
				Camera_Deprecated.Camera_ID = Camera_Deprecated.Find_Front_Facing_Camera_ID();
			else
				Camera_Deprecated.Camera_ID = Camera_Deprecated.Find_Back_Facing_Camera_ID();

			Camera_Deprecated.Camera_Device = Camera.open(Camera_Deprecated.Camera_ID);
			Camera_Deprecated.Camera_Device.setPreviewDisplay(Surface_Holder);
			Camera_Deprecated.Camera_Device.setPreviewCallback(Core.Camera_Handler.Camera_Deprecated.Camera_Preview_Callback);

			Core.Camera_Handler.Get_Supported_Focus_Modes();
			Core.Camera_Handler.Get_Supported_Flash_Modes();

			Camera.Parameters parameters = Camera_Deprecated.Camera_Device.getParameters();
			Camera_Deprecated.Supported_Preview_Sizes = parameters.getSupportedPreviewSizes();

			if(!Core.Camera_Handler.Is_Front_Facing)
			{
				Core.Camera_Handler.Flash_Mode_Index = 2;
				Core.Flash_Mode_Text_View.setText("AUTO");
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
			else
			{
				Core.Camera_Handler.Flash_Mode_Index = 0;
				Core.Flash_Mode_Text_View.setText("OFF");
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
			}

			parameters.setJpegQuality(100);

			Camera_Deprecated.Camera_Device.setParameters(parameters);
		}
		catch(IOException e)
		{
			System.err.println("Error in CameraDeprecated: Set_Camera_Front_Facing:");
			System.err.println("Caught IO Exception: " + e.getMessage());
		}
	}

	public void surfaceChanged(SurfaceHolder Surface_Holder, int format, int width, int height)
	{
		Log.d("Nic Says", "SurfaceHolderCallback: surfaceChanged");

		Core.Camera_Handler.Camera_Deprecated.Surface_Holder = Surface_Holder;
		CameraDeprecated Camera_Deprecated = Core.Camera_Handler.Camera_Deprecated;

		if(Camera_Deprecated.Supported_Preview_Sizes != null)
		{
			Camera.Size preview_size = Camera_Deprecated.Get_Optimal_Preview_Size(Camera_Deprecated.Supported_Preview_Sizes, width, height);

			Camera.Parameters parameters = Camera_Deprecated.Camera_Device.getParameters();
			parameters.setPreviewSize(preview_size.width, preview_size.height);

			if(width < height)
			{
				parameters.set("orientation", "portrait");
				Core.Camera_Handler.Is_Portrait = true;
			}
			else
			{
				parameters.set("orientation", "landscape");
				Core.Camera_Handler.Is_Portrait = false;
			}

			Camera_Deprecated.Camera_Device.setParameters(parameters);
		}

		Camera_Deprecated.Set_Camera_Display_Orientation();
	}

	public void surfaceDestroyed(SurfaceHolder Surface_Holder)
	{
		Log.d("Nic Says", "SurfaceHolderCallback: surfaceDestroyed");

		if(Core.Camera_Handler.Camera_Deprecated.Camera_Device != null)
		{
			Core.Camera_Handler.Camera_Deprecated.Camera_Device.stopPreview();
			Core.Camera_Handler.Camera_Deprecated.Camera_Device.release();
			Core.Camera_Handler.Camera_Deprecated.Camera_Device = null;
		}
	}
}

