package com.korea.project2_team4.Model.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomListResponseDto {
    private Long id;
    private String roomName;
    private String adminName;
    private int memberCount;
    private boolean inChatRoom;
    @Builder
    public ChatRoomListResponseDto(Long id, String roomName, String adminName, int memberCount, boolean inChatRoom){
        this.id = id;
        this.roomName = roomName;
        this.adminName = adminName;
        this.memberCount = memberCount;
        this.inChatRoom = inChatRoom;
    }

}
