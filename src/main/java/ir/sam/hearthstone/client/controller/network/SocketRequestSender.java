package ir.sam.hearthstone.client.controller.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.sam.hearthstone.client.controller.network.serializer_and_deserializer.Deserializer;
import ir.sam.hearthstone.client.controller.network.serializer_and_deserializer.Serializer;
import ir.sam.hearthstone.client.controller.network.serializer_and_deserializer
        .SerializerAndDeserializerConstants;
import ir.sam.hearthstone.client.model.main.CardOverview;
import ir.sam.hearthstone.client.model.main.Overview;
import ir.sam.hearthstone.client.model.main.UnitOverview;
import ir.sam.hearthstone.client.model.requests.Request;
import ir.sam.hearthstone.client.model.response.LoginResponse;
import ir.sam.hearthstone.client.model.response.Logout;
import ir.sam.hearthstone.client.model.response.Response;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

public class SocketRequestSender implements RequestSender {
    private final Scanner scanner;
    private final PrintStream printStream;
    private final Gson gson;
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private String token;

    public SocketRequestSender(Socket socket) throws IOException {
        scanner = new Scanner(socket.getInputStream());
        printStream = new PrintStream(socket.getOutputStream());
        gson = new GsonBuilder()
                .registerTypeAdapter(Request.class, new Serializer<>())
                .registerTypeAdapter(Response.class
                        , new Deserializer<>(SerializerAndDeserializerConstants.RESPONSE_PACKAGE))
                .registerTypeAdapter(Overview.class
                        , new Deserializer<>(SerializerAndDeserializerConstants.OVERVIEW_PACKAGE))
                .registerTypeAdapter(UnitOverview.class
                        , new Deserializer<>(SerializerAndDeserializerConstants.OVERVIEW_PACKAGE))
                .registerTypeAdapter(CardOverview.class
                        , new Deserializer<>(SerializerAndDeserializerConstants.OVERVIEW_PACKAGE))
                .create();
    }

    @Override
    public Response[] sendRequest(Request request) {
        Message requestMessage = new Message(token, request);
        String encode = encoder.encodeToString(toJson(requestMessage).getBytes());
        printStream.println(encode);
        String json = scanner.nextLine();
        String decode = new String(decoder.decode(json.getBytes()));
        Message responseMessage = toMessage(decode);
        checkToken(responseMessage);
        return responseMessage.getResponses();
    }

    private String toJson(Message message) {
        return gson.toJson(message);
    }

    private Message toMessage(String json) {
        return gson.fromJson(json, Message.class);
    }

    private void checkToken(Message message) {
        if (message.getResponses() != null && message.getResponses().length > 0) {
            if (message.getResponses()[0] instanceof LoginResponse) {
                token = message.getToken();
            } else if (message.getResponses()[0] instanceof Logout) {
                token = null;
            }
        }
    }
}
