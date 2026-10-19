package com.tenco.spring_blog.user;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data       @NoArgsConstructor  @AllArgsConstructor
@Table(name = "user_tb")    @Entity
public class User {
    @Id     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 같은 사용자명을 두번 가입할 수 없도록 unique 제약.
    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @CreationTimestamp  // 자동으로 now()
    private Timestamp createdAt;

    @Setter
    private String password;

    // 프로필 이미지 파일 이름(선택사항이므로 null 가능)
    private String profileImage;

    @Builder // id와 createdAt은 자동으로 채워지므로 빌더에서 제외.
    public User(String username , String password , String email , String profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public void update(String password) { // password 하나만 있으니 dto말고 그냥 String으로 처리.
        this.password = password;
    }

    // 프로필 이미지 변경.(null이면 삭제) 트랜잭션 안에서 호출하면 더티체킹 발생됨.
    public void updateProfileImage(String profileImage) {this.profileImage = profileImage;}

}
