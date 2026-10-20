package com.tenco.spring_blog.stos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 서버 측에 던질 데이터
@Data       @AllArgsConstructor     @NoArgsConstructor
@Builder    @JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto {
    private Integer userId;
    private String body;
    private String title;
}
