package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "permissions")
public class RolePermissionsConfig {

    private Map<String, String> permissions = new HashMap<>();

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }
}
