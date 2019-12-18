package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.dto.QueryDTO;
import life.majiang.community.enums.OrderByEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.QuestionExtMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.QuestionExample;
import life.majiang.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private QuestionMapper questionMapper;


    /**
     * @param queryDTO        封装查询要求的类
     * @param questionExample 根据example查询  传空为查询所有
     * @return
     */
    private PaginationDTO list(QueryDTO queryDTO, QuestionExample questionExample) {
        //封装question页面的类
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        Integer totalCount;
        if (questionExample != null) {
            //questionExample不为空 通过questionExample查询
            totalCount = (int) questionMapper.countByExample(questionExample);
        } else {
            //questionExample为空通过search查询
            totalCount = questionExtMapper.countBySearch(queryDTO);
        }

        if (totalCount == 0) {
            return null;
        }
        Integer offset = paginationDTO.setPageOffset(queryDTO, totalCount);
        List<Question> questionList;
        //得到封装过的question数组
        if (questionExample != null) {
            questionExample.setOrderByClause(OrderByEnum.ORDER_BY_GMT_MODIFIED.getSort());
            questionList = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample, new RowBounds(offset, queryDTO.getSize()));
        } else {
            questionList = questionExtMapper.selectBySearch(queryDTO);
        }
        paginationDTO.setPaginationDTODataForQuestion(questionList, userMapper);
        //返回一个封装好的页面对象
        return paginationDTO;

    }

    /**
     * 根据搜索关键字匹配标题查找对象
     *
     * @param queryDTO 封装查询要求的类
     * @return
     */
    public PaginationDTO getQuestionBySearch(QueryDTO queryDTO) {
        String search = queryDTO.getSearch();
        String[] keys = search.split(" ");
        String keysRegexp = Arrays.stream(keys).collect(Collectors.joining("|"));
        queryDTO.setSearch(keysRegexp);
        return list(queryDTO, null);
    }

    //根据userId查询用户发布的所有问题
    public PaginationDTO getQuestionsByUserId(Long userId, QueryDTO queryDTO) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        return list(queryDTO, questionExample);
    }

    //查询出所有问题
    public PaginationDTO getAllQuestion(QueryDTO queryDTO) {
        QuestionExample questionExample = new QuestionExample();
        return list(queryDTO, questionExample);
    }

    /**
     * 通过questionId查找question并封装成questionDTO
     *
     * @param id
     * @return
     */
    public QuestionDTO findById(Long id) {

        QuestionDTO questionDto = new QuestionDTO();
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());

        BeanUtils.copyProperties(question, questionDto);
        questionDto.setUser(user);

        return questionDto;

    }

    public void createOrUpdate(Question question) {

        if (question.getId() == null) {
            //第一次创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        } else {
            //更新
            Question updateQuestion = new Question();

            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);

            if (updated == 0) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void inView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);

        questionExtMapper.incViewCount(question);

    }

    /**
     * 通过当前问题查询到相关问题
     *
     * @param queryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }

        //拆分出所有tag
        String[] tags = StringUtils.split(queryDTO.getTag(), ',');

        //得到所有tag拼接的正则表达式
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));


        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        //查询得到所有相关question
        List<Question> questions = questionExtMapper.selectRelated(question);

        //封装成questionDTO
        List<QuestionDTO> questionDTOs = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOs;
    }
}
