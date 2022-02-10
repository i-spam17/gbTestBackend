package hw4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "success",
        "status"
})

@Data
public class DataJson<AnyData> {
    @JsonProperty("data")
    private AnyData data;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("status")
    private Integer status;
}