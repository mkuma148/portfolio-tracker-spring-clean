package com.crypto.tracker.dto.request;

public class CreateWalletRequest {

    private String label;
    private String walletAddress; // optional

    public String getLabel() {
        return label;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
