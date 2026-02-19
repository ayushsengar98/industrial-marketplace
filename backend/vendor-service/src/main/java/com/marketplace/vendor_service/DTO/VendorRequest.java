// VendorRequest.java
package com.marketplace.vendor_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VendorRequest {
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2-100 characters")
    private String companyName;

    @NotBlank(message = "GST number is required")
    @Size(min = 15, max = 15, message = "GST number must be 15 characters")
    private String gstNumber;

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
}