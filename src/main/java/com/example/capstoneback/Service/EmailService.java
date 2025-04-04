package com.example.capstoneback.Service;

import com.example.capstoneback.Repository.EmailRepository;
import com.example.capstoneback.Repository.MultiFileRepository;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final MultiFileRepository multiFileRepository;
    private final UserRepository userRepository;

//    public List<ReceivedEmailResponseDTO> getReceivedEmails(Authentication authentication) {
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최근에 받은 이메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndReceiverAndIsDraftIsFalseOrderByReceiveAtDesc(user, user.getEmail(), Limit.of(10));
//
//        return emails.stream().map(email -> ReceivedEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .sender(email.getSender())
//                .receiveAt(email.getReceiveAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public List<SentEmailResponseDTO> getSentEmails(Authentication authentication) {
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최근에 보낸 이메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndSenderAndIsDraftIsFalseOrderBySendAtDesc(user, user.getEmail(), Limit.of(10));
//
//        return emails.stream().map(email -> SentEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .receiver(email.getReceiver())
//                .sendAt(email.getSendAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public List<SelfEmailResponseDTO> getSelfEmails(Authentication authentication){
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최근 내게 보낸 이메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndReceiverAndSenderAndIsDraftIsFalseOrderBySendAtDesc(user, user.getEmail(), user.getEmail(), Limit.of(10));
//
//        return emails.stream().map(email -> SelfEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .sendAt(email.getSendAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public List<ImportantEmailResponseDTO> getImportantEmails(Authentication authentication){
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최신 중요 메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndIsImportantIsTrueLimit10(user, Limit.of(10));
//
//        return emails.stream().map(email -> ImportantEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .sendAt(email.getSendAt())
//                .sender(email.getSender())
//                .receiver(email.getReceiver())
//                .receiveAt(email.getReceiveAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public List<ScheduledEmailResponseDTO> getScheduledEmails(Authentication authentication){
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최신 예약 메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndScheduledAtIsAfterOrderByScheduledAtDesc(user, LocalDateTime.now());
//
//        return emails.stream().map(email -> ScheduledEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .receiver(email.getReceiver())
//                .scheduledAt(email.getScheduledAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public List<DraftEmailResponseDTO> getDraftEmails(Authentication authentication){
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 최신 임시 메일 10개 조회
//        List<Email> emails = emailRepository.findByUserAndIsDraftIsTrueOrderByCreatedAtDesc(user, Limit.of(10));
//
//        return emails.stream().map(email -> DraftEmailResponseDTO.builder()
//                .id(email.getId())
//                .title(email.getTitle())
//                .content(email.getContent())
//                .receiver(email.getReceiver())
//                .createdAt(email.getCreatedAt())
//                .isImportant(email.getIsImportant())
//                .isFileExist(multiFileRepository.existsByEmailId(email.getId()))
//                .build()).toList();
//    }

//    public MailDetailsResponseDTO getMailDetails(Long mailId ,Authentication authentication) throws IllegalAccessException {
//        String username = authentication.getName();
//
//        // 유저 확인
//        Optional<User> op_user = userRepository.findByUsername(username);
//        if(op_user.isEmpty()){
//            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
//        }
//
//        User user = op_user.get();
//
//        // 메일 가져오기
//        Email email = emailRepository.findById(mailId).orElseThrow(
//                () -> new EmailDoesntExistException(ErrorCode.EMAIL_DOESNT_EXIST)
//        );
//
//        // 메일 소유자와 요청자 동일한지 확인
//        if(email.getUser() != user){
//            throw new IllegalAccessException("email user doesnt match");
//        }
//
//        // 메일의 파일 조회 후 파일 이름 조회
//        List<MultiFile> multiFiles =  email.getMultiFiles();
//        List<String> fileName = multiFiles.stream().map(file ->
//                file.getFileName())
//                .toList();
//
//        // DTO 만들어서 리턴
//        return MailDetailsResponseDTO.builder()
//                .title(email.getTitle())
//                .content(email.getContent())
//                .sender(email.getSender())
//                .receiver(email.getReceiver())
//                .sendAt(email.getSendAt())
//                .receivedAt(email.getReceiveAt())
//                .isImportant(email.getIsImportant())
//                .fileName(fileName)
//                .build();
//    }
}
