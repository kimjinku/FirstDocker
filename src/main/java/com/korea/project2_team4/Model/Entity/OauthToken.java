package com.korea.project2_team4.Model.Entity;

import lombok.Data;
import lombok.Getter;

@Data
public class OauthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;

}
