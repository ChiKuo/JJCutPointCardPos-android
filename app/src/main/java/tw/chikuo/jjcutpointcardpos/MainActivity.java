package tw.chikuo.jjcutpointcardpos;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Firebase rootRef;
    private Firebase userRef;
    private Firebase pointRef;
    private User focusUser;

    private EditText phoneEditText;
    private Button enterButton;
    private LinearLayout actionLayout;
    private LinearLayout userInfoLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.hide();
        }

        // Setup Firebase library
        Firebase.setAndroidContext(this);
        rootRef = new Firebase("https://jjcutpointcard.firebaseio.com/");
        userRef = new Firebase("https://jjcutpointcard.firebaseio.com/users");
        pointRef = new Firebase("https://jjcutpointcard.firebaseio.com/points");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionLayout = (LinearLayout) findViewById(R.id.action_layout);
        userInfoLayout = (LinearLayout) findViewById(R.id.user_info_layout);

        // Phone
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

        // Phone Enter
        enterButton = (Button) findViewById(R.id.enter_button);
        if (enterButton != null){
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO Check user input

                    if (phoneEditText != null && !phoneEditText.getText().toString().equals("")){
                        final String phone = phoneEditText.getText().toString();

                        // Query for user
                        final Query queryRef = userRef.orderByChild("phone").equalTo(phone);
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null){
                                    // Save new user
                                    focusUser = new User(phone);
                                    userRef.push().setValue(focusUser);

                                    // Query again to find user id
                                    enterButton.performClick();

                                } else {
                                    // Phone already exist
                                    for (DataSnapshot eachData : dataSnapshot.getChildren()) {
                                        focusUser = eachData.getValue(User.class);
                                        focusUser.setId(eachData.getKey());

                                        reviewUserStep();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.d(JJCutPointCard.TAG,"Query user phone failed.");
                            }
                        });
                    }

                }
            });
        }

        // Done Button
        ImageButton doneButton = (ImageButton) findViewById(R.id.done_button);
        if (doneButton != null) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusUser = null;
                    reviewUserStep();
                }
            });
        }

        // Exchange Button
        ImageButton exchangeButton = (ImageButton) findViewById(R.id.exchange_button);
        if (exchangeButton != null) {
            exchangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO

                }
            });
        }

        // Stamp Button
        ImageButton stampButton = (ImageButton) findViewById(R.id.stamp_button);
        if (stampButton != null) {
            stampButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add points

                }
            });
        }

    }

    private void reviewUserStep() {
        if (focusUser != null){
            // View control
            phoneEditText.clearFocus();
            enterButton.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.VISIBLE);
            userInfoLayout.setVisibility(View.VISIBLE);

            // Query for user's point
            Query queryRef = pointRef.orderByChild("owner").equalTo(focusUser.getId());
            queryRef.addValueEventListener(userPointsListener);


        } else {
            // View control
            enterButton.setVisibility(View.VISIBLE);
            userInfoLayout.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.GONE);
        }
    }


    ValueEventListener userPointsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.d(JJCutPointCard.TAG, "points count = " + dataSnapshot.getChildrenCount());
            // TODO Show points

            if (dataSnapshot.getValue() != null){
                // TODO Show name
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(JJCutPointCard.TAG,"Query user's points failed.");
        }
    };

}
