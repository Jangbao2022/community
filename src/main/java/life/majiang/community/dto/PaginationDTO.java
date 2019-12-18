package life.majiang.community.dto;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {

    private List<T> data;

    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;

    //页面是否有元素
    private boolean pageFlag;

    //当前页
    private Integer page;

    //最后页
    private Integer endPage;
    //第一页
    private Integer firstPage=1;

    //展示的页面的页数
    private List<Integer> pages=new ArrayList<>();


    /**
     *设置pagination的基础属性  并返回offset
     * @param totalPage
     * @param page
     */
    public void setPagination(Integer totalPage, Integer page) {


        //复制当前页到PaginationDTO中
        this.page=page;
        //将总page数赋值给最后一页

        this.endPage=totalPage;
        //一页有五个question  一次可以看到7页
        for (int i=-3;i<=3;i++){
            if(page+i>=firstPage&&page+i<=endPage){
                pages.add(page+i);
            }
        }

        //如果pages没有值返回pageFlag=false 并且不再执行
        if(pages.size()==0){
            showPrevious=false;
            showNext=false;
            showFirstPage=false;
            showEndPage=false;

            this.pageFlag=false;

            return ;
        }

        //是否展示上一页
        if(page==firstPage){
            showPrevious=false;
        }else{
            showPrevious=true;
        }

        //是否展示下一页
        if(page==endPage){
            showNext=false;
        }else{
            showNext=true;
        }

        //是否展示达到第一页按钮 看页面第一个元素是不是1
        if(pages.get(0)!=firstPage){
            showFirstPage=true;
        }else{
            showFirstPage=false;
        }

        //是否展示到达最后一页按钮  看页面最后一个元素是不是totalPage
        if(pages.get(pages.size()-1)!=endPage){
            showEndPage=true;
        }else{
            showEndPage=false;
        }

        this.pageFlag=true;

    }

    /**
     * 检验page是否有误 设置paginationDTO的基本属型 并返回页数
     * @param totalCount
     * @return
     */
    public Integer setPageOffset(QueryDTO queryDTO, Integer totalCount) {
        Integer page= queryDTO.getPage();
        Integer size= queryDTO.getSize();
        //根据数量和size得到总页数
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage =  totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        //将page值更改至正确范围
        //如果页数大于总页数  则页数等于总页数
        if (page > totalPage) {
            page = totalPage;
        }
        //当页数小于1时  将页数置为1
        if (page < 1) {
            page = 1;
        }

        //根据totalPage和当前页设置内部属性的值
        //查看当前页面有没有元素
        setPagination(totalPage, page);

        //转换出正确的查询值
        Integer offset = size * (page - 1);
        return offset;
    }

    /**
     * 设置PaginationDTOData数据为questionDtoList
     * @param questionList
     */
    public void setPaginationDTODataForQuestion(List<Question> questionList,UserMapper userMapper) {
        List<QuestionDTO> questionDtoList = new ArrayList<>();

        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDto = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDto);
            questionDto.setUser(user);
            questionDtoList.add(questionDto);
        }

        //将questionDtoList封装到paginationDto中
        setData((List<T>) questionDtoList);
    }

}
