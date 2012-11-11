package br.com.wishnotes.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.com.wishnotes.ApplicationConstants;
import br.com.wishnotes.R;

public class MeFragment extends RoboFragment {
	@InjectView(R.id.tv_username)
	private TextView tvUsername;

	@InjectView(R.id.tv_number_lists)
	private TextView tvNumberList;

	@InjectView(R.id.tv_number_wishes)
	private TextView tvNumberWishes;

	@InjectView(R.id.user_profile_img)
	private ImageView imgUserProfile;

	@InjectView(R.id.listview_my_wishes)
	private ListView listView;

	private SharedPreferences wishlistPrefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_me, container, false);
		wishlistPrefs = getActivity().getSharedPreferences(
				ApplicationConstants.WISHLIST_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillUserInfo();
	}

	private void fillUserInfo() {
		tvUsername.setText(wishlistPrefs.getString(
				ApplicationConstants.USER_USERNAME, "fulano"));
		new DownloadBitmap().execute("");

	}

	class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						wishlistPrefs.getString(
								ApplicationConstants.USER_IMG_URL, ""))
						.getContent());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imgUserProfile.setImageBitmap(result);
		}

	}

	public ListView getListView() {
		return listView;
	}
}
