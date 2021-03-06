package de.tum.mitfahr.services;

import android.content.Context;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.clients.SearchRESTClient;
import de.tum.mitfahr.networking.models.response.SearchResponse;

/**
 * Created by amr on 31/05/14.
 */
public class SearchService extends AbstractService {

    private SearchRESTClient mSearchRESTClient;
    private Bus mBus;

    public SearchService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mSearchRESTClient = new SearchRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void search(String from, int fromThreshold, String to, int toThreshold, String dateTime, int rideType) {
        String userAPIKey = mSharedPreferences.getString("api_key", null);
        mSearchRESTClient.search(userAPIKey, from, fromThreshold, to, toThreshold, dateTime, rideType);
    }

    @Subscribe
    public void onSearchResult(SearchEvent result) {
        if (result.getType() == SearchEvent.Type.RESULT) {
            SearchResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_FAILED, response));
            } else {
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_SUCCESSFUL, response));
            }
        }
    }
}
