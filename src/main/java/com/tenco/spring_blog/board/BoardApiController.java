package com.tenco.spring_blog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 화면 (Html)이 아니라 데이터를 주고받는 컨트롤러.
// 주소 앞의 /api는 화면 주소와 데이터 주소를 구분하기 위해 사용.
@RestController // @Controller + @ResponseBody.
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardPersistRepository boardPersistRepository;

    // 주소 설계
    // DELETE   http://localhost:8080/api/boards/{id}
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            boardPersistRepository.deleteById(id);
            return ResponseEntity.ok("정상 삭제");
        }catch(Exception e) {
            return ResponseEntity.badRequest().body("잘못된 요청");
        }
    }

    }


