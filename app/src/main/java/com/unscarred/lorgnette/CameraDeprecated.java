package com.unscarred.lorgnette;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.util.Log;
import android.view.*;

class CameraDeprecated
{
	private Context                 Base_Context;
	private CoreClass               Core;

	public  SurfaceView             Surface_View;
	public  SurfaceHolder           Surface_Holder;
	public  Camera                  Camera_Device;
	public  int                     Camera_ID;
	public  List<Camera.Size>       Supported_Preview_Sizes;
	public  SurfaceHolderCallback   Surface_Holder_Callback;

	CameraDeprecated(Context context, CoreClass core)
	{
		Base_Context = context;
		Core = core;
	}

	public void Init_Camera_Deprecated()
	{
		Surface_Holder_Callback = new SurfaceHolderCallback(Base_Context, Core);
		Surface_View = new SurfaceView(Base_Context);
		Surface_Holder = Surface_View.getHolder();
		Surface_Holder.addCallback(Surface_Holder_Callback);
		Surface_Holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void Release_Camera_Deprecated()
	{
		if(Camera_Device != null)
		{
			Surface_View = null;
			Surface_Holder.removeCallback(Surface_Holder_Callback);
			Camera_Device.stopPreview();
			Camera_Device.setPreviewCallback(null);
			Camera_Device.release();
			Camera_Device = null;
		}
	}

	public void Set_Camera_Display_Orientation()
	{
		Camera_Device.stopPreview();

		Camera.CameraInfo camera_info = new Camera.CameraInfo();
		Camera.getCameraInfo(Camera_ID, camera_info);
		int result;

		WindowManager Window_Manager = (WindowManager)Base_Context.getSystemService(Context.WINDOW_SERVICE);
		Core.Camera_Handler.Camera_Orientation_Degrees = Core.Camera_Handler.Get_Degrees_From_Rotation(Window_Manager.getDefaultDisplay().getRotation());

		if(camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (camera_info.orientation + Core.Camera_Handler.Camera_Orientation_Degrees) % 360;
			result = (360 - result) % 360;  // compensate for mirror image
		}
		else // back-facing camera
		{
			result = (camera_info.orientation - Core.Camera_Handler.Camera_Orientation_Degrees + 360) % 360;
		}

		Camera_Device.setDisplayOrientation(result);

		try
		{
			Camera_Device.setPreviewDisplay(Surface_Holder);
		}
		catch(Exception e)
		{
			System.err.println("Error in Camera_Deprecated: setCameraDisplayOrientation:");
			System.err.println("Caught Exception: " + e.getMessage());
		}

		Camera_Device.startPreview();
	}

	public Camera.Size Get_Optimal_Preview_Size(List<Camera.Size> camera_sizes, int width, int height)
	{
		double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double)width / (double)height;
		Camera.Size optimal_size = null;

		if(camera_sizes != null)
		{
			double min_difference = Double.MAX_VALUE;
			int target_height = height;
			boolean run_loop = true;

			for(int i = 0; i < camera_sizes.size() && run_loop; i++)
			{
				double ratio = (double)camera_sizes.get(i).width / (double)camera_sizes.get(i).height;

				if(Math.abs(ratio - targetRatio) <= ASPECT_TOLERANCE) run_loop = false;

				if(run_loop)
				{
					if(Math.abs(camera_sizes.get(i).height - target_height) < min_difference)
					{
						optimal_size = camera_sizes.get(i);
						min_difference = Math.abs(camera_sizes.get(i).height - target_height);
					}
				}
			}

			if(optimal_size == null)
			{
				min_difference = Double.MAX_VALUE;

				for(int i = 0; i < camera_sizes.size(); i++)
				{
					if(Math.abs(camera_sizes.get(i).height - target_height) < min_difference)
					{
						optimal_size = camera_sizes.get(i);
						min_difference = Math.abs(camera_sizes.get(i).height - target_height);
					}
				}
			}
		}

		return optimal_size;
	}

	public int Find_Front_Facing_Camera_ID()
	{
		int camera_id = -1;
		int number_of_cameras = Camera.getNumberOfCameras();

		for(int i = 0; i < number_of_cameras; i++)
		{
			Camera.CameraInfo camera_info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, camera_info);
			if(camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
			{
				camera_id = i;

				break;
			}
		}

		return camera_id;
	}

