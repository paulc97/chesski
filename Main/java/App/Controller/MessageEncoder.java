package Controller;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<MessageObj> {

        private static Gson gson = new Gson();

        @Override
        public String encode(MessageObj messageObj) throws EncodeException {
            return gson.toJson(messageObj);
        }

        @Override
        public void init(EndpointConfig endpointConfig) {
            // Custom initialization logic
        }

        @Override
        public void destroy() {
            // Close resources
        }
    }

