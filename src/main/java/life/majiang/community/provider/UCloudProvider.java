package life.majiang.community.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.DeleteObjectApi;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.bean.base.BaseResponseBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import life.majiang.community.dto.UCloudFileDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * 直接管理UFile的工具类  bucketName:yanbobo
 * 上传\删除
 */
@Component
public class UCloudProvider {

    //私钥
    @Value("${ucloud.ufile.public-key}")
    private String publicKey;

    //公钥
    @Value("${ucloud.ufile.private-key}")
    private String privateKey;

    //存放器名字
    @Value("${ucloud.ufile.bucket-name}")
    private String bucketName;

    //ObjectConfig地区
    @Value("${ucloud.ufile.region}")
    private String region;

    //ObjectConfig代理后缀
    @Value("${ucloud.ufile.proxySuffix}")
    private String proxySuffix;

    //过期时间
    @Value("${ucloud.ufile.expiresDuration}")
    private Integer expiresDuration;

    //对象授权
    private ObjectAuthorization objectAuthorization;

    //对象配置
    private ObjectConfig config;

    /**
     * 获取授权和配置
     */
    private void getAuthorizationAndConfig() {
        // Bucket相关API的授权器
        objectAuthorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
        config = new ObjectConfig(region, proxySuffix);
    }

    /**
     * UCloud上传文件
     *
     * @param inputStream
     * @param mimeType
     * @param fileName
     * @return
     */
    public UCloudFileDTO upload(InputStream inputStream, String mimeType, String fileName) {

        UCloudFileDTO uCloudFileDTO = new UCloudFileDTO();
        uCloudFileDTO.setBucketName(bucketName);
        //获取文件扩展名
        String[] filePaths = fileName.split("\\.");

        //文件名
        String generatedFileName;
        if (filePaths.length > 1) {
            //生成文件名
            generatedFileName = UUID.randomUUID().toString() + "." + filePaths[filePaths.length - 1];
            uCloudFileDTO.setFileName(generatedFileName);
        } else {
            throw new CustomizeException(CustomizeErrorCode.IMG_UPLOAD_FAIL);
        }
        try {

            getAuthorizationAndConfig();

            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(inputStream, mimeType)
                    .nameAs(generatedFileName)
                    .toBucket(bucketName)
                    .setOnProgressListener((bytesWritten, contentLength) -> {
                    })
                    .execute();
            if (response != null && response.getRetCode() == 0) {
                //设置过期时间  一天

                String url = UfileClient.object(objectAuthorization, config)
                        .getDownloadUrlFromPrivateBucket(generatedFileName, bucketName, expiresDuration)
                        .createUrl();
                uCloudFileDTO.setFileUrl(url);
                return uCloudFileDTO;
            } else {
                throw new CustomizeException(CustomizeErrorCode.IMG_UPLOAD_FAIL);
            }

        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.IMG_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.IMG_UPLOAD_FAIL);
        }
    }

    /**
     * 删除指定文件
     *
     * @param fileName
     * @return
     */
    public void deleteFile(String fileName) {

        getAuthorizationAndConfig();
        try {
            UfileClient.object(objectAuthorization, config)
                    .deleteObject(fileName, bucketName)
                    .execute();
        } catch (UfileClientException e) {
            e.printStackTrace();
        } catch (UfileServerException e) {
            e.printStackTrace();
        }

    }


}
