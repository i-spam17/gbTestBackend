package hw4.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AlbumUploadJson extends DataJson<AlbumUploadJson.AlbumUploadData> {
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class AlbumUploadData {
        @JsonProperty("id")
        private String id;
        @JsonProperty("deletehash")
        private String deletehash;
    }
}
