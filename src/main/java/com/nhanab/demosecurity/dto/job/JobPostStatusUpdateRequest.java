package com.nhanab.demosecurity.dto.job;

public class JobPostStatusUpdateRequest {
    private boolean status;

    public JobPostStatusUpdateRequest() {}

    public JobPostStatusUpdateRequest(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
