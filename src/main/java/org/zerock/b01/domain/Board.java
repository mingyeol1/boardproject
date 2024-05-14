package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

//엔터티 세팅 설정.
@Entity //엔티티(테이블) 선언
@Getter
@Builder    //빌더를 이용한 객체생성을 하겠다.
@AllArgsConstructor //생성자
@NoArgsConstructor  //기본생성자
@ToString(exclude = "imageSet")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //값을 자동 생성 오토 인클리먼트
    /*
        IDENTITY : 데이터베이스에 위임하는것 (AUTO_INCREMENT)
        SEQUENCE : 데이터베이스 시퀸스 오브젝트를 사용 - @SequenceGenerator(오라클에서 사용) 필요함....
        TABLE : 키 생성용 테이블 사용. 모든 DB에서 사용 - @tableGenerator 필요함..
        AUTO : 방언(내가 쓰는 DB에 따라)에 따라서 자동 지정된다. 기본값
    */
    private Long bno;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null값 허용여부(not null과 같음)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    // 엔티티 내에서 변경 가능한 title과 content 값을 수저하는 메서드
    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages() {

        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }


}
