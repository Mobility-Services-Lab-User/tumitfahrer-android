package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.networking.models.Activities;
import de.tum.mitfahr.networking.models.ActivitiesRideRequest;
import de.tum.mitfahr.networking.models.ActivitiesRideSearch;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.util.TimelineItem;
import retrofit.RetrofitError;

/**
 * Created by abhijith on 22/05/14.
 */
public class TimelineFragment extends AbstractNavigationFragment {

    public static final int FRAGMENT_TYPE_ALL = 0;
    public static final int FRAGMENT_TYPE_AROUND = 1;
    public static final int FRAGMENT_TYPE_LAST_MINUTE = 2;
    private static final String TAG = TimelineFragment.class.getName();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TimelineFragment newInstance(int sectionNumber) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager pager;

    private TimelinePagerAdapter adapter;
    private TimelineListAllFragment mTimelineAllFragment;
    private TimelineListAroundFragment mTimelineAroundFragment;
    private TimelineListLastMinuteFragment mTimelineNearbyFragment;

    public TimelineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.inject(this, rootView);
        adapter = new TimelinePagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        tabs.setViewPager(pager);
        changeActionBarColor(getResources().getColor(R.color.blue1));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTimelineAllFragment = TimelineListAllFragment.newInstance();
        mTimelineAroundFragment = TimelineListAroundFragment.newInstance();
        mTimelineNearbyFragment = TimelineListLastMinuteFragment.newInstance();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
    }

    public class TimelinePagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"All", "Around Me", "Last Minute"};

        public TimelinePagerAdapter(FragmentManager fm) {
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
                return mTimelineAllFragment;
            else if (position == 1)
                return mTimelineAroundFragment;
            else
                return mTimelineNearbyFragment;
        }
    }

    @Subscribe
    public void onGetActivities(GetActivitiesEvent result) {
        if (result.getType() == GetActivitiesEvent.Type.GET_SUCCESSFUL) {
            new CreateTimelineItemsTask(getActivity()).execute(result.getResponse().getActivities());
        } else if (result.getType() == GetActivitiesEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "GetFailed", Toast.LENGTH_SHORT).show();
        }
    }

    private class CreateTimelineItemsTask extends AsyncTask<Activities, Void, List<TimelineItem>> {

        Context localContext;

        public CreateTimelineItemsTask(Context context) {
            this.localContext = context;
        }

        @Override
        protected List<TimelineItem> doInBackground(Activities... params) {
            Activities activities = params[0];
            List<TimelineItem> items = new ArrayList<TimelineItem>();

            for (Ride ride : activities.getRides()) {
                items.add(new TimelineItem(ride, TimelineItem.TimelineItemType.RIDE_CREATED));
            }

            for (ActivitiesRideSearch rideSearch : activities.getRideSearches()) {
                items.add(new TimelineItem(TimelineItem.TimelineItemType.RIDE_SEARCHED,
                        rideSearch.getId(),
                        rideSearch.getDeparturePlace(),
                        rideSearch.getDestination(),
                        rideSearch.getCreatedAt()));
            }
            for (ActivitiesRideRequest rideRequest : activities.getRequests()) {
                try {
                    Ride ride = TUMitfahrApplication.getApplication(localContext).getRidesService().getRideSynchronous(rideRequest.getRideId());
                    if (null != ride) {
                        items.add(new TimelineItem(ride, TimelineItem.TimelineItemType.RIDE_REQUEST));
                        TUMitfahrApplication.getApplication(localContext).getRidesService().getRide(rideRequest.getRideId());
                    }
                } catch (RetrofitError e) {
                    Log.d(TAG, e.toString());
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<TimelineItem> timelineItems) {
            mTimelineAllFragment.setTimelineItems(timelineItems);
            mTimelineAroundFragment.setTimelineItems(timelineItems);
            mTimelineNearbyFragment.setTimelineItems(timelineItems);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(5);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
