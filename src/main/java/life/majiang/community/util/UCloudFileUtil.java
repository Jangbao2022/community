package life.majiang.community.util;

import life.majiang.community.dto.UCloudFileDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.provider.UCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 间接管理UFile的工具类
 */
@Component
public class UCloudFileUtil {

    @Autowired
    private UCloudProvider uCloudProvider;

    /**
     * 调用UCloudProvider上传函数
     *
     * @param mimeType
     * @param fileName
     * @return
     */
    public UCloudFileDTO upload(MultipartFile file, String mimeType, String fileName) {
        try {
            return uCloudProvider.upload(file.getInputStream(), mimeType, fileName);
        } catch (IOException e) {
            throw new CustomizeException(CustomizeErrorCode.IMG_UPLOAD_FAIL);
        }
    }

    /**
     * 上传多个文件
     *
     * @return
     */
    public List<UCloudFileDTO> uploadToList() {
        return null;
    }

    /**
     * 获取上传文件数据  存入session
     *
     * @param request       请求信息
     * @param uCloudFileDTO 上传的数据
     */
    public void fileUploadData(HttpServletRequest request, UCloudFileDTO uCloudFileDTO) {
        //将上传到UCloud中的图片url存到session中
        Map<String, String> oldImgs = (HashMap<String, String>) request.getSession().getAttribute("imgs");
        //如果session中有imgNames和imgUrls
        if (oldImgs != null) {
            oldImgs.put(uCloudFileDTO.getFileUrl(), uCloudFileDTO.getFileName());
            request.getSession().setAttribute("imgs", oldImgs);
        } else {
            Map<String, String> imgs = new HashMap<>();
            imgs.put(uCloudFileDTO.getFileUrl(), uCloudFileDTO.getFileName());
            request.getSession().setAttribute("imgs", imgs);
        }
    }


    /**
     * 删除UCloud中无用的文件
     * 只能删除上传时 不需要的图片 无法清除之前已经存到本地数据库的图片
     *
     * @param description 问题描述  含有文件信息
     * @param request
     */
    public void deleteNotNeedFile(HttpServletRequest request, String description) {

        Map<String, String> imgs = (HashMap<String, String>) request.getSession().getAttribute("imgs");
        List<String> notUploadImgs = new ArrayList<>();

        if (imgs != null) {
            //获取不需要的img组
            for (String imgUrl : imgs.keySet()) {
                String imgName = imgs.get(imgUrl);
                //问题描述是否包含图片
                boolean flag = !description.contains("![]");
                //如果描述不包含
                if (flag || !description.contains(imgName)) {
                    notUploadImgs.add(imgName);
                }
            }
            if (notUploadImgs.size()!=0) {
                //通过List集合删除图片
                deleteFileByList(notUploadImgs);
            }
            //清空Session
            request.getSession().removeAttribute("imgs");
        }

    }

    public void deleteFileByList(List<String> notUploadImgs) {
        //遍历集合删除图片
        for (String notUploadImg : notUploadImgs) {
            deleteByFileName(notUploadImg);
        }
    }

    public void deleteByFileName(String fileName) {
        uCloudProvider.deleteFile(fileName);
    }


}
