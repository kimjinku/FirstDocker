package com.korea.project2_team4.Service;


import com.korea.project2_team4.Config.PrincipalDetails;
import com.korea.project2_team4.Config.UserRole;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class MemberSecurityService implements UserDetailsService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //설정된 "BLOCK_MSG" 세션이 있다면 초기화
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true); // true == allow create
        session.removeAttribute("BLOCK_MSG");

        Optional <Member> member1 = this.memberRepository.findByUserName(username);
        if (member1.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        Member member = member1.get();
        //추가 부분
        if (member.isBlocked() && (member.getUnblockDate() == null || LocalDateTime.now().isBefore(member.getUnblockDate()))) {
            String msg = member.getUnblockDate() + " 까지 차단된 아이디입니다";
            // HttpSession을 가져옴
//            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//            HttpSession session = attr.getRequest().getSession(true); // true == allow create
            session.setAttribute("BLOCK_MSG", msg);

            throw new LockedException("User is blocked until " + member.getUnblockDate());
        }
        if (member.isBlocked() && (member.getUnblockDate() != null && LocalDateTime.now().isAfter(member.getUnblockDate()))) {
            // 차단기간이 지나서 로그인한 경우, db에서 차단여부를 확인하는 변수를 false로 바꾼다.
            member.setBlocked(false);
            member.setUnblockDate(null);
            memberService.save(member);
        }
        //추가 부분 끝
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        }else if("ROLE_MANAGER".equals(member.getRole())){
            authorities.add(new SimpleGrantedAuthority(UserRole.MANAGER.getValue()));
        }
        else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
//        return new User(member.getUserName(), member.getPassword(), authorities);
        return new PrincipalDetails(member);
    }
}