package com.shawon.yousuf.facebookintegration;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shawon.yousuf.facebookintegration.adapter.FriendsAdapter;
import com.shawon.yousuf.facebookintegration.model.Friend;
import com.shawon.yousuf.facebookintegration.util.ConnectionDetector;
import com.shawon.yousuf.facebookintegration.util.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.login_button)
    LoginButton loginButton;

    @Bind(R.id.button)
    Button button;
    @Bind(R.id.textViewName)
    TextView textViewName;
    @Bind(R.id.buttonLoadMore)
    Button buttonLoadMore;
    @Bind(R.id.listViewFriends)
    ListView listViewFriends;
    @Bind(R.id.imageViewProfilePic)
    ImageView imageViewProfilePic;


    CallbackManager callbackManager;
    AccessToken accessToken;
    Profile userProfile;
    GraphRequest pagingRequest;
    AccessTokenTracker accessTokenTracker;


    FriendsAdapter friendsAdapter;
    List<Friend> friendList;

    ConnectionDetector cd;
    @Bind(R.id.linearLayoutProfile)
    LinearLayout linearLayoutProfile;


    private String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //  printHashKey();



        friendList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(this, R.layout.row_friends, friendList);
        listViewFriends.setAdapter(friendsAdapter);

        setProperty();

        changeVisibility(View.INVISIBLE);

        loadAccessToken();
        loadUserProfile();

//
//        if (accessToken != null) {
//
//
//            GraphRequest request = GraphRequest.newMeRequest(
//                    accessToken,
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//                            // Application code
//
//                            Log.d(TAG, "jsonObject: " + object.toString());
//                            Log.d(TAG, "graph response: " + response.toString());
//
//                        }
//                    });
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id,name,link");
//            request.setParameters(parameters);
//            request.executeAsync();
//
//
//        }
//

        ;

//
//        pagingRequest = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me/taggable_friends",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                       /* handle the result */
//                        Log.d(TAG, "taggabe_friends: " + response.toString());
//                        JSONObject jsonObject = response.getJSONObject();
//                        try {
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                            JSONObject jsonObjectPaging = jsonObject.getJSONObject("paging");
//                            Log.d(TAG, "paging: " + jsonObjectPaging);
//                            Log.d(TAG, "friend list size: " + jsonArray.length());
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                String name = jsonObject1.getString("name");
//                                Log.d(TAG, "name: " + name);
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//        );
//

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessTokenTracker != null) {
            accessTokenTracker.stopTracking();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }





    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.shawon.yousuf.facebookintegration",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    private void setProperty() {
        List<String> permissions = Arrays.asList("public_profile", "user_friends");
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "success");

                loadAccessToken();
                loadUserProfile();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError");
            }
        });



       accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    imageViewProfilePic.setImageDrawable(null);
                    textViewName.setText("");
                    friendList.clear();
                    friendsAdapter.notifyDataSetChanged();

                    changeVisibility(View.INVISIBLE);
                }
            }
        };



    }

    private void changeVisibility(int visibility){

        linearLayoutProfile.setVisibility(visibility);
        imageViewProfilePic.setVisibility(visibility);

    }


    private void loadAccessToken() {

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d(TAG, "access token is null");
        } else {
            Log.d(TAG, "access token is not null");
            Set<String> permissions = accessToken.getPermissions();
            Log.d(TAG, "permissions: " + Arrays.toString(permissions.toArray()));
        }


    }


    private void loadUserProfile() {
        userProfile = Profile.getCurrentProfile();

        if (userProfile == null) {
            Log.d(TAG, "userProfile is null");
        } else {
            String name = userProfile.getName();
            Uri profileUri = userProfile.getProfilePictureUri(100, 100);

            Log.d(TAG, "name: " + name);
            Log.d(TAG, "profile uri: " + profileUri.getPath());

            textViewName.setText(name);

            Picasso.with(this)
                    .load(profileUri)
                    .into(imageViewProfilePic);

            changeVisibility(View.VISIBLE);

        }
    }


    private void loadFriends() {

        if (cd == null) {
            cd = new ConnectionDetector(this);
        }

        if (!cd.isConnectedToInternet()) {
            Utility.showSimpleAlertDialog(this, "Warning", "Please check your internet connection");
            return;
        }


        if (accessToken == null) {
            Log.d(TAG, "access token is null");
            return;
        }

        friendList.clear();
        friendsAdapter.notifyDataSetChanged();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                friendResultCallback

        ).executeAsync();


    }

    private void loadMoreFriends() {

        if (cd == null) {
            cd = new ConnectionDetector(this);
        }

        if (!cd.isConnectedToInternet()) {
            Utility.showSimpleAlertDialog(this, "Warning", "Please check your internet connection");
            return;
        }

        if (pagingRequest != null) {
            pagingRequest.setCallback(friendResultCallback);
            pagingRequest.executeAsync();
        } else {
            Log.d(TAG, "next request is null");
        }


    }





    GraphRequest.Callback friendResultCallback = new GraphRequest.Callback() {
        public void onCompleted(GraphResponse response) {
                       /* handle the result */
            Log.d(TAG, "taggabe_friends: " + response.toString());
            JSONObject jsonObject = response.getJSONObject();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject jsonObjectPaging = jsonObject.getJSONObject("paging");
                Log.d(TAG, "paging: " + jsonObjectPaging);
                Log.d(TAG, "friend list size: " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectEach = jsonArray.getJSONObject(i);
                    String name = jsonObjectEach.getString("name");
                    JSONObject jsonObjectPicture = jsonObjectEach.getJSONObject("picture");
                    JSONObject pictureData = jsonObjectPicture.getJSONObject("data");
                    String pictureUrl = pictureData.getString("url");

                    Friend friend = new Friend(name, pictureUrl);
                    friendList.add(friend);

                    Log.d(TAG, "name: " + name);
                }

                int totalItem = listViewFriends.getCount();

                friendsAdapter.notifyDataSetChanged();
                listViewFriends.smoothScrollToPosition(totalItem + 3);

                pagingRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @OnClick({R.id.button, R.id.buttonLoadMore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                loadFriends();
                break;
            case R.id.buttonLoadMore:
                loadMoreFriends();
                break;
        }
    }
}