	public int Find_Back_Facing_Camera_ID()
	{
		int camera_id = -1;
		int number_of_cameras = Camera.getNumberOfCameras();

		for(int i = 0; i < number_of_cameras; i++)
		{
			Camera.CameraInfo camera_info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, camera_info);
			if(camera_info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
			{
				camera_id = i;

				break;
			}
		}

		return camera_id;
	}

	public void Set_Camera_Focus_Mode(String Focus_Mode)
	{
		Camera.Parameters parameters = Core.Camera_Handler.Camera_Deprecated.Camera_Device.getParameters();
		Core.Camera_Handler.Camera_Deprecated.Supported_Preview_Sizes = parameters.getSupportedPreviewSizes();

		if(Core.Camera_Handler.Is_Front_Facing)
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
		else
			parameters.setFocusMode(Focus_Mode);

		Core.Camera_Handler.Camera_Deprecated.Camera_Device.setParameters(parameters);
	}

	public void Set_Camera_Flash_Mode()
	{
		Camera.Parameters parameters = Core.Camera_Handler.Camera_Deprecated.Camera_Device.getParameters();
		Core.Camera_Handler.Camera_Deprecated.Supported_Preview_Sizes = parameters.getSupportedPreviewSizes();

		if(Core.Camera_Handler.Is_Front_Facing)
		{
			Core.Camera_Handler.Flash_Mode_Index = 0;
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}
		else
		{
			Core.Camera_Handler.Flash_Mode_Index++;
			if(Core.Camera_Handler.Flash_Mode_Index > 3) Core.Camera_Handler.Flash_Mode_Index = 0;

			switch(Core.Camera_Handler.Flash_Mode_Index)
			{
				case 0:
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					Core.Flash_Mode_Text_View.setText("OFF");
					break;

				case 1:
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
					Core.Flash_Mode_Text_View.setText("ON");
					break;

				case 2:
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
					Core.Flash_Mode_Text_View.setText("AUTO");
					break;

				case 3:
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					Core.Flash_Mode_Text_View.setText("TORCH");
					break;
			}
		}

		Core.Camera_Handler.Camera_Deprecated.Camera_Device.setParameters(parameters);
	}

	public Camera.AutoFocusCallback Camera_Auto_Focus_Callback = new Camera.AutoFocusCallback()
	{
		public void onAutoFocus(boolean success, Camera camera)
		{
			if(success && Core.Camera_Handler.Take_Picture_On_Focus)
			{
				Core.Camera_Handler.Take_Picture();

				Camera_Device.autoFocus(null);
				Camera_Device.cancelAutoFocus();
				Core.Photo_Button_Highlight.setVisibility(View.GONE);
				Core.Camera_Handler.Take_Picture_On_Focus = false;
				Set_Camera_Focus_Mode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
		}
	};

	public Camera.PreviewCallback Camera_Preview_Callback = new Camera.PreviewCallback()
	{
		public void onPreviewFrame(byte[] data, Camera arg1)
		{
			Core.Camera_Handler.Camera_Deprecated.Surface_View.postInvalidate();
		}
	};

	public Camera.ShutterCallback Shutter_Callback = new Camera.ShutterCallback()
	{
		public void onShutter()
		{
			Core.Preview_View.setBackgroundColor(Color.argb(127, 255, 255, 255));
		}
	};

	public Camera.PictureCallback Raw_Picture_Callback = new Camera.PictureCallback()
	{
		public void onPictureTaken(byte[] data, Camera camera)
		{
			Core.Preview_View.setBackgroundColor(Color.argb(0, 255, 255, 255));
		}
	};

	public Camera.PictureCallback Picture_Callback = new Camera.PictureCallback()
	{
		@Override public void onPictureTaken(byte[] data, Camera camera)
		{
			Core.File_Handler.Save_Preview_to_File(data);
			Core.Show_Conformation_Layout();

			WindowManager Window_Manager = (WindowManager)Base_Context.getSystemService(Context.WINDOW_SERVICE);
			Display display = Window_Manager.getDefaultDisplay();
			Point   size    = new Point();
			display.getSize(size);

			Drawable background = new BitmapDrawable(Base_Context.getResources(),
			                                         Core.Camera_Handler.Get_Resized_Bitmap(BitmapFactory.decodeFile(Core.File_Handler.Destination_Folder +
			                                                                                                         "/" +
			                                                                                                         Core.File_Handler.Filename),
			                                                                 size.y, size.x));

			Core.Confirmation_Layout.setBackground(background);
		}
	};
}