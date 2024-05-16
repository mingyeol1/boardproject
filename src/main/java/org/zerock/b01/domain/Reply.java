package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
                            //참조하는 객체를 사용하지 않게 하기 위해서 exclude로 board를 제외한다.
@ToString //(exclude = "board") //ToString = 할때 보드라는 애를 빼고 출력을 해줘라.// 객체 정보를 불러 올 때 board를 빼라.

public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 관계로 구성됨 (연관관계시 fetch 타입은= FetchType.LAZY로 구성한다.)
    private Board board;                // board를 구분할 수 있는 PK인 bno가 들어간다. .LAZY : 나중에 로딩한다.
    private String replyText;
    private String replyer;

    public void changeText(String text){
        this.replyText = text;
    }
    // board 값 설정을 위해서 -> bno를 받아서 생성.
    public void setBoard(Long bno){
        this.board = Board.builder().bno(bno).build();
    }
    public void changeBoard(Board board){
        this.board = board;
    }


}
