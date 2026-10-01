//package com.tenco.spring_blog.board;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.sql.Timestamp;
//
//// 엔티티 클래스 만들기 : 데이터 베이스 한개를 자바 클래스로 그린 설계도.
//@Table(name = "board_tb")   @Entity     @Data
//public class Board {
//    @Id // 이 필드가 PK임을 명시.
//
//    // 기본키 값을 자동으로 생성(IDENTITY --> DB의 기본 설정을 따름.) Auto-increment 기능 사용.
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // 별도 어노테이션이 없으면 필드명이 컬럼명이 됨.
//    private String title;
//    private String content;
//    private String username;
//    private Timestamp createdAt; // created_at 컬럼(스프링이 createdAt을 기본값인 스네이크 케이스로 자동 변환해줌.)
//
//}
