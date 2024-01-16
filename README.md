# 공지사항❗

## 프로젝트명 : 'PetPlanet' 반려동물커뮤니티사이트 
## 기간 : 대략 7주 (12/18 ~ 2/3 )


```
협업관리 담당 : 유영준 
// 프로젝트 merging은 위 팀원만 권한이 있음. 
// 나머지 조원들은 commit, push 까지만 해놓기.
```
## 일정 : 
### 1주차: db설계 및 소셜로그인 기능 구현, 기능명세 작성 진행 
 - 소셜로그인 제일 오래걸릴 것 같아서 먼저 시작
```
 12/18 layout, navbar적용 안됐던 이유 : 
build.gradle 에 implementation 'nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect' 추가했더니 해결됨

```
    
### 2주차: 기능명세작성 완료, 기능구현 진행 
### 3주차: 기능구현 진행 
### 4주차: 기능구현 진행  + 템플릿 다듬기
### 5주차: 기능구현 진행  + 템플릿 다듬기
```
⭐5주차 목표⭐

지금까지 만들었던 기능 버그,에러 다 완벽하게 수정하기.+ 템플릿도.
✍ 01/16
 - 검색결과에 태그검색도 추가하기
+ 추가기능 상의 : 리뷰, 입양분양 기능 등등
+ (보류)지도 api적용해보기 -> 리뷰공간 에 추가. /병원리뷰, 반려동물 카페 리뷰


```
### 6주차: 기능구현 진행  + 템플릿 다듬기 + 자잘한 버그 수정
### 7주차: 마무리 및 자잘한 버그 수정 + 발표자료,대본 만들기

## ERD 설계_2차수정
![image](https://github.com/second-project-team/project_team4/assets/143607484/39430ae4-52a0-45e0-b981-7826379beb39)




```
사이트의 회원 계정 및 프로필을 인스타그램 처럼 진행 예정
- 회원 : 회원가입시 입력된 개인정보 엔터티
- 프로필 : 사용자가 직접 관리하는 프로필 엔터티
- 반려동물 : 계정 하나당 여러개 연결가능한 반려동물 프로필 엔터티
- 관계 : 계정들간의 팔로우.. (이부분 추후 설명 수정하겠습니다)
- 사진파일 : 전체 프로그램에서 이미지파일들 담기위한객체. 자세한 동작은 imageservice의 메서드 들로 이루어집니다.
- 게시물 : 댓글과 좋아요를 List로 속성추가함.
- 댓글  
 



```
