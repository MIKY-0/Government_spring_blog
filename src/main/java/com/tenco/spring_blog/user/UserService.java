package com.tenco.spring_blog.user;

/*
Service
핵심 개념 : 비즈니스 로직을 처리하는 계층. Controller와 Repository사이의 중간 계층. 트랜잭션 관리.
계층 구조 (3 Tier 아키텍처) 컨트롤러 -> 서비스 -> 레포지토리 -> DB -> 레포지토리 -> 서비스 -> 컨트롤러 -> Model / View
 */

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service    @RequiredArgsConstructor    @Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserJpaRepository userJpaRepository;

    // 회원가입.
    /**
     * 회원가입
     * @param joinDto : 회원가입 정보
     * @return : 저장된 사용자 정보
     */
    @Transactional
    public UserResponse.JoinDto join(UserRequest.JoinDto joinDto){
        log.info("회원 가입 서비스 시작 - 사용자명 : {}" , joinDto.getUsername());

        userJpaRepository.findByUsername(joinDto.getUsername()).ifPresent(user -> {
            log.warn("회원가입 실패 - 중복된 사용자명 : {}" , joinDto.getUsername());
            throw new Exception400("이미 존재하는 사용자명입니다");
        });

        // 이메일 중복 체크.
        userJpaRepository.findByEmail(joinDto.getEmail()).ifPresent(u -> {throw new Exception400("이미 존재하는 이메일입니다.");});

        User savedUser = userJpaRepository.save(joinDto.toEntity());

        log.info("회원 가입 서비스  완료 - 사용자명 : {} , Id : {}" , joinDto.getUsername() , joinDto.getEmail());

        return new UserResponse.JoinDto(savedUser);
    }

    // 로그인.
    /**
     * 로그인 처리
     * @param loginDto : 로그인 정보
     * @return : 로그인된 사용자 정보
     */
    public SessionUserDto login(UserRequest.LoginDto loginDto) {
        log.info("로그인 서비스 시작 - 사용자명 : {}" , loginDto.getUsername());
            User user = userJpaRepository.findByUsernameAndPassword(loginDto.getUsername() , loginDto.getPassword())
                    .orElseThrow( () -> {
                        log.warn("로그인 실패 - 사용자명 : {} , 원인 : 인증정보 불일치" , loginDto.getUsername());
                        return new Exception400("사용자명 또는 패스워드가 올바르지 않습니다.");
                    });

        log.info("로그인 성공 - 사용자명 : {}" , loginDto.getUsername());

        return new SessionUserDto(user);
    }


    /**
     * 회원 정보 수정 화면 : 화면에 필요한 값만 담는 DTO 반환.
     * @param userId
     * @return
     */
     public UserResponse.updateFormDto findUpdateForm(Long userId) {
        User user = findById(userId);
         return new UserResponse.updateFormDto(user);
     }


    // 회원정보 수정
    /**
     * 회원 정보 수정 처리 (더티 체킹 활용)
     * 세션 동기화용 DTO 반환.
      * @param id : 사용자 PK
     * @param updateDto : 수정할 정보
     * @return : 수정된 사용자 정보
     */
    @Transactional
    public SessionUserDto updateById(Long id , UserRequest.UpdateDto updateDto) {
        User user = findById(id); // get매핑에서 findById했는데 또 하는 이유 : post매핑에서도 영속상태로 가져오려고.
        user.update(updateDto.getPassword());

        return new SessionUserDto(user);
    }


    /**
     * 사용자 정보 조회.  서비스 안에서만 쓰는 조회용 메서드(캡슐화)
     * @param id : 사용자 PK
     * @return : 조회한 사용자 정보
     */
    private User findById(Long id) {
        log.info("사용자 정보 조회 - ID : {}" , id);
        return userJpaRepository.findById(id).orElseThrow(() -> new Exception404("일치하는 사용자가 없습니다"));
    }

}