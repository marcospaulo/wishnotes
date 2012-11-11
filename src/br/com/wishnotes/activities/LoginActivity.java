package br.com.wishnotes.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import br.com.wishnotes.ApplicationConstants;
import br.com.wishnotes.R;
import br.com.wishnotes.model.User;
import br.com.wishnotes.model.services.UserServices;

import com.evernote.client.oauth.android.EvernoteSession;

public class LoginActivity extends RoboActivity {

	@InjectView(R.id.sign_in)
	ImageButton bSignIn;

	private EvernoteSession mEvernoteSession;
	private SharedPreferences evernotePref;
	private SharedPreferences wishListPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		evernotePref = getSharedPreferences(
				ApplicationConstants.EVERNOTE_PREFERENCE_NAME,
				Context.MODE_PRIVATE);

		wishListPref = getSharedPreferences(
				ApplicationConstants.WISHLIST_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		mEvernoteSession = EvernoteSession.init(this,
				ApplicationConstants.CONSUMER_KEY,
				ApplicationConstants.CONSUMER_SECRET,
				ApplicationConstants.EVERNOTE_HOST, null);

		bSignIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mEvernoteSession.authenticate(LoginActivity.this);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case EvernoteSession.REQUEST_CODE_OAUTH:
			if (resultCode == Activity.RESULT_OK) {
				createUserInServer(evernotePref.getString(
						ApplicationConstants.OAUTH_KEY, ""));

			}
			break;
		}
	}

	private void createUserInServer(String oauthToken) {
		CreateUser createUser = new CreateUser();
		createUser.execute(oauthToken);
	}

	class CreateUser extends AsyncTask<String, Void, User> {

		@Override
		protected User doInBackground(String... params) {
			UserServices userServices = new UserServices(LoginActivity.this);
			User user = userServices.sendUserLogin(params[0]);

			return user;
		}

		@Override
		protected void onPostExecute(User user) {
			saveUserToPreferences(user);
			Intent intent = new Intent(LoginActivity.this,
					NoPictureActivity.class);
			startActivity(intent);
		}

	}

	public void saveUserToPreferences(User user) {
		if (user != null) {
			Editor editor = wishListPref.edit();
			editor.putString(ApplicationConstants.USER_USERNAME,
					user.getUsername());
			editor.putString(ApplicationConstants.USER_PIC,
					user.getAvatar_url());
			editor.putInt(ApplicationConstants.USER_EVERNOTE_ID,
					user.getEvernote_id());
			editor.putString(ApplicationConstants.TOKEN, user.getToken());
			editor.putInt(ApplicationConstants.USER_ID, user.getId());
			editor.putString(ApplicationConstants.USER_NAME, user.getName());

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			} else {
				editor.commit();
			}
		}
	}

}
