package org.zerock.b01.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardLike extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_bno")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "member_mid")
    private Member member;
}
