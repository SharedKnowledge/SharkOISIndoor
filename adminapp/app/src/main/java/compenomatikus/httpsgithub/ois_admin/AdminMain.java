package compenomatikus.httpsgithub.ois_admin;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


public class AdminMain extends AppCompatActivity implements AdminFragment.OnFragmentInteractionListener{

    public final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Do not disturb! (╯°□°）╯︵ ┻━┻)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AdminFragment adminFragment = new AdminFragment();
        fragmentTransaction.add(R.id.fragment, adminFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Resume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "Stop.");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroid");
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}