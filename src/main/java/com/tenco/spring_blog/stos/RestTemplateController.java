package com.tenco.spring_blog.stos;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class RestTemplateController {
    /// GET
    // 테스트 주소 : http://localhost:8080/practice/todos/1
    @GetMapping("/practice/todos/{id}")
    public ResponseEntity<?> getTodoAsSting(@PathVariable Integer id) {
        // 1. 요청할 외부 API 주소.
        String url = "https://jsonplaceholder.typicode.com/todos/" + id;

        // 2. HTTP 요청을 보내는 도구 생성.
        RestTemplate restTemplate = new RestTemplate();

        // 3. 요청 헤더 - 응답을 JSON으로 주세요.
        // 추후 외부 API사용시 헤더에 어떤형식으로 던질지 양식이 문서에 나와있음. 이거 보고 맞춰서 만들자.
        // setAccept : 서버의 응답은 'JSON'형식으로 주라고 요청.
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 4. 보내는 요청 꾸러미(HttpEntity) = 헤더 + 바디.(HTTP요청 메세지 헤더와 바디를 만들어줌.)
        HttpEntity request = new HttpEntity(headers); // 이 시점 : 헤더만 있는 상태.
        // GET - 자원의 요청(바디가 없음)

        // 5. 요청 보내기 - exchange(주소 , 메서드 , HTTP 메시지 , 응답바디 타입)
        // ResponseEntity = 응답.  상태코드 + 헤더 + 바디
        // url주소로 GET요청을 HTTP메시지(헤더 , 바디)에 담아서 서버의 응답바디(Json형식)를 String타입으로 바꿔서 받겠다.( TodoDto 설계전)
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        ResponseEntity<TodoDto> response = restTemplate.exchange(url, HttpMethod.GET, request, TodoDto.class);

        // 6. 응답 확인
        System.out.println("상태 코드 확인 : " + response.getStatusCode());
        System.out.println("응답 헤더 확인 : " + response.getHeaders());
        System.out.println("바디 내용 확인 : " + response.getBody());

        // 7. 응답 TodoDto 형태 구축
        System.out.println("userId : " + response.getBody().getUserId());
        System.out.println("id : " + response.getBody().getId());
        System.out.println("title : " + response.getBody().getTitle());
        System.out.println("completed : " + response.getBody().isCompleted());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response.getBody());
    }


    ///  POST
    // 브라우저 --> 우리서버 요청 주소 설정.
    // 브라우저(GET) --> 우리서버 요청(POST)  --> 상대 서버
    // 테스트 주소 : http://localhost:8080/practice/posts
    @GetMapping("/practice/posts")
    public ResponseEntity<?> createPost() {
        // 1. post 요청할 url주소.
        String url = "https://jsonplaceholder.typicode.com/posts";

        // 2. HTTP 요청 보낼 상자 생성.
        RestTemplate restTemplate = new RestTemplate();

        // 3. 헤더 생성.
        HttpHeaders headers = new HttpHeaders();

        //headers의 content-type을 지정해달라는 양식이 있으니 지정해주자.  setContentType : 보내는 바디는 JSON형식이다.
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 4. 바디 내용 생성.
        PostDto postDto = PostDto.builder()
                .userId(100)
                .title("오늘은 뭐 배우나")
                .body("가나다라마바사")
                .build();

        try {
            // 바디 + 헤더.  헤더를 먼저 넣으면 헤더만 보내는 요청으로 인식할 수 있음. 바디를 먼저 넣어줘야함
            HttpEntity<PostDto> request = new HttpEntity(postDto, headers);

            // 6. 요청 보내기
            // PostDto - 하나로 요청 , 응답() --> 관리가 힘듦.  요청DTO , 응답 DTO 설계가 관례.
            // 여기선 HttpEntity<PostDto> , ResponseEntity<PostDto> 요청 , 응답을 같은 DTO로 사용중. --> 응답 , 요청 양식 구조가 거의 동일하므로
            // 같이 사용. 원래는 분리해서 사용.
            ResponseEntity<PostDto> response = restTemplate.exchange(url, HttpMethod.POST, request, PostDto.class);

            System.out.println(response.getStatusCode());
            System.out.println(response.getHeaders());
            System.out.println(response.getBody());

            // return 찍어야 response탭에 바디가 담기고 화면에 담김.
            return ResponseEntity.ok().body(response.getBody());

        } catch (HttpClientErrorException e) { // 외부 서버가 보낸 상태코드와 오류 내용 확인 가능.
            return ResponseEntity.status(e.getStatusCode()).body("외부 API 요청에 실패");
        }
    }

}
