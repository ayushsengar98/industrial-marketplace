package com.marketplace.auth_service.dto;

public class VendorApplyRequest {

    private String companyName;
    private String gstNumber;

    public String getCompanyName(){ return companyName; }
    public void setCompanyName(String companyName){ this.companyName=companyName; }

    public String getGstNumber(){ return gstNumber; }
    public void setGstNumber(String gstNumber){ this.gstNumber=gstNumber; }
}
