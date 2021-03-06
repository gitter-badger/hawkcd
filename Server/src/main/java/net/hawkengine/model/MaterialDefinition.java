package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.hawkengine.model.enums.MaterialType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitMaterial.class, name = "GIT")})
public abstract class MaterialDefinition extends DbEntry {
    private String name;
    private String errorMessage;
    private MaterialType type;
    private boolean isPollingForChanges;

    public MaterialDefinition(){
        this.setErrorMessage("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MaterialType getType() {
        return this.type;
    }

    public void setType(MaterialType value) {
        this.type = value;
    }

    @JsonProperty("isPollingForChanges")
    public boolean isPollingForChanges() {
        return this.isPollingForChanges;
    }

    public void setPollingForChanges(boolean pollingForChanges) {
        this.isPollingForChanges = pollingForChanges;
    }
}
