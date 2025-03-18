package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.ReceivedEmailResponseDTO;
import com.example.capstoneback.DTO.SelfEmailResponseDTO;
import com.example.capstoneback.DTO.SentEmailResponseDTO;
import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Repository.EmailRepository;
import com.example.capstoneback.Repository.MultiFileRepository;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final MultiFileRepository multiFileRepository;
    private final UserRepository userRepository;

    public List<ReceivedEmailResponseDTO> getReceivedEmails(Authentication authentication) {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if(op_user.isEmpty()){
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // 최근에 받은 이메일 10개 조회
        List<Email> emails = emailRepository.findByUserAndReceiverAndIsDraftIsFalse(user, user.getEmail(), Limit.of(10));

        return emails.stream().map(email -> ReceivedEmailResponseDTO.builder()
                .id(email.getId())
                .title(email.getTitle())
                .content(email.getContent())
                .sender(email.getSender())
                .receiveAt(email.getReceiveAt())
                .isImportant(email.getIsImportant())
                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
                .build()).toList();
    }

    public List<SentEmailResponseDTO> getSentEmails(Authentication authentication) {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if(op_user.isEmpty()){
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // 최근에 보낸 이메일 10개 조회
        List<Email> emails = emailRepository.findByUserAndSenderAndIsDraftIsFalse(user, user.getEmail(), Limit.of(10));

        return emails.stream().map(email -> SentEmailResponseDTO.builder()
                .id(email.getId())
                .title(email.getTitle())
                .content(email.getContent())
                .receiver(email.getReceiver())
                .sendAt(email.getSendAt())
                .isImportant(email.getIsImportant())
                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
                .build()).toList();
    }

    public List<SelfEmailResponseDTO> getSelfEmails(Authentication authentication){
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if(op_user.isEmpty()){
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // 최근 내게 보낸 이메일 10개 조회
        List<Email> emails = emailRepository.findByUserAndReceiverAndSenderAndIsDraftIsFalse(user, user.getEmail(), user.getEmail(), Limit.of(10));

        return emails.stream().map(email -> SelfEmailResponseDTO.builder()
                .id(email.getId())
                .title(email.getTitle())
                .content(email.getContent())
                .sendAt(email.getSendAt())
                .isImportant(email.getIsImportant())
                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
                .build()).toList();
    }
}
