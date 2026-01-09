package com.gdgoc.member.domain.profile.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3UploadService {

    private final S3Template s3Template;
    private final String bucketName;
    private final String region;

    public S3UploadService(S3Template s3Template, 
                           @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
                           @Value("${spring.cloud.aws.region.static:ap-northeast-2}") String region) {
        this.s3Template = s3Template;
        this.bucketName = bucketName;
        this.region = region;
    }

    /**
     * 프로필 이미지를 S3에 업로드하고 URL을 반환합니다.
     *
     * @param file 업로드할 이미지 파일
     * @param userId 사용자 ID (파일명에 사용)
     * @return 업로드된 이미지의 S3 URL
     * @throws IOException 파일 업로드 실패 시
     */
    public String uploadProfileImage(MultipartFile file, UUID userId) throws IOException {
        // 파일 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // S3에 저장할 파일명 생성: profiles/{userId}/{timestamp}{extension}
        String fileName = String.format("profiles/%s/%d%s", userId, System.currentTimeMillis(), extension);

        // S3에 업로드
        s3Template.upload(bucketName, fileName, file.getInputStream());

        // S3 URL 생성
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);

        return url;
    }

    /**
     * S3에서 이미지를 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지의 S3 URL
     */
    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 파일명 추출
            int bucketIndex = imageUrl.indexOf(bucketName);
            if (bucketIndex == -1) {
                return; // URL 형식이 맞지 않으면 무시
            }
            String fileName = imageUrl.substring(bucketIndex + bucketName.length() + 1);
            if (fileName != null && !fileName.isEmpty()) {
                s3Template.deleteObject(bucketName, fileName);
            }
        } catch (Exception e) {
            // 삭제 실패 시 로그만 남기고 예외는 던지지 않음 (기존 이미지가 없을 수 있음)
            System.err.println("Failed to delete image from S3: " + imageUrl + ", Error: " + e.getMessage());
        }
    }
}

