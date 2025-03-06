package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.MultiFile;
import com.example.capstoneback.Repository.MultiFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class MultiFileService {

    @Value("${FILE_DIR}")
    private String uploadDir;

    private final MultiFileRepository multifileRepository;

    @Transactional
    public void saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // 저장할 파일 경로 설정
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes()); // 파일 저장

        // 파일 정보 DB에 저장
        MultiFile multifile = MultiFile.builder()
                .fileName(fileName)
                .filePath(filePath.toString())
                .build();

        multifileRepository.save(multifile);
    }
}
