package br.com.wishnotes;

import com.evernote.client.oauth.android.EvernoteSession;

public interface ApplicationConstants {

	String EVERNOTE_PREFERENCE_NAME = "evernote.preferences";
	String WISHLIST_PREFERENCE_NAME = "wishlist.preferences";
	
	String CONSUMER_KEY = "marcospaulosd";
	String CONSUMER_SECRET = "e1bf5912c9f3ff0b";
	String OAUTH_KEY = "evernote.mAuthToken";
	String TOKEN = "wishlist.mToken";
	
	String USER_USERNAME = "wishlist.mUsername";
	String USER_PIC = "wishlist.mUserPic";
	String USER_EVERNOTE_ID = "wishlist.mUserEvernoteId";
	String USER_ID = "wishlist.mUserId";
	String USER_NAME = "wishlist.mUserRealName";

	String USER_URL = "http://evernotewishlist.herokuapp.com/users/create";
	String USER_IMG_URL = "http://evernotewishlist.herokuapp.com/users/avatar";
	String ADD_WISH = "http://evernotewishlist.herokuapp.com/wishes/create";
	String USER_LIST = "http://evernotewishlist.herokuapp.com/users/";

	String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;

}
