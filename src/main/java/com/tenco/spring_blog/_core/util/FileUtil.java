package com.tenco.spring_blog._core.util;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception500;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

//@Component  // 미리 스프링 빈에 등록해도되지만 여기선 안함.
/*
 내부에 이 클래스는 상태값을 가지지 않게 할거임.
 FileUtil은 상태도 없고 다른 빈에 의존하지 않는 순수한 도구 함수 모음이라 static메서드로 활용할 예정.

 MultipartFile : 업로드된 파일 그 자체(이미지 뿐 아니라 모든 파일)
 */
@Slf4j
public class FileUtil {
    /*
    1. 업로드 폴더 : 사용자 홈 폴더 아래 blog_uploads(운영체제와 관계없이 동작시킬거임)
    "user.home" : 자바에서 기본으로 제공하는 프로퍼티 값. 홈 디렉터리에 "\blog_uploads"경로를 붙여서 문자열로 가져옴.
    실제 이 경로로 폴더를 생성하지는 않는다. 실제 생성은 createDirectories.
    */
    public static final String UPLOAD_DIR = Paths.get(System.getProperty("user.home") , "blog_uploads").toString();

    // 2 . 허용할 이미지 형식과 저장할 확장자 설정.
    private static final Map<String , String >  IMAGE_TYPES = Map.of(
            "image/jpeg" , ".jpg" ,
            "image/png" , ".png" ,
            "image/gif" , ".gif" ,
            "image/webp" , ".webp"
    );


    // 3. 이미지 저장 : 저장할 파일 이름을 반환 (파일이 없으면 null)
    public static String saveImage(MultipartFile file) {
        // 1. 파일 저장은 선택사항으로 결정할 예정(프로필)
        if(file == null || file.isEmpty()) return null;

        // 2. 허용한 이미지 형식인지 확인.
        if(!isImageFile(file)) throw new Exception400("이미지 파일은 (JPG , PNG , GIF , WEBP)만 업로드 가능.");

        // 3. 겹치지 않는 새 파일 이름 만들기(UUID + 확장자)
        String extension = IMAGE_TYPES.get(file.getContentType());
        String saveFileName = UUID.randomUUID() + extension;

        // 4. 폴더가 없으면 만들고 , 파일 저장.
        Path uploadPath = Paths.get(UPLOAD_DIR);
        try{
            Files.createDirectories(uploadPath); // 이미 있으면 아무일도 하지않음.
            file.transferTo(uploadPath.resolve(saveFileName));  // 폴더경로 + 파일이름(파일 생성됨)
        }catch (IOException e) {
            log.error("파일 저장 실패 : {}" , e.getMessage());
            throw new Exception500("파일 저장 중 오류가 발생했습니다.");
        }
        return saveFileName;
    }


    // 파일 삭제(파일 이름이 없거나 파일이 이미 없으면 아무일도 하지 않음.)
    public static void deleteFile(String filename){
        if(filename == null || filename.isBlank())  return;

        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR , filename));
        } catch (IOException e) {
            // 파일 삭제에 실패해도 사용자 요청은 계속 진행(로그만 남김)
            log.warn("파일 삭제 실패 : {}" , e.getMessage());
        }
    }



    // 편의 메서드 : 허용한 이미지 형식인지 확인.
    public static boolean isImageFile(MultipartFile file) {
        // getContentType : MIME타입을 가져옴.("cat.png 파일" -->  (확장자 - .png) , (MIME 타입 - image/page))
        if(file == null || file.getContentType() == null) return false;  // 형식 정보가 없으면 허용하지 않음.

        return IMAGE_TYPES.containsKey(file.getContentType()); // IMAGE_TYPES에서 작성해놓은 파일형식이라면 true 반환.
    }
}
