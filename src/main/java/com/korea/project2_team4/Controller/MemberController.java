package com.korea.project2_team4.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.project2_team4.Config.OAuth2.OAuth2UserInfo;
import com.korea.project2_team4.Config.UserRole;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Model.Form.EditPasswordForm;
import com.korea.project2_team4.Model.Form.MemberCreateForm;
import com.korea.project2_team4.Model.Form.MemberResetForm;
import com.korea.project2_team4.Repository.MemberRepository;
import com.korea.project2_team4.Service.MemberService;
import com.korea.project2_team4.Service.ProfileService;
import com.korea.project2_team4.Service.ReportService;
import com.korea.project2_team4.Service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8888")
@Controller
@RequestMapping("/member")
@Builder
public class MemberController {

    private final MemberService memberService;
    private final TagService tagService;
    private final ReportService reportService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private HttpSession session;
    private final MemberRepository memberRepository; //카카오로그인 테스트용. 추후 삭제 예정. 황선영추가
    private final ProfileService profileService; //카카오로그인 테스트용. 추후 삭제 예정. 황선영추가
    @GetMapping("/signup")
    public String signup(MemberCreateForm memberCreateForm) {

        return "Member/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult,
                         String authenticationCode,
                         SessionStatus sessionStatus,
                         HttpSession session,
                         Model model) {

//        // 세션에서 authenticationCode 속성 가져오기
//        String expectedAuthenticationCode = (String) session.getAttribute("expectedAuthenticationCode");
//
//        // 세션이 없는 경우(이메일 인증을 거치지 않은 경우)
//        if (expectedAuthenticationCode == null) {
//            model.addAttribute("codeNotFound","인증번호를 받은 기록이 없습니다. 인증번호를 받아주세요");
//            return "Member/signup_form";
//        }
//        //문자로 발송 되어 기존 세션에 저장된 인증코드와 현재 입력된 인증코드가 일치하는지 확인
//        if (expectedAuthenticationCode.equals(authenticationCode)) {
//            // 검증에 성공하면 세션에서 인증 코드 제거
//            sessionStatus.setComplete();

            if (bindingResult.hasErrors()) {
                return "Member/signup_form";
            }


            if (!memberCreateForm.getPassword().equals(memberCreateForm.getRe_password())) {
                bindingResult.rejectValue("password2", "passwordInCorrect",
                        "2개의 패스워드가 일치하지 않습니다.");
                return "Member/signup_form";
            }

            try {
                memberService.create(memberCreateForm);
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
                return "Member/signup_form";
            } catch (Exception e) {
                e.printStackTrace();
                bindingResult.reject("signupFailed", e.getMessage());
                return "Member/signup_form";
            }
            return "redirect:/";
        }
//        model.addAttribute("mismatchCode","인증번호가 일치하지 않습니다.");
//        return "Member/signup_form";
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/resetPassword")
    public String resetPassword(Model model, MemberResetForm memberResetForm) {
        model.addAttribute("memberResetForm", memberResetForm);
        return "Member/reset_password_from";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid MemberResetForm memberResetForm, BindingResult bindingResult, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        if (bindingResult.hasErrors()) {
            return "Member/reset_password_from";
        }
        if (!passwordEncoder.matches(memberResetForm.getPassword(), member.getPassword())) {
            bindingResult.rejectValue("password", "passwordInCorrect",
                    "기존의 패스워드와 일치하지 않습니다.");
            return "Member/reset_password_from";
        }
        if (!memberResetForm.getNew_password().equals(memberResetForm.getRe_password())) {
            bindingResult.rejectValue("re_password", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "Member/reset_password_from";
        }

        memberService.resetPassword(memberResetForm, principal);

        return "redirect:/profile/myPage";
    }


    @PostMapping("/editPassword")
    public String editPassword(@Valid EditPasswordForm editPasswordForm, BindingResult bindingResult, String foundMemberName) {
        Member member = memberService.getMember(foundMemberName);
        if (bindingResult.hasErrors()) {
            return "Member/editPassword_from";
        }
        if (!editPasswordForm.getNew_password().equals(editPasswordForm.getRe_password())) {
            bindingResult.rejectValue("re_password", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "Member/editPassword_from";
        }

        memberService.editPassword(editPasswordForm, member);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "Member/login_form";
    }

    @PostMapping("/login")
    public String login(String username, String password) {



        return "redirect:/";
    }

    @GetMapping("/managePage")
    public String managePage(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Post> reportedPosts = reportService.findPostsLinkedWithReports(page);
        List<Tag> defaultTagList = tagService.getDefaultTags();
        model.addAttribute("defaultTagList", defaultTagList);
        model.addAttribute("paging", reportedPosts);
        return "Member/findReportedPosts_form";
    }

    @GetMapping("/adminPage")
    public String adminPage(Principal principal, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = memberService.getMember(principal.getName());
        List<Member> memberList = memberService.getAllMembers();
        Page<Member> paging = this.memberService.getMemberListByPage(page);

        model.addAttribute("member", member);
        model.addAttribute("memberList", memberList);
        model.addAttribute("paging", paging);
        return "Member/adminPage_form";
    }

    @GetMapping("/search")
    public String searchMember(Model model,
                               Principal principal,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "kw", defaultValue = "") String kw) {
        Member member = memberService.getMember(principal.getName());
        List<Member> memberList = memberService.getAllMembers();
        Page<Member> paging = this.memberService.searchMember(page, kw);
        model.addAttribute("memberList", memberList);
        model.addAttribute("paging", paging);
        model.addAttribute("member", member);
        return "Member/adminPage_form";
    }

    @PostMapping("/adminPage/changeMemberRole/{id}")
    public String changeMemberRole(@PathVariable Long id, @RequestParam String[] role) {
        for (String userRole : role) {
            memberService.changeMemberRole(id, userRole);
        }
        return "redirect:/member/adminPage";
    }

    @GetMapping("/signup/social")
    public String signup(MemberCreateForm memberCreateForm,
                         HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        OAuth2UserInfo socialLogin = (OAuth2UserInfo) session.getAttribute("SOCIAL_LOGIN");

        if (socialLogin != null) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                memberCreateForm.setNickName(socialLogin.getName());
            memberCreateForm.setUserName(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setPassword(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRe_password(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRealName(socialLogin.getName());
            memberCreateForm.setEmail(socialLogin.getEmail());
            memberCreateForm.setProvider(socialLogin.getProvider());
            memberCreateForm.setProviderID(socialLogin.getProviderId());
//            memberCreateForm.setSnsImage(socialLogin.getImage());
        }
        model.addAttribute("socialLogin", socialLogin);

        return "Member/social_signup_form";
    }

    @PostMapping("/signup/social")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult,
//                         @RequestParam(value = "profile-picture", defaultValue = "") String profile,
                         HttpServletRequest request, Model model,
                         String authenticationCode,
                         SessionStatus sessionStatus) {

//        HttpSession session = request.getSession();
        OAuth2UserInfo socialLogin = (OAuth2UserInfo) session.getAttribute("SOCIAL_LOGIN");

//        // 세션에서 authenticationCode 속성 가져오기
//        String expectedAuthenticationCode = (String) session.getAttribute("expectedAuthenticationCode");
//
//        // 세션이 없는 경우(이메일 인증을 거치지 않은 경우)
//        if (expectedAuthenticationCode == null) {
//            return "redirect:/member/signup/social";
//        }
//
//        //문자로 발송 되어 기존 세션에 저장된 인증코드와 현재 입력된 인증코드가 일치하는지 확인
//        if (expectedAuthenticationCode.equals(authenticationCode)) {
//            // 검증에 성공하면 세션에서 인증 코드 제거
//            sessionStatus.setComplete();

            if (socialLogin != null) {
                memberCreateForm.setNickName(socialLogin.getName());
                memberCreateForm.setUserName(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
                memberCreateForm.setPassword(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
                memberCreateForm.setRe_password(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
                memberCreateForm.setRealName(socialLogin.getName());
                memberCreateForm.setEmail(socialLogin.getEmail());
                memberCreateForm.setProvider(socialLogin.getProvider());
                memberCreateForm.setProviderID(socialLogin.getProviderId());
//            memberCreateForm.setSnsImage(socialLogin.getImage());
            }
            model.addAttribute("socialLogin", socialLogin);


            if (bindingResult.hasErrors()) {
                return "Member/social_signup_form";
            }

            if (!memberCreateForm.getPassword().equals(memberCreateForm.getRe_password())) {
                bindingResult.rejectValue("password2", "passwordInCorrect",
                        "2개의 패스워드가 일치하지 않습니다.");
                return "Member/social_signup_form";
            }

            try {

//            소셜 로그인 프로필 이미지
//
//            if (profile.equals("sns-picture")) {
//                memberCreateForm.setImageType(0);
//            } else {
//                memberCreateForm.setImageType(1);
//            }

                Member member = memberService.create(memberCreateForm);
//            if(member.isBlocked()){
//                model.addAttribute("blockMessage",member.getUnblockDate()+"까지 차단된 아이디입니다");
//                return "redirect:/signup/social";
//            }
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
                return "/Member/social_signup_form";
            } catch (Exception e) {
                e.printStackTrace();
                bindingResult.reject("signupFailed", e.getMessage());
                return "Member/social_signup_form";
            }
            return "redirect:/";
        }

//        return "redirect:/member/signup/social";
//    }

    @GetMapping("/member")
    public String saveDefaultAdmin() throws Exception {
        memberService.saveDefaultAdmin();

        return "redirect:/";

    }

    @GetMapping("/member1")
    public String saveDefaultUser() throws Exception {
        memberService.saveDefaultUser();

        return "redirect:/";

    }

    @GetMapping("/findUserName")
    public String findUserName(Model model) {
        return "Member/findUserName_form";
    }

    @PostMapping("/delete")
    public String memberDelete(@RequestParam List<String> userNames) {
        for (String userName : userNames) {
            Member member = this.memberService.getMember(userName);
            memberService.delete(member);
        }
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String realName, @RequestParam String email,
                                                       HttpSession session) {
        try {
            if (memberService.checkMemberToFindUserName(email, realName)) {
                String verificationCode = memberService.generateRandomCode();
                memberService.SendVerificationCode(email, verificationCode);

                // 클라이언트에게 전송된 인증 코드를 세션에 저장
                session.setAttribute("expectedVerificationCode", verificationCode);

                return ResponseEntity.ok("Verification code sent successfully!");
            }
            return ResponseEntity.badRequest().body("User not found for the provided email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending verification code: " + e.getMessage());
        }
    }

    @PostMapping("/findUserName")
    public String findUserName(@RequestParam String verificationCode,
//                               @SessionAttribute("expectedVerificationCode") String expectedVerificationCode,
                               SessionStatus sessionStatus,
                               HttpSession session,
                               String realName, String email, Model model) {
        // 세션에서 expectedVerificationCode 속성 가져오기
        String expectedVerificationCode = (String) session.getAttribute("expectedVerificationCode");

        // 세션이 없는 경우(이메일 인증을 거치지 않은 경우)
        if (expectedVerificationCode == null) {
            return "redirect:/member/findUserName";
        }

        // 클라이언트에게 전송된 인증 코드와 서버에서 예상하는 인증 코드가 일치하는지 확인
        if (verificationCode.equals(expectedVerificationCode)) {
            // 검증에 성공하면 세션에서 인증 코드 제거
            sessionStatus.setComplete();
            Member member = memberService.foundUser(realName, email);
            model.addAttribute("foundMember", member);
            return "Member/foundUserName_form";
        }
        return "redirect:/member/findUserName";
    }

    @GetMapping("/findPassword")
    public String findPassword(Model model) {
        return "Member/findPassword_form";
    }

    @PostMapping("/sendVerificationCodeToFindPassword")
    public ResponseEntity<String> sendVerificationCodeToFindPassword(@RequestParam String realName,
                                                                     @RequestParam String email,
                                                                     @RequestParam String userName,
                                                                     HttpSession session) {
        try {
            if (memberService.checkMemberToFindPassword(userName, email, realName)) {
                String verificationCode = memberService.generateRandomCode();
                memberService.SendVerificationCode(email, verificationCode);

                // 클라이언트에게 전송된 인증 코드를 세션에 저장
                session.setAttribute("expectedVerificationCode", verificationCode);

                return ResponseEntity.ok("Verification code sent successfully!");
            }
            return ResponseEntity.badRequest().body("User not found for the provided email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending verification code: " + e.getMessage());
        }
    }

    @PostMapping("/findPassword")
    public String findPassword(@RequestParam String verificationCode,
//                               @SessionAttribute("expectedVerificationCode") String expectedVerificationCode,
                               SessionStatus sessionStatus,
                               HttpSession session,
                               String realName,
                               String email,
                               String userName,
                               Model model,
                               EditPasswordForm editPasswordForm) {
        // 세션에서 expectedVerificationCode 속성 가져오기
        String expectedVerificationCode = (String) session.getAttribute("expectedVerificationCode");

        // 세션이 없는 경우(이메일 인증을 거치지 않은 경우)
        if (expectedVerificationCode == null) {
            return "redirect:/member/findUserName";
        }

        // 클라이언트에게 전송된 인증 코드와 서버에서 예상하는 인증 코드가 일치하는지 확인
        if (verificationCode.equals(expectedVerificationCode)) {
            // 검증에 성공하면 세션에서 인증 코드 제거
            sessionStatus.setComplete();
            Member member = memberService.foundUserByUserName(realName, email, userName);
            model.addAttribute("foundMember", member);
            model.addAttribute("editPasswordFrom", editPasswordForm);
            return "Member/editPassword_form";
        }
        return "redirect:/member/findPassword";
    }

    @PostMapping("/block/{username}")
    public String blockMember(@PathVariable String username,
                              @RequestParam Long reportDurationDays) {
        Duration blockDuration = Duration.ofDays(reportDurationDays);
        memberService.blockMember(username, blockDuration);
        return "redirect:/member/adminPage";
    }

    @PostMapping("/unblock/{username}")
    public String unblockMember(@PathVariable String username) {
        memberService.unblockMember(username);
        return "redirect:/member/adminPage";
    }

    @PostMapping("/phoneCheck")
    public String memberPhoneCheck(@RequestParam(value = "phoneNum") String to, HttpSession session) {

        memberService.phoneNumberCheck(to, session);
        return "redirect:/member/signup";
    }
    @GetMapping("/checkDuplicateUsername/{username}")
    @ResponseBody
    public boolean checkDuplicateUsername(@PathVariable String username) {
        return !memberService.existsByUserName(username);
    }
    @GetMapping("/checkDuplicateEmail/{email}")
    @ResponseBody
    public boolean checkDuplicateEmail(@PathVariable String email) {
        return !memberService.existsByEmail(email);
    }
    @GetMapping("/checkDuplicateNickName/{nickName}")
    @ResponseBody
    public boolean checkDuplicateNickName(@PathVariable String nickName) {
        return !memberService.existsByNickName(nickName);
    }
    @GetMapping("/checkDuplicate/{field}/{value}")
    @ResponseBody
    public boolean checkDuplicate(@PathVariable String field, @PathVariable String value) {
        switch (field) {
            case "username":
                return !memberService.existsByUserName(value);
            case "email":
                return !memberService.existsByEmail(value);
            case "nickName":
                return !memberService.existsByNickName(value);
            default:
                return false;
        }
    }

}
