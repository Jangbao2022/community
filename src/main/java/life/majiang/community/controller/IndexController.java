package life.majiang.community.controller;


import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QueryDTO;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;


    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search) {

        PaginationDTO pagination;
        if (StringUtils.isNotBlank(search)) {
            //如果search不是空 通过search查询
            pagination = questionService.getQuestionBySearch(new QueryDTO(page,size,search));
        } else {
            //否则查询所有
            pagination = questionService.getAllQuestion(new QueryDTO(page,size));
        }
        model.addAttribute("search", search);
        model.addAttribute("pagination", pagination);
        return "index";

    }
}
