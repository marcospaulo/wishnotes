package br.com.wishnotes.activities;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import br.com.wishnotes.R;
import br.com.wishnotes.model.Wish;
import br.com.wishnotes.model.services.UserServices;

public class AddWishActivity extends RoboActivity {

	@InjectView(R.id.btn_save)
	private Button btnSave;

	@InjectView(R.id.btn_cancel)
	private Button btnCancel;

	@InjectView(R.id.img_wish)
	private ImageView imgView;

	@InjectView(R.id.edit_title_wish)
	private EditText editTitle;

	@InjectView(R.id.edit_description_wish)
	private EditText editDescription;

	private ProgressDialog progressDialog;

	Bitmap img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wish);
		img = getIntent().getExtras().getParcelable("data");
		imgView.setImageBitmap(img);
	}

	public void save(View view) {
		Wish wish = new Wish();
		wish.setName(editTitle.getText().toString());
		wish.setDescription(editDescription.getText().toString());
		progressDialog = ProgressDialog.show(AddWishActivity.this, "",
				"Uploading Picture", true);
		new SendImg().execute(wish);
	}

	class SendImg extends AsyncTask<Wish, Void, Void> {

		@Override
		protected Void doInBackground(Wish... params) {

			UserServices userServices = new UserServices(AddWishActivity.this);
			userServices.addWish(params[0], img);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			finish();
			super.onPostExecute(result);
		}

	}
}
