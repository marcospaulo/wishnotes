package br.com.wishnotes.model.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.support.Base64;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import br.com.wishnotes.ApplicationConstants;
import br.com.wishnotes.model.ProfilePicture;
import br.com.wishnotes.model.Status;
import br.com.wishnotes.model.User;
import br.com.wishnotes.model.Wish;

public class UserServices {

	private SharedPreferences wishlistPrefs;
	private RestTemplate restTemplate;
	private HttpHeaders requestHeaders;
	private String token;

	public UserServices(Context ctx) {
		wishlistPrefs = ctx.getSharedPreferences(
				ApplicationConstants.WISHLIST_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		token = wishlistPrefs.getString(ApplicationConstants.TOKEN, "");
		restTemplate = new RestTemplate();
		requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(new MediaType("application", "json"));
	}

	public User sendUserLogin(String oauthToken) {
		User user = new User();
		user.setAccess_token(oauthToken);
		user.setToken(token);

		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());

		User result = restTemplate.postForObject(ApplicationConstants.USER_URL,
				user, User.class);

		user = result;
		return user;
	}

	public String sendUserImg(Bitmap bmp) {
		ProfilePicture profilePicture = new ProfilePicture();
		profilePicture.setToken(token);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bmp.compress(Bitmap.CompressFormat.JPEG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeBytes(ba);

		profilePicture.setImg(ba1);

		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());

		User result = restTemplate.postForObject(
				ApplicationConstants.USER_IMG_URL, profilePicture, User.class);

		return result.getAvatar_url();
	}

	public void addWish(Wish wish, Bitmap bmp) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bmp.compress(Bitmap.CompressFormat.JPEG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeBytes(ba);

		wish.setImg(ba1);

		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());

		wish.setToken(token);

		Status result = restTemplate.postForObject(
				ApplicationConstants.ADD_WISH, wish, Status.class);
	}

	public List<Wish> searchList() {

		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());

		List<Wish> result = restTemplate.getForObject(
				ApplicationConstants.ADD_WISH + token, List.class);

		return result;
	}

}
