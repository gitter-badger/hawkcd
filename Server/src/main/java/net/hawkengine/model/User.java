package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.hawkengine.model.payload.Permission;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public class User extends DbEntry {

    private String email;
    private String password;
    private String ghAuthCode;
    private String provider;
    private List<Permission> permissions;
    private List<String> userGroupIds;
    private boolean isEnabled;

    public User(){
        this.setPermissions(new ArrayList<>());
        this.setUserGroupIds(new ArrayList<>());
        this.setEnabled(true);
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email =  email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getGhAuthCode() {
        return ghAuthCode;
    }

    public void setGhAuthCode(String ghAuthCode) {
        this.ghAuthCode = ghAuthCode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<String> getUserGroupIds() {
        return this.userGroupIds;
    }

    public void setUserGroupIds(List<String> userGroupIds) {
        this.userGroupIds = userGroupIds;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    @JsonProperty("isEnabled")
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
