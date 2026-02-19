package com.marketplace.vendor_service.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.marketplace.vendor_service.model.Vendor;
import com.marketplace.vendor_service.repository.VendorRepository;

@Service
public class VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorService.class);
    private final VendorRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${auth.internal.api-key:internal-dev-key}")
    private String internalApiKey;

    public VendorService(VendorRepository repo){
        this.repo = repo;
    }

    @Transactional
    public Vendor apply(Vendor v, String email){
        // Check if already applied
        if (repo.findByEmail(email).isPresent()) {
            throw new RuntimeException("You have already applied for vendor status");
        }
        
        // Validate GST number (basic check)
        if (v.getGstNumber() == null || v.getGstNumber().length() < 10) {
            throw new RuntimeException("Invalid GST number");
        }
        
        v.setEmail(email);
        v.setStatus(Vendor.Status.PENDING);
        return repo.save(v);
    }

    public Vendor status(String email){
        Vendor vendor = repo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Vendor application not found"));

        // Self-heal old records: approved vendor rows created before role sync was added.
        if (vendor.getStatus() == Vendor.Status.APPROVED) {
            tryPromoteUserToVendor(email);
        }

        return vendor;
    }

    public List<Vendor> pending(){
        return repo.findByStatus(Vendor.Status.PENDING);
    }

    @Transactional
    public Vendor approve(Long id){
        Vendor v = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        
        if (v.getStatus() != Vendor.Status.PENDING) {
            throw new RuntimeException("Can only approve pending applications");
        }

        promoteUserToVendor(v.getEmail());
        
        v.setStatus(Vendor.Status.APPROVED);
        return repo.save(v);
    }

    @Transactional
    public Vendor reject(Long id){
        Vendor v = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        
        if (v.getStatus() != Vendor.Status.PENDING) {
            throw new RuntimeException("Can only reject pending applications");
        }
        
        v.setStatus(Vendor.Status.REJECTED);
        return repo.save(v);
    }
    
    public List<Vendor> getApprovedVendors() {
        return repo.findByStatus(Vendor.Status.APPROVED);
    }
    
    public List<Vendor> getRejectedVendors() {
        return repo.findByStatus(Vendor.Status.REJECTED);
    }
    
    public long getPendingCount() {
        return repo.countByStatus(Vendor.Status.PENDING);
    }

    @Transactional(readOnly = true)
    public void syncApprovedVendorRoles() {
        List<Vendor> approvedVendors = repo.findByStatus(Vendor.Status.APPROVED);
        for (Vendor vendor : approvedVendors) {
            tryPromoteUserToVendor(vendor.getEmail());
        }
        log.info("Vendor role sync completed for {} approved vendors", approvedVendors.size());
    }

    @Transactional
    public void backfillMissingVendorProfilesFromAuth() {
        List<String> vendorEmails = fetchEmailsByRoleFromAuth("VENDOR");
        int created = 0;

        for (String email : vendorEmails) {
            if (repo.findByEmail(email).isPresent()) {
                continue;
            }

            Vendor vendor = new Vendor();
            vendor.setEmail(email);
            vendor.setCompanyName(buildDefaultCompanyName(email));
            vendor.setGstNumber("PENDING-UPDATE");
            vendor.setStatus(Vendor.Status.APPROVED);
            repo.save(vendor);
            created++;
        }

        log.info("Vendor profile backfill completed. Created {} missing profile(s) from {} VENDOR account(s).", created, vendorEmails.size());
    }

    private void promoteUserToVendor(String email) {
        String encodedEmail = UriUtils.encodePathSegment(email, java.nio.charset.StandardCharsets.UTF_8);
        String url = authServiceUrl + "/internal/users/" + encodedEmail + "/role";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Api-Key", internalApiKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("role", "VENDOR"), headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to sync user role with auth-service");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to promote user to vendor: " + ex.getMessage());
        }
    }

    private void tryPromoteUserToVendor(String email) {
        try {
            promoteUserToVendor(email);
        } catch (Exception ex) {
            log.warn("Role sync retry failed for approved vendor {}: {}", email, ex.getMessage());
        }
    }

    private List<String> fetchEmailsByRoleFromAuth(String role) {
        String url = authServiceUrl + "/internal/users?role=" + UriUtils.encodeQueryParam(role, java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            List<?> body = response.getBody();
            if (body == null) {
                return List.of();
            }

            return body.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(item -> item.get("email"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to fetch users by role {} from auth-service: {}", role, ex.getMessage());
            return List.of();
        }
    }

    private String buildDefaultCompanyName(String email) {
        int atIndex = email.indexOf('@');
        String prefix = atIndex > 0 ? email.substring(0, atIndex) : email;
        if (prefix.isBlank()) {
            return "Vendor Account";
        }
        return prefix + " Ventures";
    }
}
