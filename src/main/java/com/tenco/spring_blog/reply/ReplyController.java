package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller     @RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 댓글 등록.
    @PostMapping("/reply/save")
    public String save(ReplyRequest.SaveDto saveDto , HttpSession session) {
        // 2. 유효성검사.
        saveDto.validate();

        // 3. 서비스에 전달.(등록 요청)
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        replyService.save(sessionUserDto , saveDto);

        // 4. 뷰에 뿌림.
        return "redirect:/board/" + saveDto.getBoardId();
    }


    // 댓글 삭제.
    @PostMapping("/reply/{id}/delete")
    public String deleteById(@PathVariable Long id , HttpSession session){
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        Long boardId = replyService.deleteById(id , sessionUserDto);

        return "redirect:/board/" + boardId;
    }

}
