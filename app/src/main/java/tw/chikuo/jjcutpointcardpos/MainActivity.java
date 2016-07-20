package tw.chikuo.jjcutpointcardpos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Firebase library
        Firebase.setAndroidContext(this);
        Firebase rootRef = new Firebase("https://jjcutpointcard.firebaseio.com/");
        Firebase userRef = new Firebase("https://jjcutpointcard.firebaseio.com/users");



    }
}
