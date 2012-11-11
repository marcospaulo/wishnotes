package br.com.wishnotes.adapters;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.wishnotes.R;
import br.com.wishnotes.model.Wish;

public class MyWishesAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Wish> wishes;
	private ViewHolder holder;

	static class ViewHolder {
		private TextView tvWishName;
		private ImageView img;
	}

	public MyWishesAdapter(Context context, List<Wish> wishes) {
		mInflater = LayoutInflater.from(context);
		this.wishes = wishes;
	}

	public int getCount() {
		return wishes.size();
	}

	public Object getItem(int index) {
		return wishes.get(index);
	}

	public long getItemId(int index) {
		// return pessoas(index).getImgRes();
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.row_my_wishes, parent, false);
			holder = new ViewHolder();
			holder.tvWishName = (TextView) convertView
					.findViewById(R.id.tv_wishname);
			holder.img = (ImageView) convertView
					.findViewById(R.id.image_my_wish);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Wish wish = wishes.get(position);
		holder.tvWishName.setText(wish.getName());

		try {
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					wish.getImg()).getContent());
			holder.img.setImageBitmap(bitmap);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return convertView;

	}
}
