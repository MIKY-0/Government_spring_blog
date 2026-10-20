package com.tenco.spring_blog.stos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data       @AllArgsConstructor     @NoArgsConstructor
// 외부 서버에서는 우리 서버에 불필요한 내용까지 많은 데이터를 전달. 불필요한 필드 즉, 우리코드에서는 선언이 안된 외부서버의 필드는 무시하는 뜻.
// 없어도 괜찮지만 안전상 선언 권장
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoDto {
    private Integer id;
    private Integer userId;
    private String title;
    private boolean completed;
}
