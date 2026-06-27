package com.pradeep.slms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClerkWebhookPayloadDTO {
    private String type;
    private ClerkUserData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClerkUserData {
        private String id;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("email_addresses")
        private List<EmailAddress> emailAddresses;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class EmailAddress {
            @JsonProperty("email_address")
            private String emailAddress;
        }
    }
}
