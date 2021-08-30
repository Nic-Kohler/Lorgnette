package com.unscarred.lorgnette;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bgreco.DirectoryPicker;

import java.io.File;


public class CoreClass
{
	private Context         Base_Context;
	public  Handler         Activity_Handler;

	public  FileHandler     File_Handler;
	public  CameraHandler   Camera_Handler;

	private LinearLayout    Main_Layout;
	private PopupWindow     Popup_Window;
	private View            Popup_View;

	public  FrameLayout     Preview_View;
	private LinearLayout    Photo_Button;
	public  LinearLayout    Photo_Button_Highlight;
	private ImageButton     Settings_Button;
	public  LinearLayout    Swap_Camera_Button;
	public  LinearLayout    Flash_Mode_Layout;
	public  TextView        Flash_Mode_Text_View;

	private Button          Settings_Done_Button;
	private ImageButton     Settings_Folder_Button;
	private TextView        Settings_Location_Text_View;

	private Button          Confirmation_Save_Button;
	private Button          Confirmation_Delete_Button;
	public  RelativeLayout  Confirmation_Layout;


	CoreClass(Context context, LinearLayout main_layout, Handler activity_handler)
	{
		Base_Context = context;
		Main_Layout = main_layout;
		Activity_Handler = activity_handler;

		File_Handler = new FileHandler(Base_Context, this);
		Camera_Handler = new CameraHandler(Base_Context, this);
	}

	public void Set_Settings_Location_Text_View()
	{
		if(Settings_Location_Text_View != null) Settings_Location_Text_View.setText(File_Handler.Destination_Folder);
	}

	public void Show_Preview_Layout()
	{
		Main_Layout.removeAllViews();
		Camera_Handler.Camera_Deprecated.Release_Camera_Deprecated();
		Camera_Handler.Camera_Deprecated.Init_Camera_Deprecated();

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View Main_Layout_View = layoutInflater.inflate(R.layout.preview, null);

		Preview_View = null;
		Preview_View = (FrameLayout)Main_Layout_View.findViewById(R.id.preview_layout);
		Photo_Button = (LinearLayout)Main_Layout_View.findViewById(R.id.preview_photo_button);
		Photo_Button_Highlight = (LinearLayout)Main_Layout_View.findViewById(R.id.preview_photo_button_highlight);
		Settings_Button = (ImageButton)Main_Layout_View.findViewById(R.id.preview_settings_button);
		Swap_Camera_Button = (LinearLayout)Main_Layout_View.findViewById(R.id.preview_swap_camera);
		Flash_Mode_Layout = (LinearLayout) Main_Layout_View.findViewById(R.id.preview_flash_mode_layout);
		Flash_Mode_Text_View = (TextView) Main_Layout_View.findViewById(R.id.preview_flash_mode_text_view);

		Photo_Button_Highlight.setVisibility(View.GONE);

		Preview_View.removeAllViews();
		Preview_View.addView(Camera_Handler.Camera_Deprecated.Surface_View);

		Photo_Button.setOnTouchListener(On_Touch_Listener);
		Photo_Button.setOnHoverListener(On_Hover_Listener);
		Settings_Button.setOnClickListener(On_Click_Listener);
		Swap_Camera_Button.setOnClickListener(On_Click_Listener);
		Flash_Mode_Layout.setOnClickListener(On_Click_Listener);

		Main_Layout.addView(Main_Layout_View);
	}

	public void Show_Settings_Layout()
	{
		Main_Layout.removeAllViews();

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View Settings_Layout_View = layoutInflater.inflate(R.layout.settings, null);

		Settings_Folder_Button = (ImageButton)Settings_Layout_View.findViewById(R.id.settings_button_folder);
		Settings_Location_Text_View = (TextView)Settings_Layout_View.findViewById(R.id.settings_location_text);
		Settings_Done_Button = (Button)Settings_Layout_View.findViewById(R.id.settings_button_done);

		Settings_Done_Button.setOnClickListener(On_Click_Listener);
		Settings_Folder_Button.setOnClickListener(On_Click_Listener);

		Settings_Location_Text_View.setText(File_Handler.Destination_Folder);

		Main_Layout.addView(Settings_Layout_View);
	}

