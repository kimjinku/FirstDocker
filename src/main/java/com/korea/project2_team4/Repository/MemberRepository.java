package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserName(String username);

    Optional<Member> findByRealNameAndPhoneNum(String realName, String phoneNum);
    Optional<Member> findByRealNameAndEmail(String realName, String email);
    Optional<Member> findByRealNameAndEmailAndUserName(String realName, String email, String userName);
    boolean existsByEmailAndRealName(String email,String realName);

    boolean existsByUserNameAndEmailAndRealName(String UserName,String email,String realName);

    @Query("SELECT m FROM Member m WHERE m.realName LIKE %:kw% OR m.userName LIKE %:kw% OR m.nickName LIKE %:kw%")
    Page<Member> findByKeywordInRealNameOrUserNameOrNickName(@Param("kw") String kw, Pageable pageable);

    boolean existsByUserName(String userName);
}
