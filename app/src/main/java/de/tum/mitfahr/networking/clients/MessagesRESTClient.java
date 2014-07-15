package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.CreateMessageEvent;
import de.tum.mitfahr.networking.api.MessagesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.MessageRequest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class MessagesRESTClient extends AbstractRESTClient {

    private MessagesAPIService messagesAPIService;

    public MessagesRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        messagesAPIService = mRestAdapter.create(MessagesAPIService.class);
    }

    public void createMessage(String userAPIKey, int rideId, int conversationId,
                              MessageRequest message) {
        messagesAPIService.createMessage(userAPIKey, rideId, conversationId, message,
                createMessageCallback);
    }

    private Callback createMessageCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new CreateMessageEvent(CreateMessageEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };


}
