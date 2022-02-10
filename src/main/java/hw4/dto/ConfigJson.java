package hw4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "safeFlags",
        "highRiskFlags",
        "unsafeFlags",
        "wallUnsafeFlags",
        "showsAds"
})

@Data
public class ConfigJson<AnyData> {
    @JsonProperty("safeFlags")
    private List<String> safeFlags = new ArrayList<String>();
    @JsonProperty("highRiskFlags")
    private List<Object> highRiskFlags = new ArrayList<Object>();
    @JsonProperty("unsafeFlags")
    private List<String> unsafeFlags = new ArrayList<String>();
    @JsonProperty("wallUnsafeFlags")
    private List<Object> wallUnsafeFlags = new ArrayList<Object>();
    @JsonProperty("showsAds")
    private Boolean showsAds;
}