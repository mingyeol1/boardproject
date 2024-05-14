package org.zerock.b01.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter //주어진 값으로 만들어진것을 불러오기만 하는 것.
@ToString
public class PageResponseDTO<E> {
    //기본 생성자가 필요 없음.

    private int page;
    private int size;
    private int total;

    // 시작페이지 페이지 번호를 말하는것
    private int start;
    //끝페이지 번호
    private int end;

    // 이전페이지 여부
    private boolean prev;
    //다음페이지 여부
    private boolean next;

    private List<E> dtoList; //게시글 내용 //위에서 정의한 제너릭값을 그대로 받는다.

    @Builder(builderMethodName = "withAll")     //위드 올로 만드는 특정 생성자를 만들겠다.
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total){
        if (total <= 0){
            return;
        }
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;

        this.dtoList = dtoList;

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10; //화면에 표시할 페이지 번호 갯수
        this.start = this.end - 9; //화면에서 시작번호
        int last = (int)(Math.ceil(total/(double)size)); //데이터 개수로 계산한 마지막 페이지 번호 real last

        this.end = end > last ? last : end;

        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}
