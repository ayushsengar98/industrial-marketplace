// VendorResponse.java
package com.marketplace.vendor_service.DTO;

import com.marketplace.vendor_service.model.Vendor;

public class VendorResponse {
    private Long id;
    private String email;
    private String companyName;
    private String gstNumber;
    private String status;

    public static VendorResponse fromEntity(Vendor vendor) {
        VendorResponse response = new VendorResponse();
        response.setId(vendor.getId());
        response.setEmail(vendor.getEmail());
        response.setCompanyName(vendor.getCompanyName());
        response.setGstNumber(vendor.getGstNumber());
        response.setStatus(vendor.getStatus().name());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}