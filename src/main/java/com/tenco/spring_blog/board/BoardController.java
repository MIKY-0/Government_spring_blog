//package com.tenco.spring_blog.board;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//import java.util.Map;
//
//@Controller     @Slf4j      @RequiredArgsConstructor
//public class BoardController {
//    private final BoardNativeRepository boardNativeRepository;
//
//    // GET http://localhost:8080/ , http://localhost:8080/board/list  둘 다 담당.
//    @GetMapping({"/" , "/board/list"})
//    public String list(Model model) {
//
//        // 임시 데이터(D)
////        model.addAttribute("boardList" , List.of(
////                Map.of("id" , 1 , "title" , "첫번째 글") ,
////                Map.of("id" , 2 , "title" , "두번째 글") ,
////                Map.of("id" , 3 , "title" , "세번째 글")
////        ));
//
//        List<Board> boardList = boardNativeRepository.findAll();
//        model.addAttribute("boardList" , boardList);
//
//        return "board/list";
//    }
//
//
//    // GET http://localhost:8080/board/3
//    @GetMapping({"/board/{id}"})
//    public String detail(@PathVariable(name="id") Long id , // name을 설정하면 name에서는 {id}와 맞추고 Long은 내가 원하는 변수명 사용 가능.
//                         Model model) {
//
//        // 샘플데이터(D)
////        model.addAttribute(("board"), sampleBoard(id));
//
//        Board board = boardNativeRepository.findById(id);
//
//        if(board == null) {
//            return "redirect:/"; // 존재하지 않는 id를 요청시 그냥 메인페이지로 빠지도록 설계.
//        }
//
//        model.addAttribute("board", board);
//
//        return "board/detail";
//    }
//
//
//    // GET http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
//    @GetMapping({"/board/save"})
//    public String saveForm() {
//
//        return "board/save-form";
//    }
//
//    // [[코드 추가]]
//    // POST http://localhost:8080/board/save (화면요청.  화면만 뿌림.)
//    // 스프링부트의 데이터 기본 파싱 전략 key=value.
//    // 현재 save-form.mustache에는 name속성이 있다. --> 이 name속성이 있어야 스프링부트가 데이터를 뽑아낼 수 있음.
//    // form태그에 있는 값을 자바로 가져오는 것.
//    // 이 name속성은 키값이므로 ? 뒤에오는 쿼리. --> RequestParam.
//    @PostMapping({"/board/save"})
//    public String save(@RequestParam("username") String username ,
//                       @RequestParam("title") String title ,
//                       @RequestParam("content") String content) {
//        // form의 name속성과 매개변수명이 일치하면 자동으로 값이 바인딩됨.
//        // name = "title" && @RequestParam("title") --> String title로 자동 매핑
//
//        // Slf4j의 로그 출력 방식.
//        log.info("username : {}" , username);
//        log.info("title : {}" , title);
//        log.info("content : {}" , content);
//
//        // DAO 객체에게 데이터 전달 후 저장하는 일 위임. -- BoardNativeRepository
//        boardNativeRepository.save(title , content , username);
//
//
//        return "redirect:/"; // 저장후 메인 페이지로 이동. POST요청 후 메인페이지로 이동시킬때. PRG(Post-Redirect-Get)패턴.
//        // Post로 요청받고 Redirect로 메인페이지로 Get요청하여 메인페이지로 이동.
////        return "board/save-form";
//    }
//
//    // =================================================================================================
//
//    // GET http://localhost:8080/board/1/update (수정 화면요청. 지금은 form태그로 던질거라서 restful 맞추지않고 사용.)
//    @GetMapping({"/board/{id}/update"})
//    public String updateForm(@PathVariable(name = "id") Long id , Model model) {
//        // 샘플 데이터(D)
////        model.addAttribute("board" , sampleBoard(id));
//
//        // 수정하기 화면 요청(먼저 조회부터)
//        Board board = boardNativeRepository.findById(id);
//        model.addAttribute("board", board);
//        return "board/update-form";
//    }
//
//
//    // Post http://localhost:8080/board/1/update (게시글 실제 수정 기능 요청.)
//    @PostMapping({"/board/{id}/update"})
//    public String update(@PathVariable(name = "id") Long id ,
//                         @RequestParam(name = "title") String title ,
//                         @RequestParam(name = "content") String content) {
//        boardNativeRepository.updateById(title , content , id);
//
//        // 수정완료했으면 PRG 패턴으로 수정한 게시글페이지로 가서 다시 보여줌.  /board/{id}
//
//        return "redirect:/board/" + id; // 리다이렉트 수정성공한 해당 게시글 화면 이동.
//    }
//
//    // ==================================================================================================
//
//
//    // 게시글 삭제.
//    // /board/{{board.id}}/delete
//    @PostMapping("/board/{id}/delete")
//    public String delete(@PathVariable(name = "id") Long id) {
//        boardNativeRepository.deleteById(id);
//
//        // PRG 패턴 사용. -- 삭제완료시 메인페이지로.
//        return "redirect:/";
//    }
//
//
//    // TODO
//    // 뼈대용 임시 게시글 한개(데이터베이스 연결시 삭제 예정)(D)
////    private Map<String , Object> sampleBoard(Long id) {
////        return Map.of("id", id , "title" , id + "번째 글" ,  "content" , "임시내용" ,
////                "username" , "김민수");
////    }
//}
