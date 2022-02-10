package hw4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.annotation.Generated;

@Data
public class AccountGetInfo extends DataJson<AccountGetInfo.AccauntGetInfoData> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "url",
            "bio",
            "avatar",
            "avatar_name",
            "cover",
            "cover_name",
            "reputation",
            "reputation_name",
            "created",
            "pro_expiration",
            "user_follow",
            "is_blocked"
    })

    @Data
    @Generated("jsonschema2pojo")
    public class AccauntGetInfoData extends DataJson<AccauntGetInfoData.UserFollow> {
        @JsonProperty("id")
        private String id;
        @JsonProperty("url")
        private String url;
        @JsonProperty("bio")
        private Object bio;
        @JsonProperty("avatar")
        private String avatar;
        @JsonProperty("avatar_name")
        private String avatarName;
        @JsonProperty("cover")
        private String cover;
        @JsonProperty("cover_name")
        private String coverName;
        @JsonProperty("reputation")
        private Integer reputation;
        @JsonProperty("reputation_name")
        private String reputationName;
        @JsonProperty("created")
        private Integer created;
        @JsonProperty("pro_expiration")
        private Boolean proExpiration;
        @JsonProperty("user_follow")
        private UserFollow userFollow;
        @JsonProperty("is_blocked")
        private Boolean isBlocked;


        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonPropertyOrder({
                "status"
        })
        @Data
        @Generated("jsonschema2pojo")
        public class UserFollow {
            private Boolean status;
        }
    }
}
