package com.unscarred.lorgnette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileHandler
{
	private CoreClass   Core;
	private Context     Base_Context;

	public  String      Destination_Folder;
	public  String      Filename;

	FileHandler(Context context, CoreClass core)
	{
		Base_Context = context;
		Core = core;

		File file = new File(Base_Context.getFilesDir().getPath() + "/settings");

		if(file.exists())
		{
			try
			{
				FileInputStream   fis = new FileInputStream(Base_Context.getFilesDir().getPath() + "/settings");
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader    br  = new BufferedReader(isr);

				Destination_Folder = br.readLine();

				br.close();
				isr.close();
				fis.close();
			}
			catch(FileNotFoundException e)
			{
				System.err.println("Error saving User Login Data:");
				System.err.println("Caught FileNotFoundException: " + e.getMessage());
			}
			catch(IOException e)
			{
				System.err.println("Error saving User Login Data:");
				System.err.println("Caught IOException: " + e.getMessage());
			}
		}
		else
		{
			Destination_Folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Lorgnette";
		}
	}

	public void Save_Preview_to_File(byte[] bytes)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[32 * 1024];
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

		if(Core.Camera_Handler.Is_Portrait)
		{
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
		}

		FileOutputStream out;

		try
		{
			DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
			Date       currentDate   = new Date();

			Filename = dateFormatter.format(currentDate) + ".jpeg";

			File file = new File(Destination_Folder);
			file.mkdirs();

			out = new FileOutputStream(Destination_Folder + "/" + Filename);
			bMap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			bMap.recycle();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
