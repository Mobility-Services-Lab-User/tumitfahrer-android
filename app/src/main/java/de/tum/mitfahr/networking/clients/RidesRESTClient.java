package de.tum.mitfahr.networking.clients;

import android.util.Log;

import java.util.List;
import java.util.Objects;

import de.tum.mitfahr.events.AcceptRequestEvent;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.GetRideRequestsEvent;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.events.GetRidesEvent;
import de.tum.mitfahr.events.GetRidesPageEvent;
import de.tum.mitfahr.events.GetUserRequestsEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.MyRidesAsDriverEvent;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.events.MyRidesPastEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RejectRequestEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.AcceptRideRequest;
import de.tum.mitfahr.networking.models.requests.JoinRideReqest;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.requests.RespondRideReqest;
import de.tum.mitfahr.networking.models.response.AcceptRideResponse;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RejectRideResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 18/05/14.
 */
public class RidesRESTClient extends AbstractRESTClient {

    private RidesAPIService ridesAPIService;

    public RidesRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        ridesAPIService = mRestAdapter.create(RidesAPIService.class);
    }

    public void offerRide(final String departure,
                          final String destination,
                          final String meetingPoint,
                          final int freeSeats,
                          final String dateTime,
                          final String userAPIKey,
                          final int rideType,
                          final int userId,
                          final String isDriving,
                          final String car,
                          final List<String> repeatDates) {
        OfferRideRequest requestData = new OfferRideRequest(departure, destination, meetingPoint, freeSeats, dateTime, rideType, isDriving, car,repeatDates);
        ridesAPIService.offerRide(userAPIKey, userId, requestData, offerRideCallback);
    }

    private Callback<OfferRideResponse> offerRideCallback = new Callback<OfferRideResponse>() {

        @Override
        public void success(OfferRideResponse offerRideResponse, Response response) {
            mBus.post(new OfferRideEvent(OfferRideEvent.Type.RESULT, offerRideResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getRide(String userAPIKey, int rideId) {
        ridesAPIService.getRide(userAPIKey, rideId, getRideCallback);
    }

    private Callback<RideResponse> getRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new GetRideEvent(GetRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public Ride getRideSynchronous(String userAPIKey, int rideId) {
        RideResponse response = ridesAPIService.getRideSynchronous(userAPIKey, rideId);
        if (null != response && null != response.getRide()) {
            return response.getRide();
        }
        return null;
    }

    public void updateRide(String userAPIKey, int userId, Ride updatedRide) {
        ridesAPIService.updateRide(userAPIKey, userId, updatedRide.getId(), updatedRide, updateRideCallback);
    }

    private Callback<RideResponse> updateRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getMyRidesAsDriver(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesAsDriver(userAPIKey, userId, getMyRidesAsDriverCallback);
    }

    private Callback<RidesResponse> getMyRidesAsDriverCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getMyRidesAsPassenger(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesAsPassenger(userAPIKey, userId, getMyRidesAsPassengerCallback);
    }

    private Callback<RidesResponse> getMyRidesAsPassengerCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getMyRidesPast(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesPast(userAPIKey, userId, getMyRidesPastCallback);
    }

    private Callback<RidesResponse> getMyRidesPastCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getPage(String userAPIKey, int pageNo) {
        ridesAPIService.getPage(userAPIKey, pageNo, getPageCallback);
    }

    private Callback<RidesResponse> getPageCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    // Doc not clear
    public void getRides(String userAPIKey, String fromDate, int rideType) {
        ridesAPIService.getRides(userAPIKey, fromDate, rideType, getRidesCallback);
    }

    private Callback<RidesResponse> getRidesCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getRidesPaged(String userAPIKey, int rideType, int page) {
        ridesAPIService.getRidesPaged(userAPIKey, rideType, page, getRidePagedCallback);
    }

    private Callback<RidesResponse> getRidePagedCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getAllRides(String userAPIKey, int rideType) {
        ridesAPIService.getRides(userAPIKey, rideType, getAllRideCallback);
    }

    private Callback<RidesResponse> getAllRideCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesEvent(GetRidesEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void removePassenger(String userAPIKey, int userId, int rideId, int removedPassengerId) {
        ridesAPIService.removePassenger(userAPIKey, userId, rideId, removedPassengerId, removePassengerCallback);
    }

    private Callback removePassengerCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };


    public void deleteRide(final String userAPIKey, final int userId, final int rideId) {
        ridesAPIService.deleteRide(userAPIKey, userId, rideId, deleteRideCallback);
    }

    private Callback<DeleteRideResponse> deleteRideCallback = new Callback<DeleteRideResponse>() {

        @Override
        public void success(DeleteRideResponse deleteRideResponse, Response response) {
            mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.RESULT, deleteRideResponse, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void joinRequest(int rideId, int passengerId, String userAPIKey) {
        JoinRideReqest joinRideReqest = new JoinRideReqest(passengerId);
        ridesAPIService.joinRequest(userAPIKey, rideId, joinRideReqest, joinRequestCallback);
    }

    private Callback<JoinRequestResponse> joinRequestCallback = new Callback<JoinRequestResponse>() {
        @Override
        public void success(JoinRequestResponse joinRequestResponse, Response response) {
            mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.RESULT, joinRequestResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void acceptRequest(int rideId, int passengerId, int requestId, String userAPIKey) {
        AcceptRideRequest request = new AcceptRideRequest(passengerId,1);
        ridesAPIService.acceptRideRequest(userAPIKey, rideId, requestId, request, acceptRequestCallback);
    }

    public void rejectRequest(int rideId, int requestId, String userAPIKey) {
        ridesAPIService.rejectRideRequest(userAPIKey, rideId, requestId, rejectRequestCallback);
    }

    private Callback<RejectRideResponse> rejectRequestCallback = new Callback<RejectRideResponse>() {
        @Override
        public void success(RejectRideResponse o, Response response) {
            Log.e("RidesClient", "RejectCallback");
            Log.e("RidesService", "RejectCode:" + response.getStatus());
            if(response.getStatus() == 200) {
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_SENT, response));
            }else{
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_FAILED, response));
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    private Callback<AcceptRideResponse> acceptRequestCallback = new Callback<AcceptRideResponse>() {
        @Override
        public void success(AcceptRideResponse o, Response response) {
            Log.e("RidesClient", "AcceptCallback");
            Log.e("RidesService", "AcceptCode:" + response.getStatus());
            if(response.getStatus() == 200) {
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_SENT, response));
            }else{
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_FAILED, response));
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getRideRequests(String userAPIKey, int rideId) {
        ridesAPIService.getRideRequests(userAPIKey, rideId, getRideRequestsCallback);
    }

    private Callback<RequestsResponse> getRideRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
            mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getUserRequests(String userAPIKey, int userId) {
        ridesAPIService.getUserRequests(userAPIKey, userId, getUserRequestsCallback);
    }

    private Callback<RequestsResponse> getUserRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
            mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void deleteRideRequest(String userAPIKey, int rideId, int requestId) {
        ridesAPIService.deleteRideRequest(userAPIKey, rideId, requestId, deleteRideRequestCallback);
    }

    private Callback deleteRideRequestCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
