package com.ax.avatarcoach.domain.session.entity;

public enum SessionStatus {
    READY, // 세션 생성됨, 연습 시작 전
    IN_PROGRESS, // 연습 진행 중
    COMPLETED, // 정상 종료
    FAILED // 중간 실패
}
