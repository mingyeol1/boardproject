package org.zerock.b01.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Long rno;
    @NotNull // 객체(내용) 자체가 없는 경우
    private Long bno; //게시글번호
    @NotEmpty
    private String replyText; //응답내용
    @NotEmpty
    private String replyer; //응답자

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") //잘짜 형식 설정 Json형식으로
    private LocalDateTime regDate;

    @JsonIgnore //포메팅할때 제외시켜라 //날짜 변환 무시.
    private LocalDateTime modDate;

}
