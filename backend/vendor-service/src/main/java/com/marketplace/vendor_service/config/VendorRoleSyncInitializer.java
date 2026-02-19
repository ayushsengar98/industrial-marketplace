package com.marketplace.vendor_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marketplace.vendor_service.service.VendorService;

@Configuration
public class VendorRoleSyncInitializer {

    @Bean
    CommandLineRunner syncApprovedVendorsOnStartup(VendorService vendorService) {
        return args -> {
            vendorService.syncApprovedVendorRoles();
            vendorService.backfillMissingVendorProfilesFromAuth();
        };
    }
}
