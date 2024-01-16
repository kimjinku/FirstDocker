package com.korea.project2_team4.Config;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Profile;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private Member member;
    private Profile profile;
    private Map<String, Object> attributes;



    //일반 로그인 생성자
    public PrincipalDetails(Member member) {
        this.member = member;
        this.profile = member.getProfile();
    }
    // 차단 여부
    private boolean isBlocked;

    // 차단 해제 날짜
    private LocalDateTime unblockDate;

    //OAuth 로그인 생성자
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
        this.isBlocked = false;
        this.unblockDate = null;
    }
    // 차단 여부


    /**
     * OAuth2Member 인터페이스 메소드
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    /**
     * MemberDetails 인터페이스 메소드
     */
    // 해당 Member의 권한을 리턴하는 곳!(role)
    // SecurityFilterChain에서 권한을 체크할 때 사용됨
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                if (member.getRole().equals("ROLE_ADMIN")) {
                    return String.valueOf(UserRole.ADMIN.getValue());
                } else if (member.getRole().equals("ROLE_MANAGER")) {
                    return String.valueOf(UserRole.MANAGER.getValue());
                } else {
                    return String.valueOf(UserRole.USER.getValue());
                }
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        //우리사이트!! 1년 동안 사용하지 않으면 휴면 계정으로 바꾼다.
        // 현재 시간 - 마지막 로그인 시간 => 1년을 초기하면 return false로 바꾼다.
        // 이러한 로직을 여기 넣는다.
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
