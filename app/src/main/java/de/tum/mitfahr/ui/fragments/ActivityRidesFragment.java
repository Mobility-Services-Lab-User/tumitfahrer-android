package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.events.GetRidesEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;

/**
 * Created by abhijith on 22/05/14.
 */
public class ActivityRidesFragment extends AbstractNavigationFragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager pager;

    private ActivityRidesPagerAdapter adapter;
    private RidesAllListFragment mRidesAllListFragment;
    private RidesAroundListFragment mRidesAroundListFragment;

    public ActivityRidesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ActivityRidesFragment newInstance(int sectionNumber) {
        ActivityRidesFragment fragment = new ActivityRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_rides, container, false);
        ButterKnife.inject(this, rootView);
        adapter = new ActivityRidesPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        tabs.setViewPager(pager);
        changeActionBarColor(getResources().getColor(R.color.blue2));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRidesAllListFragment = RidesAllListFragment.newInstance(1);
        mRidesAroundListFragment = RidesAroundListFragment.newInstance(1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        outputFormat.setTimeZone(TimeZone.getDefault());
        String fromDate = outputFormat.format(calendar.getTime());

        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(1);
    }

    @Subscribe
    public void onActivityRidesEvent(GetRidesEvent result) {
        if (result.getType() == GetRidesEvent.Type.GET_SUCCESSFUL) {
            mRidesAllListFragment.setRides(result.getResponse().getRides());
            mRidesAroundListFragment.setRides(result.getResponse().getRides());
        } else if (result.getType() == GetRidesEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Fetching Rides Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onGetDateResult(GetRidesDateEvent result) {
        if (result.getType() == GetRidesDateEvent.Type.GET_SUCCESSFUL) {
            List<Ride> refreshedRides = result.getResponse().getRides();
            if (refreshedRides == null || refreshedRides.size() > 0) {
                Toast.makeText(getActivity(), "No new rides", Toast.LENGTH_SHORT).show();
            } else {
                mRidesAllListFragment.setRefreshedRides(refreshedRides);
                mRidesAroundListFragment.setRefreshedRides(refreshedRides);
            }
        } else if (result.getType() == GetRidesDateEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Get new rides failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(5);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public class ActivityRidesPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"All", "Around Me", "Get a car"};

        public ActivityRidesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return mRidesAllListFragment;
            else if (position == 1)
                return mRidesAroundListFragment;
            else
                return CarSharingFragment.newInstance();
        }
    }
}
