package com.tenco.spring_blog.refund;

public enum RefundStatus {
    PENDING("대기중") ,
    APPROVED("승인됨") ,
    REJECTED("거절됨");

    private final String displayname;
    private RefundStatus(String displayName){
        this.displayname = displayName;
    }

    public String getDisplayname() {return this.displayname;}
}
