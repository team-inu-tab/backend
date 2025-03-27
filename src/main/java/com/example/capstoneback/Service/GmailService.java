package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.*;
import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Repository.EmailRepository;
import com.example.capstoneback.Repository.UserRepository;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GmailService {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    //Http 요청 객체와 Json 변환 객체 생성
    final static HttpTransport httpTransport = new NetHttpTransport();
    final static JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Transactional
    public void loadGmail(OAuth2AccessToken oAuth2AccessToken, User user) throws IOException {

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(oAuth2AccessToken.getTokenValue(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        saveInboxGmail(gmail, user); //받은 gmail 5개 db에 저장
        saveSentGmail(gmail, user); // 보낸 gamil 5개 db에 저장
    }

    private void saveInboxGmail(Gmail gmail, User user) throws IOException {
        // 받은 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(5L)
                .setLabelIds(List.of("INBOX"))
                .execute()
                .getMessages();

        // 받은 이메일 세부사항 확인 및 db에 저장
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher;
        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, sender = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 발신자, 수신 날짜 확인
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "From":
                        String from = header.getValue();
                        matcher = pattern.matcher(from);
                        sender = matcher.find() ? matcher.group(1) : from;
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            // 임시 메일 or 별표 메일인지 확인
            List<String> labelIds = detailMessage.getLabelIds();
            boolean isDraft = labelIds.contains("DRAFT");
            boolean isImportant = labelIds.contains("STARRED");

            // 이메일 엔티티 생성 및 저장
            Email email = Email.builder()
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .sender(sender)
                    .receiver(user.getEmail())
                    .isImportant(isImportant)
                    .sendAt(null)
                    .receiveAt(date)
                    .isDraft(isDraft)
                    .scheduledAt(null)
                    .user(user)
                    .build();

            emailRepository.save(email);
        }
    }

    private void saveSentGmail(Gmail gmail, User user) throws IOException {
        // 보낸 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(5L)
                .setLabelIds(List.of("SENT"))
                .execute()
                .getMessages();

        // 보낸 이메일 세부사항 확인 및 db에 저장
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher;
        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, receiver = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 수신자, 발신 날짜 확인
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "To":
                        String to = header.getValue();
                        matcher = pattern.matcher(to);
                        receiver = matcher.find() ? matcher.group(1) : to;
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            // 임시 메일 or 별표 메일인지 확인
            List<String> labelIds = detailMessage.getLabelIds();
            boolean isDraft = labelIds.contains("DRAFT");
            boolean isImportant = labelIds.contains("STARRED");

            // 이메일 엔티티 생성 및 저장
            Email email = Email.builder()
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .sender(user.getEmail())
                    .receiver(receiver)
                    .isImportant(isImportant)
                    .sendAt(date)
                    .receiveAt(null)
                    .isDraft(isDraft)
                    .scheduledAt(null)
                    .user(user)
                    .build();

            emailRepository.save(email);
        }
    }

    public List<ReceivedEmailResponseDTO> getReceivedGmail(Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        // 받은 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .setLabelIds(List.of("INBOX"))
                .execute()
                .getMessages();

        List<ReceivedEmailResponseDTO> receivedEmailDTOs = new ArrayList<>();

        for (Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, from = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 발신자, 수신 날짜 확인
            for (MessagePartHeader header : detailMessage.getPayload().getHeaders()) {
                switch (header.getName()) {
                    case "Subject":
                        title = header.getValue();
                        break;
                    case "From":
                        from = header.getValue();
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            List<String> labelIds = detailMessage.getLabelIds();
            List<HashMap<String, String>> fileNameList = getFileNameList(detailMessage);

            receivedEmailDTOs.add(ReceivedEmailResponseDTO.builder()
                    .id(detailMessage.getId())
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .sender(from)
                    .receiveAt(date)
                    .isImportant(labelIds.contains("STARRED"))
                    .fileNameList(fileNameList)
                    .build()
            );
        }

        return receivedEmailDTOs;
    }

    public List<SentEmailResponseDTO> getSentGmail(Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        // 보낸 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .setLabelIds(List.of("SENT"))
                .execute()
                .getMessages();

        List<SentEmailResponseDTO> sentEmailDTOs = new ArrayList<>();

        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, to = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 수신자, 발신 날짜 확인
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "To":
                        to = header.getValue();
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            List<String> labelIds = detailMessage.getLabelIds();
            List<HashMap<String, String>> fileNameList = getFileNameList(detailMessage);

            sentEmailDTOs.add(SentEmailResponseDTO.builder()
                    .id(detailMessage.getId())
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .receiver(to)
                    .sendAt(date)
                    .isImportant(labelIds.contains("STARRED"))
                    .fileNameList(fileNameList)
                    .build()
            );
        }
        return sentEmailDTOs;
    }

    public List<SelfEmailResponseDTO> getSelfGmail(Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        // 내게 보낸 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .setLabelIds(List.of("SENT", "INBOX"))
                .execute()
                .getMessages();

        List<SelfEmailResponseDTO> selfEmailDTOs = new ArrayList<>();

        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 수신자, 발신 날짜 확인
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            List<String> labelIds = detailMessage.getLabelIds();
            List<HashMap<String, String>> fileNameList = getFileNameList(detailMessage);

            selfEmailDTOs.add(SelfEmailResponseDTO.builder()
                    .id(detailMessage.getId())
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .sendAt(date)
                    .isImportant(labelIds.contains("STARRED"))
                    .fileNameList(fileNameList)
                    .build()
            );
        }

        return selfEmailDTOs;
    }

    public List<ImportantEmailResponseDTO> getImportantGmail(Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        // 중요 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .setLabelIds(List.of("STARRED"))
                .execute()
                .getMessages();

        List<ImportantEmailResponseDTO> importantEmailDTOs = new ArrayList<>();

        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, from= null, to = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 수신자, 발신 날짜 확인
            for (MessagePartHeader header : detailMessage.getPayload().getHeaders()) {
                switch (header.getName()) {
                    case "Subject":
                        title = header.getValue();
                        break;
                    case "To":
                        to = header.getValue();
                        break;
                    case "From":
                        from = header.getValue();
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            List<String> labelIds = detailMessage.getLabelIds();
            List<HashMap<String, String>> fileNameList = getFileNameList(detailMessage);

            if(labelIds.contains("SENT") && labelIds.contains("INBOX")) {
                importantEmailDTOs.add(ImportantEmailResponseDTO.builder()
                        .id(detailMessage.getId())
                        .title(title)
                        .content(detailMessage.getSnippet())
                        .sender(from)
                        .receiver(to)
                        .sendAt(date)
                        .receiveAt(null)
                        .fileNameList(fileNameList)
                        .build()
                );
            } else if (labelIds.contains("SENT")) {
                importantEmailDTOs.add(ImportantEmailResponseDTO.builder()
                        .id(detailMessage.getId())
                        .title(title)
                        .content(detailMessage.getSnippet())
                        .sender(from)
                        .receiver(to)
                        .sendAt(date)
                        .receiveAt(null)
                        .fileNameList(fileNameList)
                        .build()
                );
            }else if(labelIds.contains("INBOX")){
                importantEmailDTOs.add(ImportantEmailResponseDTO.builder()
                        .id(detailMessage.getId())
                        .title(title)
                        .content(detailMessage.getSnippet())
                        .sender(from)
                        .receiver(to)
                        .sendAt(null)
                        .receiveAt(date)
                        .fileNameList(fileNameList)
                        .build()
                );
            }else if(labelIds.contains("DRAFT")){
                importantEmailDTOs.add(ImportantEmailResponseDTO.builder()
                        .id(detailMessage.getId())
                        .title(title)
                        .content(detailMessage.getSnippet())
                        .sender(from)
                        .receiver(to)
                        .sendAt(null)
                        .receiveAt(null)
                        .fileNameList(fileNameList)
                        .build()
                );
            }else{
                System.out.println("중요 메일 처리 중 예외 메일 발생");
            }
        }
        return importantEmailDTOs;
    }

    public List<DraftEmailResponseDTO> getDraftGmail(Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        // 임시 이메일 리스트 요청
        List<Message> inboxMessages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .setLabelIds(List.of("DRAFT"))
                .execute()
                .getMessages();

        List<DraftEmailResponseDTO> draftEmailDTOs = new ArrayList<>();

        for(Message messageInfo : inboxMessages) {
            // 단일 이메일 조회
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, to = null;
            LocalDateTime date = null;

            // 헤더에서 제목, 수신자, 발신 날짜 확인
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue();
                        break;
                    case "To":
                        to = header.getValue();
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        date = koreaTime.toLocalDateTime();
                        break;
                    default:
                        break;
                }
            }

            List<String> labelIds = detailMessage.getLabelIds();
            List<HashMap<String, String>> fileNameList = getFileNameList(detailMessage);

            draftEmailDTOs.add(DraftEmailResponseDTO.builder()
                    .id(detailMessage.getId())
                    .title(title)
                    .content(detailMessage.getSnippet())
                    .receiver(to)
                    .createdAt(date)
                    .isImportant(labelIds.contains("STARRED"))
                    .fileNameList(fileNameList)
                    .build()
            );
        }

        return draftEmailDTOs;
    }

    public String getFileResource(String mailId, String attachmentId, Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        MessagePartBody file = gmail.users().messages().attachments()
                .get(user.getEmail(), mailId, attachmentId).execute();

        return file.getData();
    }

    // Message에서 fileName과 attachmentId로 구성된 HashMap 리스트를 추출해서 반환하는 메서드
    private static List<HashMap<String, String>> getFileNameList(Message detailMessage) {
        List<MessagePart> messageParts = detailMessage.getPayload().getParts();
        List<HashMap<String, String>> fileNameList = new ArrayList<>();

        if(messageParts != null){
            for(MessagePart messagePart : messageParts){
                String fileName = messagePart.getFilename();
                if(fileName.isEmpty()){
                    continue;
                }
                HashMap<String, String> fileInfo = new HashMap<>();
                fileInfo.put("fileName", fileName);
                fileInfo.put("attachmentId", messagePart.getBody().getAttachmentId());
                fileNameList.add(fileInfo);
            }
        }
        return fileNameList;
    }
}
