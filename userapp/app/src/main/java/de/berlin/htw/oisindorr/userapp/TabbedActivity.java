package de.berlin.htw.oisindorr.userapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindorr.userapp.fragments.DetailFragment;
import de.berlin.htw.oisindorr.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindorr.userapp.model.GeoCoordinate;

public class TabbedActivity extends AppCompatActivity implements NoteFragment.OnListFragmentInteractionListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    @Bind(R.id.ac_tabbed_vp) ViewPager viewPager;
    @Bind(R.id.ac_tabbed_main_content) View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_tabbed);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_tabbed_toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.ac_tabbed_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ac_tabbed_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUIMessage("Floating Action Button");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showUIMessage("Settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onListClicked(@NonNull GeoCoordinate item) {
        showUIMessage(item.toString());
    }

    private void showUIMessage(CharSequence s) {
        Snackbar.make(content, s, Snackbar.LENGTH_SHORT).show();
    }

    private void showUIMessage(@StringRes int res) {
        Snackbar.make(content, res, Snackbar.LENGTH_SHORT).show();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DetailFragment.newInstance();
                case 1:
                    return NoteFragment.newInstance(GeoCoordinate.ITEMS);
                default:
                    throw new IllegalArgumentException("wrong Position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Positioning";
                case 1:
                    return "Notes";
                default:
                    throw new IllegalArgumentException("wrong Position: " + position);
            }
        }
    }
}
