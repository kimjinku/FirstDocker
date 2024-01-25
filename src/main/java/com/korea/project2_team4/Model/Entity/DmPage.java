package com.korea.project2_team4.Model.Entity;

import com.korea.project2_team4.Model.Dto.SaveMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class DmPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile me;

    @ManyToOne
    private Profile partner;// 여기서 receivedmessage불러와서 출력하면 되나??

    @OneToMany(mappedBy = "dmPage", cascade = CascadeType.REMOVE ,fetch = FetchType.EAGER) //many라고 해봤자 나랑 상대방 각각의 dmpage에저장?dkslsrk nn아닌가 ㅜㅜ
    List<SaveMessage> saveMessages;

    private LocalDateTime createDate;
}
