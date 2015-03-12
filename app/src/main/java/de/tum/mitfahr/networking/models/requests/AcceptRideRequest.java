package de.tum.mitfahr.networking.models.requests;

/**
 * Author: abhijith
 * Date: 27/02/15.
 */
public class AcceptRideRequest {
    int passengerId;
    int confirmed;


    public AcceptRideRequest(int passengerId, int confirmed) {
        this.passengerId = passengerId;
        this.confirmed = confirmed;
    }
}