	public void Show_Conformation_Layout()
	{
		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Popup_View = layoutInflater.inflate(R.layout.confirmation, null);

		Popup_Window = new PopupWindow(Popup_View, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		Popup_Window.setAnimationStyle(android.R.style.Animation_Dialog);
		//Popup_Window.showAtLocation(Popup_View, Gravity.CENTER, 0, 0);

		Confirmation_Save_Button = (Button)Popup_View.findViewById(R.id.confirm_button_save);
		Confirmation_Delete_Button = (Button)Popup_View.findViewById(R.id.confirm_button_delete);
		Confirmation_Layout = (RelativeLayout)Popup_View.findViewById(R.id.confirm_layout);

		Confirmation_Save_Button.setOnClickListener(On_Click_Listener);
		Confirmation_Delete_Button.setOnClickListener(On_Click_Listener);
	}

	private void Show_Dialog(String head, String msg)
	{

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Popup_View = layoutInflater.inflate(R.layout.dialog, null);

		Popup_Window = new PopupWindow(Popup_View, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		Popup_Window.setAnimationStyle(android.R.style.Animation_Dialog);
		Popup_Window.showAtLocation(Popup_View, Gravity.CENTER, 0, 0);

		TextView Dialog_Heading_Text = (TextView)Popup_View.findViewById(R.id.dialog_heading_message);
		TextView Dialog_Text = (TextView)Popup_View.findViewById(R.id.dialog_message);
		Button Dialog_Ok_Button = (Button)Popup_View.findViewById(R.id.dialog_ok);

		Dialog_Ok_Button.setOnClickListener(On_Click_Listener);

		Dialog_Heading_Text.setText(head);
		Dialog_Text.setText(msg);
	}

	private View.OnHoverListener On_Hover_Listener = new View.OnHoverListener()
	{
		@Override public boolean onHover(View view, MotionEvent Motion_Event)
		{
			switch(Motion_Event.getAction())
			{
				case MotionEvent.ACTION_HOVER_ENTER:
					Log.d("Nic Says", "AirView ENTER action triggered.");
					Camera_Handler.Has_AirView = true;

					Photo_Button_Highlight.setVisibility(View.VISIBLE);

					Camera_Handler.Camera_Deprecated.Set_Camera_Focus_Mode(Camera.Parameters.FOCUS_MODE_AUTO);
					Camera_Handler.Take_Picture_On_Focus = true;
					Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(null);
					Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(Camera_Handler.Camera_Deprecated.Camera_Auto_Focus_Callback);

					break;

				case MotionEvent.ACTION_HOVER_EXIT:
					Photo_Button_Highlight.setVisibility(View.GONE);

					Camera_Handler.Take_Picture_On_Focus = false;
					Camera_Handler.Camera_Deprecated.Camera_Device.cancelAutoFocus();
					Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(null);
					Camera_Handler.Camera_Deprecated.Set_Camera_Focus_Mode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
					break;
			}

			return false;
		}
	};

	public View.OnTouchListener On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent Motion_Event)
		{
			switch(Motion_Event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					switch(view.getId())
					{
						case R.id.preview_photo_button:
							if(!Camera_Handler.Has_AirView)
							{
								Photo_Button_Highlight.setVisibility(View.VISIBLE);

								Camera_Handler.Camera_Deprecated.Set_Camera_Focus_Mode(Camera.Parameters.FOCUS_MODE_AUTO);
								Camera_Handler.Take_Picture_On_Focus = true;
								Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(null);
								Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(Camera_Handler.Camera_Deprecated.Camera_Auto_Focus_Callback);
							}
							break;
					}
					break;

				case MotionEvent.ACTION_UP:
					switch(view.getId())
					{
						case R.id.preview_photo_button:
							if(!Camera_Handler.Has_AirView)
							{
								Photo_Button_Highlight.setVisibility(View.GONE);

								Camera_Handler.Take_Picture_On_Focus = false;
								Camera_Handler.Camera_Deprecated.Camera_Device.autoFocus(null);
								Camera_Handler.Camera_Deprecated.Camera_Device.cancelAutoFocus();
							}
							break;
					}
					break;
			}

			return false;
		}
	};

	public View.OnClickListener On_Click_Listener = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			Log.d("Nic Says", "On_Click_Listener HAS RUN.");

			switch(view.getId())
			{
				case R.id.preview_settings_button:
					Show_Settings_Layout();
					break;

				case R.id.preview_swap_camera:
					Camera_Handler.Swap_Camera();
					break;

				case R.id.preview_flash_mode_layout:
					Camera_Handler.Camera_Deprecated.Set_Camera_Flash_Mode();
					break;

				case R.id.settings_button_folder:
					Message message = new Message();
					Bundle bundle = new Bundle();

					bundle.putInt("action", 0);
					message.setData(bundle);
					Activity_Handler.sendMessage(message);
					break;

				case R.id.settings_button_done:
					Show_Preview_Layout();
					break;

				case R.id.dialog_ok:
					Popup_Window.dismiss();
					break;

				case R.id.confirm_button_save:
					Show_Preview_Layout();
					break;

				case R.id.confirm_button_delete:
					File file = new File(File_Handler.Destination_Folder + "/" + File_Handler.Filename);

					if(file.exists()) file.delete();

					File_Handler.Filename = null;

					Show_Preview_Layout();
					break;
			}
		}
	};
}
