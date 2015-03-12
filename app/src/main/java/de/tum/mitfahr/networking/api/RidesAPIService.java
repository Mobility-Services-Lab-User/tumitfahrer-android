package de.tum.mitfahr.networking.api;

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
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by amr on 18/05/14.
 */
public interface RidesAPIService {

    @POST("/users/{id}/rides")
    public void offerRide(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            @Body OfferRideRequest ride,
            Callback<OfferRideResponse> callback
    );

    @GET("/rides/{id}")
    public void getRide(
            @Header("apiKey") String apiKey,
            @Path("id") int rideId,
            Callback<RideResponse> callback
    );

    @GET("/rides/{id}")
    public RideResponse getRideSynchronous(
            @Header("apiKey") String apiKey,
            @Path("id") int rideId
    );

    @PUT("/users/{userId}/rides/{rideId}")
    public void updateRide(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Body Ride ride,
            Callback<RideResponse> callback
    );

    @GET("/users/{id}/rides?driver=true")
    public void getMyRidesAsDriver(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides?passenger=true")
    public void getMyRidesAsPassenger(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides?past=true")
    public void getMyRidesPast(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @PUT("/users/{userId}/rides/{rideId}removed_passenger")
    public void removePassenger(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Query("removed_passenger") int removedPassengerId,
            Callback<RideResponse> callback
    );

    @DELETE("/users/{userId}/rides/{rideId}")
    public void deleteRide(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            Callback<DeleteRideResponse> callback
    );

    @POST("/rides/{rideId}/requests")
    public void joinRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Body JoinRideReqest request,
            Callback<JoinRequestResponse> callback
    );

    @PUT("/rides/{rideId}/requests/{requestId}")
    public void acceptRideRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            @Body AcceptRideRequest request,
            Callback<AcceptRideResponse> callback
    );

    @DELETE("/rides/{rideId}/requests/{requestId}")
    public void rejectRideRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            Callback<RejectRideResponse> callback
    );


    @GET("/rides/{rideId}/requests")
    public void getRideRequests(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            Callback<RequestsResponse> callback
    );

    @GET("/users/{userId}/requests")
    public void getUserRequests(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            Callback<RequestsResponse> callback
    );

    @DELETE("/rides/{rideId}/requests/{requestId}")
    public void deleteRideRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            Callback<RequestsResponse> callback
    );

    @GET("/rides?page={pageNo}")
    public void getPage(
            @Header("apiKey") String apiKey,
            @Path("pageNo") int pageNo,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRides(
            @Header("apiKey") String apiKey,
            @Query("from_date") String fromDate,
            @Query("ride_type") int rideType,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRides(
            @Header("apiKey") String apiKey,
            @Query("ride_type") int rideType,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRidesPaged(
            @Header("apiKey") String apiKey,
            @Query("ride_type") int rideType,
            @Query("page") int page,
            Callback<RidesResponse> callback
    );
}
