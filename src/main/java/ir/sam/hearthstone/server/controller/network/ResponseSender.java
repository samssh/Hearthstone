package ir.sam.hearthstone.server.controller.network;

import ir.sam.hearthstone.server.model.requests.Request;
import ir.sam.hearthstone.server.model.response.Response;

public interface ResponseSender {
    Request getRequest();

    void sendResponse(Response... responses);

    void close();
}