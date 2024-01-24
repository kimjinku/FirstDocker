package com.korea.project2_team4.Model.Entity;

import lombok.Data;

@Data
public class KaKaoProfile {

    public Long id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;
    public static class KakaoAccount {
        public Boolean profile_nickname_needs_agreement;

        public Profile profile;
        public static class Profile {
            public String nickname;
        }

    }

    public static class Properties {
        public String nickname;

    }

}




