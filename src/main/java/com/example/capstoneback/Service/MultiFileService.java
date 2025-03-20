package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.MultiFile;
import com.example.capstoneback.Error.EmailDoesntExistException;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.FileDoesntExistException;
import com.example.capstoneback.Repository.EmailRepository;
import com.example.capstoneback.Repository.MultiFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MultiFileService {

    @Value("${FILE_DIR}")
    private String uploadDir;

    private final MultiFileRepository multifileRepository;
    private final EmailRepository emailRepository;

    @Transactional
    public void saveFile(MultipartFile file, Long mailId) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // 저장할 파일 경로 설정
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes()); // 파일 저장

        // 해당 이메일 조회
        Optional<Email> op_email = emailRepository.findById(mailId);
        if(op_email.isEmpty()){
            throw new EmailDoesntExistException(ErrorCode.EMAIL_DOESNT_EXIST);
        }

        // 파일 정보 DB에 저장
        MultiFile multifile = MultiFile.builder()
                .email(op_email.get())
                .fileName(fileName)
                .filePath(filePath.toString())
                .build();

        multifileRepository.save(multifile);
    }

    @Transactional
    public Resource findFileByEmailIdAndFileName(Long mailId, String fileName, Authentication authentication) throws MalformedURLException, FileNotFoundException, IllegalAccessException {

        // 이메일이 존재하는지 확인
        Optional<Email> op_email = emailRepository.findById(mailId);
        if(op_email.isEmpty()) {
            throw new EmailDoesntExistException(ErrorCode.EMAIL_DOESNT_EXIST);
        }
        Email email = op_email.get();

        // 계정 소유자와 이메일 보유자가 같은지 확인
        String username = authentication.getName();

        if(!email.getUser().getUsername().equals(username)){
            throw new IllegalAccessException("잘못된 접근입니다.");
        }

        // 해당 이메일과 연결된 파일 조회
        Optional<MultiFile> op_file = multifileRepository.findByEmailAndFileNameContaining(email, fileName);
        if(op_file.isEmpty()) {
            throw new FileDoesntExistException(ErrorCode.FILE_DOESNT_EXIST);
        }

        MultiFile file = op_file.get();

        // 파일 경로로 접근해서 파일 가져오기
        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        // 파일 존재 여부, 읽기 가능 여부 확인
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        return resource;
    }
}
