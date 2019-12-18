package life.majiang.community.dto;

import lombok.Data;

/**
 * 根据查询格式查询
 */
@Data
public class QueryDTO {

    private Integer page;
    private Integer size;
    private  String search;

    public QueryDTO(Integer page, Integer size, String search) {
        this.page = page;
        this.size = size;
        this.search = search;
    }

    public QueryDTO(Integer page, Integer size) {
        this.page = page;
        this.size = size;
        this.search = search;
    }
}
