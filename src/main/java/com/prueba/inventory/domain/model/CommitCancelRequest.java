package com.prueba.inventory.domain.model;

public class CommitCancelRequest {
    private String requestId;
    public CommitCancelRequest(String requestId) {
        this.requestId = requestId;
    }
    public String getRequestId() {
        return requestId;
    }
}
