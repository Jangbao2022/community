package life.majiang.community.controller;

import life.majiang.community.dto.FileDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.dto.UCloudFileDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import life.majiang.community.util.UCloudFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FileController {

    @Autowired
    private UCloudFileUtil uCloudFileUtil;


    @Autowired
    private UserService userService;

    /**
     * 评论上传图片
     *
     * @param request
     * @return
     */
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //markdown上传时需要写的文件名
        MultipartFile file = multipartRequest.getFile("editormd-image-file");

        //得到一个图片的UCloud的文件对象
        UCloudFileDTO uCloudFileDTO = uCloudFileUtil.upload(file, file.getContentType(), file.getOriginalFilename());

        //管理上传文件
        uCloudFileUtil.fileUploadData(request, uCloudFileDTO);

        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl(uCloudFileDTO.getFileUrl());

        return fileDTO;
    }

    /**
     * 修改头像更新
     * @param request
     * @return
     */
    @RequestMapping("/avatar/upload")
    @ResponseBody
    public ResultDTO uploadAvatar(HttpServletRequest request) {
        UCloudFileDTO uCloudFileDTO;
        String avatar_url = request.getParameter("avatar_url");
        //如果是传来的图片地址
        if(avatar_url!=null){
          uCloudFileDTO=new UCloudFileDTO();
          uCloudFileDTO.setFileUrl(avatar_url);
          uCloudFileDTO.setFileName("local");
        }else {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            //markdown上传时需要写的文件名
            MultipartFile file = multipartRequest.getFile("avatar");

            //得到一个图片的UCloud的文件对象
            uCloudFileDTO = uCloudFileUtil.upload(file, file.getContentType(), file.getOriginalFilename());
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        } else {
            if (user.getAvatarName() != null&&!"local".equals(user.getAvatarName())) {
                //之前用过头像 删除之前的头像
                //UCloud删除以往头像
                uCloudFileUtil.deleteByFileName(user.getAvatarName());
            }
            //更新头像地址
            userService.updateAvatar(user.getId(), uCloudFileDTO);
            return ResultDTO.okOf();
        }
    }

}


