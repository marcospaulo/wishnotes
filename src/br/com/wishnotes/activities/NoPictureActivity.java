package br.com.wishnotes.activities;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.wishnotes.ApplicationConstants;
import br.com.wishnotes.R;
import br.com.wishnotes.model.services.UserServices;

public class NoPictureActivity extends RoboFragmentActivity {
	private static final int PICTURE_SELECT = 0;
	private final int CAMERA_CAPTURE = 1;
	private final int PIC_CROP = 2;
	private Uri picUri;

	private SharedPreferences wishlistPrefs;

	private ProgressDialog progressDialog;

	@InjectView(R.id.sure_btn)
	Button bSure;

	@InjectView(R.id.skip_btn)
	Button bSkip;
	@InjectView(R.id.you_dont_have_pic_text)
	TextView tvYouDontHavePic;

	@InjectView(R.id.upload_text)
	TextView tvUploadOne;

	@InjectView(R.id.pic_img_view)
	ImageView imgViewPicture;

	private Bitmap thePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_photo);
		bSure.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DialogFragment newFragment = new TakePicDialogFragment();
				newFragment.show(getSupportFragmentManager(), "getPic");
			}
		});
		wishlistPrefs = this.getSharedPreferences(
				ApplicationConstants.WISHLIST_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		bSkip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressDialog = ProgressDialog.show(NoPictureActivity.this,
						"", "Uploading Picture", true);
				SendImg sendImgTask = new SendImg();
				sendImgTask.execute(thePic);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();

	}

	public void pickImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, PICTURE_SELECT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PIC_CROP && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			thePic = extras.getParcelable("data");
			imgViewPicture.setImageBitmap(thePic);
		} else {
			picUri = data.getData();
			performCrop();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void performCrop() {

		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 512);
			cropIntent.putExtra("outputY", 512);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);

		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	public class TakePicDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					NoPictureActivity.this);
			builder.setTitle(R.string.choose_an_action);
			builder.setItems(R.array.choose_actions,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								pickImage();
							} else {
								try {
									Intent captureIntent = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									startActivityForResult(captureIntent,
											CAMERA_CAPTURE);
								} catch (ActivityNotFoundException anfe) {
									// display an error message
									String errorMessage = "Whoops - your device doesn't support capturing images!";
									Toast toast = Toast.makeText(
											NoPictureActivity.this,
											errorMessage, Toast.LENGTH_SHORT);
									toast.show();
								}

							}
						}
					});
			return builder.create();
		}
	}

	public void updateUI() {
		if (picUri != null) {
			imgViewPicture.setImageBitmap(thePic);
			bSure.setText("Take Another");
			bSkip.setText("That's Ok!");
			tvYouDontHavePic.setText("Look at that, you look gorgeous!!");
			tvUploadOne.setVisibility(View.INVISIBLE);

		}
	}

	class SendImg extends AsyncTask<Bitmap, Void, String> {

		@Override
		protected String doInBackground(Bitmap... params) {
			UserServices userServices = new UserServices(NoPictureActivity.this);
			String imgURL = userServices.sendUserImg(params[0]);
			return imgURL;
		}

		@Override
		protected void onPostExecute(String imgURL) {
			progressDialog.dismiss();
			Editor editor = wishlistPrefs.edit();
			editor.putString(ApplicationConstants.USER_IMG_URL, imgURL);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			} else {
				editor.commit();
			}

			Intent intent = new Intent(NoPictureActivity.this,
					MainAppActivity.class);
			startActivity(intent);
		}

	}
}
