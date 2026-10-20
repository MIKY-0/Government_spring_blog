package com.tenco.spring_blog.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 [복합 유니크 제약]
 한 사용자는 같은 역할을 한번만 가진다. (1 , ADMIN)  (1 , USER)  ----> 가능.  (1 , ADMIN)  (1 , ADMIN) --> 불가능.
 columnNames에는 자바 필드명이 아닌 DB컬럼명 써야함.
 현재 user_id는 이 클래스에 필드로 없지만 User엔티티측에서 @JoinColumn(name = user_id)를 설정하면
 자동으로 컬럼이 만들어짐. --> UserRole엔티티에 userId 안만들어줘도 됨.
 */
@AllArgsConstructor     @Data       @NoArgsConstructor
@Builder    @Entity
@Table(name = "user_role_tb" , uniqueConstraints = {@UniqueConstraint(name = "uk_user_role" , columnNames = {"user_id" , "role"})})
public class UserRole {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , length = 20)  @Enumerated(EnumType.STRING) // enum 을 정수가 아닌 문자열(ADMIN , USER)로 사용.
    private Role role;

    public UserRole(Role role) {
        this.role = role;
    }
}
