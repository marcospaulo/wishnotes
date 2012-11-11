package br.com.wishnotes.activities;

import java.util.List;
import java.util.Vector;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import br.com.wishnotes.ApplicationConstants;
import br.com.wishnotes.R;
import br.com.wishnotes.adapters.PagerAdapter;
import br.com.wishnotes.fragments.MeFragment;

@SuppressLint({ "NewApi", "NewApi" })
public class MainAppActivity extends RoboFragmentActivity {

	private static final int PICTURE_SELECT = 0;
	private final int CAMERA_CAPTURE = 1;
	private final int PIC_CROP = 2;
	private Bitmap thePic;
	private Uri picUri;
	private ListView listView;

	/** maintains the pager adapter */
	private PagerAdapter mPagerAdapter;

	@InjectView(R.id.btn_camera)
	private ImageButton btnCamera;
	

	private SharedPreferences wishlistPrefs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view_pager_layout);
		// initialsie the pager
		wishlistPrefs = this.getSharedPreferences(
				ApplicationConstants.WISHLIST_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		this.initialisePaging();

		btnCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DialogFragment newFragment = new TakePicDialogFragment();
				newFragment.show(getSupportFragmentManager(), "getPic");
			}
		});

	}

	public void pickImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, PICTURE_SELECT);
	}

	/**
	 * Initialise the fragments to be paged
	 */
	private void initialisePaging() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, MeFragment.class.getName()));

		this.mPagerAdapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		//
		ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(this.mPagerAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PIC_CROP && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();

			Intent intent = new Intent(this, AddWishActivity.class);
			intent.putExtra("data", extras.getParcelable("data"));
			startActivity(intent);
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

	class TakePicDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainAppActivity.this);
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
											MainAppActivity.this, errorMessage,
											Toast.LENGTH_SHORT);
									toast.show();
								}

							}
						}
					});
			return builder.create();
		}
	}

}
