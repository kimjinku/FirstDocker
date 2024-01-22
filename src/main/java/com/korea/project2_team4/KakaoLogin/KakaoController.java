package com.korea.project2_team4.KakaoLogin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@Builder
@RequestMapping("/login/oauth2")
public class KakaoController {

    @GetMapping("/authorization/kakao")  // 수정된 부분
    public String redirectToKakaoAuthorization() {
        // Redirect to Kakao OAuth2 authorization endpoint
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/oauth/kakao/callback")
    public @ResponseBody String kakaocallback(String code) {
        System.out.println("코드 : ");
        System.out.println(code);


        RestTemplate rt = new RestTemplate();
        //HttpHeader오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","6c129972ef373b5f9b8f5dd02ecfc5c6");
        params.add("redirect_uri","http://localhost:8888/member/oauth/kakao/callback");
        params.add("code",code);

        //header와body를 하나의 오브젝트에 담기 --> 밑에 exchange라는 메서드가, httpEntity라는 오브젝트를 인수로 받기때문
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params,headers);

        //http요청하기 post방식, response변수의 응답 반음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token", //post요청보낼 주소
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
//        return "카카오 토큰 요청에 대한 응답 : " + response;
        //받은 응답 오브젝트로 담기. Json데이터를 자바 오브젝트에서 처리하기 위해서.
        // 라이브러리 사용 -> Gson,Json Simple, ObjectMapper등등, 마지막거씀.
        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = new OauthToken();

        try { //parsing할때 오류 막기위해 try catch로 감싸야함.
            oauthToken = objectMapper.readValue(response.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//
        System.out.println("카카오 액세스 토큰 : "+ oauthToken.getAccess_token());

        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me", //post요청보낼 주소
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );
        System.out.println(response2.getBody());


        //받은 응답 오브젝트로 담기. Json데이터를 자바 오브젝트에서 처리하기 위해서.
        // 라이브러리 사용 -> Gson,Json Simple, ObjectMapper등등, 마지막거씀.
        ObjectMapper objectMapper2 = new ObjectMapper();
        KaKaoProfile kaKaoProfile = new KaKaoProfile();

        try { //parsing할때 오류 막기위해 try catch로 감싸야함.
            kaKaoProfile = objectMapper.readValue(response2.getBody(), KaKaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 아이디 : " + kaKaoProfile.getId());
//        System.out.println("카카오 닉네임 : " + kakaoProfile.getProperties().getNickname());

//        return "카카오 토큰 요청에 대한 응답 : " + response; -> 헤더랑 바디 다 뜸. 헤더는 별로 쓸모없음. 바디만 받기
//        return "카카오 토큰 요청에 대한 응답 : " + response.getBody();
        return response2.getBody();
    }
}
