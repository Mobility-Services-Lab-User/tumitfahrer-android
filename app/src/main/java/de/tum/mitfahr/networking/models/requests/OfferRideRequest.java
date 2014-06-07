package de.tum.mitfahr.networking.models.requests;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideRequest {

    String departurePlace;
    String destination;
    String meetingPoint;
    String freeSeats;
    String departureTime;
    int rideType;

    public OfferRideRequest(String departure, String destination, String meetingPoint,
                            String freeSeats, String dateTime, int rideType) {
        this.departurePlace = departure;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = dateTime;
        this.rideType = rideType;
    }
}