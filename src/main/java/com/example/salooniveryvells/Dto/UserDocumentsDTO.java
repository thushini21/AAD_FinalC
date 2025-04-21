package com.example.salooniveryvells.Dto;

public class UserDocumentsDTO {
    private String idProofPath;
    private String addressProofPath;
    private String verificationStatus;

    public UserDocumentsDTO(String idProofPath, String addressProofPath, String verificationStatus) {
        this.idProofPath = idProofPath;
        this.addressProofPath = addressProofPath;
        this.verificationStatus = verificationStatus;
    }

    // Getters
    public String getIdProofPath() {
        return idProofPath;
    }

    public String getAddressProofPath() {
        return addressProofPath;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}
