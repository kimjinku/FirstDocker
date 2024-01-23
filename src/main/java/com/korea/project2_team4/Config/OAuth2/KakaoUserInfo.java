package com.korea.project2_team4.Config.OAuth2;

import java.util.Map;
public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override //스프링시큐리티 OAuth에서 필수요청사항임. -> oauthuserinfo에 해놔서.
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("nickname"));
    }

    @Override
    public String getImage()
    {
        return String.valueOf(attributes.get("picture"));
    }
}
