package com.study.douyin.basic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.douyin.basic.entity.UserEntity;
import com.study.douyin.basic.entity.VideoEntity;
import com.study.douyin.basic.vo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 视频
 */
public interface VideoService extends IService<VideoEntity> {

    List<VideoEntity> searchVideosByUserId(int userId);

    Video[] listVideoList(UserEntity user);

    Video[] getVideoListByVideoIds(List<Integer> videoIds, String token);

    /**
     * 将视频文件保存到指定文件夹
     * @param videoTargetFile
     * @param data
     * @throws IOException
     */
    void fetchVideoToFile(String videoTargetFile, MultipartFile data) throws IOException;

    /**
     * 截取视频文件某一帧，将其保存到指定文件夹
     * @param videoFile
     * @param targetFile
     * @param frameNum
     */
    void fetchFrameToFile(String videoFile, String targetFile, int frameNum);

    void saveVideoMsg(int userId, String videoPath, String coverPath, String title);
}
