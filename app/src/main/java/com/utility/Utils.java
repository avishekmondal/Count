package com.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	public static String convertStreamToString(InputStream inputStream)
			throws IOException {
		if (inputStream != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				inputStream.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/* Checking Connectivity Internet */
	public static boolean checkConnectivity(Context context) {
		boolean isConnected = false;
		try {
			ConnectivityManager connService = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = connService.getActiveNetworkInfo();
			if (network != null) {
				if (network.isConnected()) {
					isConnected = true;
				}
			} else {
				isConnected = false;

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return isConnected;
	}

	public static String convertDate(String date) {

		date = date.substring(0, 10);
		Format formatter;
		Date newdate = new Date();
		formatter = new SimpleDateFormat("dd/mm/yyyy");
		date = formatter.format(newdate);
		return date;

		// return date;
		/*
		 * java.util.Date ss1=new Date(date); SimpleDateFormat formatter5=new
		 * SimpleDateFormat("dd/mm/yyyy"); String formats1 =
		 * formatter5.format(ss1); return formats1;
		 */
	}

	public static int getBitmapSamplingRate(int initialWidth,
			int initialHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		if (initialHeight > reqHeight || initialWidth > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			int heightRatio = Math.round((float) initialHeight
					/ (float) reqHeight);
			int widthRatio = Math
					.round((float) initialWidth / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static void showNoInternet(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setMessage("Check Internet Connection and Retry Later.")
				.setTitle("No Internet.").setCancelable(false)
				.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						activity.finish();
						System.exit(0);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
