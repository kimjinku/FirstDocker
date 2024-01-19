package com.korea.project2_team4.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    public enum MessageType {
        ENTER, TALK, LEAVE;
    }

    private ChatDTO.MessageType type; //메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람
    private String message; // 메시지
    private String image; // 이미지
    private String time; // 채팅발송 시간
}
