package com.tenco.spring_blog.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String password;

    @Builder // id와 createdAt은 자동으로 채워지므로 빌더에서 제외.
    public User(String username , String password , String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

}
