package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserName(String username);

    Optional<Member> findByRealNameAndPhoneNum(String realName, String phoneNum);
    Optional<Member> findByRealNameAndEmail(String realName, String email);
    Optional<Member> findByRealNameAndEmailAndUserName(String realName, String email, String userName);
    boolean existsByEmailAndRealName(String email,String realName);
    boolean existsByUserNameAndEmailAndRealName(String UserName,String email,String realName);;
}
