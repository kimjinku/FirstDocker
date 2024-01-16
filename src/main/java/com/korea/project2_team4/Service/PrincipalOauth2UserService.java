package com.korea.project2_team4.Service;

import com.korea.project2_team4.Config.OAuth2.GoogleUserInfo;
import com.korea.project2_team4.Config.OAuth2.NaverUserInfo;
import com.korea.project2_team4.Config.OAuth2.OAuth2UserInfo;
import com.korea.project2_team4.Config.PrincipalDetails;
import com.korea.project2_team4.Model.Entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@Builder
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private PasswordEncoder passwordEncoder;
    private MemberService memberService;


    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {




        //"registraionId" 로 어떤 OAuth 로 로그인 했는지 확인 가능(google,naver등)
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
        System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());
        //구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Clien라이브러리가 받아줌) -> code를 통해서 AcssToken요청(access토큰 받음)
        // => "userRequest"가 감고 있는 정보
        //회원 프로필을 받아야하는데 여기서 사용되는것이 "loadMember" 함수이다 -> 구글 로 부터 회원 프로필을 받을수 있다.


        /**
         *  OAuth 로그인 회원 가입
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }
//            else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
//                oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes().get("kakao_account"),
//                        String.valueOf(oAuth2User.getAttributes().get("id")));
//            }
        else {
            System.out.println("지원하지 않은 로그인 서비스 입니다.");
        }

        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;

        Member member = memberService.getMember(username);
        if(member != null){
            if (member.isBlocked() && (member.getUnblockDate() == null || LocalDateTime.now().isBefore(member.getUnblockDate()))) {
                String msg = member.getUnblockDate() + " 까지 차단된 아이디입니다";
                // HttpSession을 가져옴
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true); // true == allow create
                session.setAttribute("BLOCK_MSG", msg);
                throw new LockedException("User is blocked until " + member.getUnblockDate());
            }
        }
        //처음 서비스를 이용한 회원일 경우
        if (member == null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession session = request.getSession();

            session.setAttribute("SOCIAL_LOGIN", oAuth2UserInfo);
            throw new UsernameNotFoundException("OAuth2 : OAuth2User is Not Found in DB Member Data");
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }

}

