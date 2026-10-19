package com.tenco.spring_blog.user;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    // 사용자의 역할 목록(User : Role = 1 : N , 단방향)
    // [cascade , orphanRemoval] : 두 옵션은 부모 쪽의 @OneToMany 컬렉션에 붙인다. User가 List<UserRole>을 가지고 있으므로 사용할 수 있다.
    @JoinColumn(name = "user_id" , nullable = false)    @OneToMany(cascade = CascadeType.ALL , orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();


    @Builder // id와 createdAt은 자동으로 채워지므로 빌더에서 제외.
    public User(String username , String password , String email , String profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    // password 하나만 있으니 dto말고 그냥 String으로 처리.
    public void update(String password)  {this.password = password;}

    // 프로필 이미지 변경.(null이면 삭제) 트랜잭션 안에서 호출하면 더티체킹 발생됨.
    public void updateProfileImage(String profileImage) {this.profileImage = profileImage;}


    // 역할 추가.(회원가입시 기본 User 부여 , 관리자는 직접 지정)
    public void addRole(Role role) {
        // 이미 가진 역할이면 추가하지 않음(코드에서 1차 방어)
        if(hasRole(role)) return;

        this.roles.add(UserRole.builder().role(role).build());
    }

    // 로그인한 사용자의 관리자 여부 확인 메서드.
    public boolean isAdmin()    {return hasRole(Role.ADMIN);}

    // 해당 역할을 가지고 있는지 확인.
    public boolean hasRole(Role role) {
        for(UserRole u : this.roles) {
            if(u.getRole() == role) return true;  // role이 이미 있다면 바로 true.
        }
        return false; // 전부 찾은후 role이 없다면 false;
    }
}
