package life.majiang.community.mapper;

import life.majiang.community.dto.QueryDTO;
import life.majiang.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    /**
     * 增加浏览数
     * @param record
     * @return
     */
    int incViewCount(Question record);

    /**
     * 增加评论数
     * @param record
     * @return
     */
    int incCommentCount(Question record);

    /**
     * 根据标签查找相似的问题
     * 修改时间倒序输出
     * @param question
     * @return
     */
    List<Question>selectRelated(Question question);

    /**
     * 根据搜索关键字查找问题个数
     * @param queryDTO
     * @return
     */
    Integer countBySearch(QueryDTO queryDTO);

    /**
     * 根据搜索关键字查找问题列表
     * 修改时间倒序输出
     * @param queryDTO
     * @return
     */
    List<Question>selectBySearch(QueryDTO queryDTO);
}