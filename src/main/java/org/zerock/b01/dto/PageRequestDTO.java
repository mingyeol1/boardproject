package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; //검색 타입 종류 :  t ,c ,w ,tc, tw, twc

    private String keyword;

    public String[] getType() { //type을 불러오면 Type이 불려온다.
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split(""); //스플릿을 하고 아무 글자를 안넣으면 한글자 한글자 다 나눠버린다. //타입을 위한 내용을 나누기 위해서
    }

    public Pageable getPageable(String...props){ //(String...props (가변))
        return  PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }
    private String link;

    public String getLink(){
        if(link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);
            if (type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }
            if (keyword != null) {
                try {
                    builder.append("&keyword" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }
            link = builder.toString();
        }
        return link;
    }
}
