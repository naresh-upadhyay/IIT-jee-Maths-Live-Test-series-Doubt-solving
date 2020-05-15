package com.naresh.kingupadhyay.mathsking.UiMaterial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class LoginOption extends AppCompatActivity {
    private static final int RC_SIGN_IN = 888;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    public String id;
    public String name="New user";
    ProgressBar loginProgress;
    LoginActivity l=new LoginActivity();
    protected String NAME="name";

    FirebaseUser firebaseUser;
    private String userImage = "https://firebasestorage.googleapis.com/v0/b/mathsking-a45fe.appspot.com/o/ic_launcher.pngr.png?alt=media&token=60950c10-269c-4fd7-84dd-eaaf504967dd";
    private int rating=1;
    private int follow=0;
    private int following=0;
    private boolean publicStatus=true;// public/private status of user(private user nobody knows you)
    private int solutionMade=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        SharedPreferences prefs =getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();

        Button phoneAuth=findViewById(R.id.phoneAuth);
        phoneAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("skip",false);
                edt.apply();
                Intent intent = new Intent(LoginOption.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        Button skip=findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("skip",true);
                edt.apply();
                SharedPreferences prefs = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear().apply();

                Intent intent = new Intent(LoginOption.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        phoneAuth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_phone, 0,0, 0);
        skip.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_next_item, 0);
        mAuth = FirebaseAuth.getInstance();
        loginProgress=findViewById(R.id.loginProgress);
        loginProgress.setVisibility(View.GONE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("skip",false);
                edt.apply();

                if (!isNetworkAvailable()){
                    Toast.makeText(LoginOption.this, "Network not available ", Toast.LENGTH_LONG).show();
                }else{
                    signIn();

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginOption.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setUserAccount(String iduser){
        //todo update
            try{
                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                userImage = firebaseUser.getPhotoUrl().toString();
                UserDetails userdataUpdate = new UserDetails();
                userdataUpdate.setName(name);
                userdataUpdate.setUid(iduser);
                userdataUpdate.setUserImage(userImage);
                userdataUpdate.setFollow(follow);
                userdataUpdate.setFollowing(following);
                userdataUpdate.setPpStatus(publicStatus);
                userdataUpdate.setRating(rating);
                userdataUpdate.setSolved(solutionMade);
                final FirebaseFirestore db= FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());
                docRef.set(userdataUpdate.toMap(), SetOptions.merge());

            }catch (Exception e){
            }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginProgress.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            name=user.getDisplayName();
                            id=user.getEmail();
//                            l.addUserDatabase(name,id);
                            SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edt = pref.edit();
                            edt.putString("name",name);
                            edt.putString("id",id);
                            edt.apply();

                            try{
                                boolean isnewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if(isnewUser){
                                    setUserAccount(id);
                                }
                            }catch(Exception e){
                            }
                            startActivity(new Intent(LoginOption.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(LoginOption.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void signIn() {
        loginProgress.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
