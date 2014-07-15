package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.MessageRequest;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by amr on 07.07.14.
 */
public interface MessagesAPIService {

    @POST("/rides/{rideId}/conversations/{conversationId}/messages")
    public void createMessage(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Path("conversationId") int conversationId,
            @Body MessageRequest messageRequest,
            Callback callback
    );
}
