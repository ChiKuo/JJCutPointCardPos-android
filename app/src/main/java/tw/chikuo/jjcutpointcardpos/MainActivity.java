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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Firebase rootRef;
    private Firebase userRef;
    private Firebase pointRef;
    private User focusUser;

    private TextView nameTextView;
    private TextView pointsTextView;
    private TextView branchTextView;
//    private Button enterButton;
    private LinearLayout actionLayout;
    private LinearLayout userInfoLayout;
    private LinearLayout controlLayout;

    private EditText phoneEditText;

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

        initControlButton();

        actionLayout = (LinearLayout) findViewById(R.id.action_layout);
        userInfoLayout = (LinearLayout) findViewById(R.id.user_info_layout);
        controlLayout = (LinearLayout) findViewById(R.id.control_layout);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        pointsTextView = (TextView) findViewById(R.id.point_counts_text_view);

        // Branch name
        branchTextView = (TextView) findViewById(R.id.branch_text_view);
        if (branchTextView != null){
            branchTextView.setText(JJCutPointCard.CURRENT_BRANCH_NAME);
        }

        // Phone
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

        // Done Button
        ImageButton doneButton = (ImageButton) findViewById(R.id.done_button);
        if (doneButton != null) {
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusUser = null;
                    reviewUserStep();
                    // Clean User Info
                    phoneEditText.setText("");
                    pointsTextView.setText("");
                    nameTextView.setText("");
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
                    if (focusUser != null && focusUser.getId() != null){
                        Point point = new Point(focusUser.getId());
                        pointRef.push().setValue(point);
                        Log.d(JJCutPointCard.TAG,"point = " + point.toString());
                    }
                }
            });
        }

    }

    private void submitForQuery(final String phone) {

        controlLayout.setVisibility(View.GONE);

        final Query queryRef = userRef.orderByChild("phone").equalTo(phone);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    // Save new user
                    focusUser = new User(phone);
                    userRef.push().setValue(focusUser);

                    // Query again to find user id
                    submitForQuery(phone);

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
                Log.d(JJCutPointCard.TAG, "Query user phone failed.");
                controlLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void reviewUserStep() {
        if (focusUser != null){
            // View control
            controlLayout.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.VISIBLE);
            actionLayout.setVisibility(View.VISIBLE);

            // Query for user's point
            Query queryRef = pointRef.orderByChild("owner").equalTo(focusUser.getId());
            queryRef.addValueEventListener(userPointsListener);

        } else {
            // View control
            controlLayout.setVisibility(View.VISIBLE);
            userInfoLayout.setVisibility(View.GONE);
            actionLayout.setVisibility(View.GONE);
        }
    }



    ValueEventListener userPointsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // Show points
            pointsTextView.setText(getString(R.string.points_count, dataSnapshot.getChildrenCount()));
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                Point point = snapshot.getValue(Point.class);
//                Log.d(JJCutPointCard.TAG, "point = " + point.toString());
//            }

            // Show name
            if (focusUser != null && focusUser.getName() != null){
                nameTextView.setText(focusUser.getName());
            } else {
                nameTextView.setText(getString(R.string.user_name_null));
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(JJCutPointCard.TAG,"Query user's points failed.");
        }
    };

    View.OnClickListener controlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.zero:
                    phoneEditText.setText(phoneEditText.getText() + "0");
                    break;
                case R.id.one:
                    phoneEditText.setText(phoneEditText.getText() + "1");
                    break;
                case R.id.two:
                    phoneEditText.setText(phoneEditText.getText() + "2");
                    break;
                case R.id.three:
                    phoneEditText.setText(phoneEditText.getText() + "3");
                    break;
                case R.id.four:
                    phoneEditText.setText(phoneEditText.getText() + "4");
                    break;
                case R.id.five:
                    phoneEditText.setText(phoneEditText.getText() + "5");
                    break;
                case R.id.six:
                    phoneEditText.setText(phoneEditText.getText() + "6");
                    break;
                case R.id.seven:
                    phoneEditText.setText(phoneEditText.getText() + "7");
                    break;
                case R.id.eight:
                    phoneEditText.setText(phoneEditText.getText() + "8");
                    break;
                case R.id.nine:
                    phoneEditText.setText(phoneEditText.getText() + "9");
                    break;
                case R.id.ac:
                    phoneEditText.setText("");
                    break;
                case R.id.enter:
                    // TODO

                    if (phoneEditText != null && !phoneEditText.getText().toString().equals("")
                            && phoneEditText.getText().toString().length() == 10){
                        // Query for user
                        final String phone = phoneEditText.getText().toString();
                        submitForQuery(phone);
                    } else {
                        Toast.makeText(MainActivity.this, "輸入格式錯誤喔！", Toast.LENGTH_SHORT).show();
                    }



            }
        }
    };

    private void initControlButton() {
        Button zero = (Button) findViewById(R.id.zero);
        Button one = (Button) findViewById(R.id.one);
        Button two = (Button) findViewById(R.id.two);
        Button three = (Button) findViewById(R.id.three);
        Button four = (Button) findViewById(R.id.four);
        Button five = (Button) findViewById(R.id.five);
        Button six = (Button) findViewById(R.id.six);
        Button seven = (Button) findViewById(R.id.seven);
        Button eight = (Button) findViewById(R.id.eight);
        Button nine = (Button) findViewById(R.id.nine);
        Button ac = (Button) findViewById(R.id.ac);
        Button enter = (Button) findViewById(R.id.enter);
        if (zero != null) {
            zero.setOnClickListener(controlButtonListener);
        }
        if (one != null) {
            one.setOnClickListener(controlButtonListener);
        }
        if (two != null) {
            two.setOnClickListener(controlButtonListener);
        }
        if (three != null) {
            three.setOnClickListener(controlButtonListener);
        }
        if (four != null) {
            four.setOnClickListener(controlButtonListener);
        }
        if (five != null) {
            five.setOnClickListener(controlButtonListener);
        }
        if (six != null) {
            six.setOnClickListener(controlButtonListener);
        }
        if (seven != null) {
            seven.setOnClickListener(controlButtonListener);
        }
        if (eight != null) {
            eight.setOnClickListener(controlButtonListener);
        }
        if (nine != null) {
            nine.setOnClickListener(controlButtonListener);
        }
        if (ac != null) {
            ac.setOnClickListener(controlButtonListener);
        }
        if (enter != null) {
            enter.setOnClickListener(controlButtonListener);
        }
    }
}
