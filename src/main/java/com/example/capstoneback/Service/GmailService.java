package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.*;
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
import com.google.api.services.gmail.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GmailService {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    //Http 요청 객체와 Json 변환 객체 생성
    final static HttpTransport httpTransport = new NetHttpTransport();
    final static JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

//    @Transactional
//    public void loadGmail(OAuth2AccessToken oAuth2AccessToken, User user) throws IOException {
//
//        // OAuth2 AccessToken을 GoogleCredentials로 변환
//        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(oAuth2AccessToken.getTokenValue(), null));
//
//        //Gmail api 요청 객체 생성
//        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
//                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
//                .setApplicationName("maeil-mail")
//                .build();
//
//        saveInboxGmail(gmail, user); //받은 gmail 5개 db에 저장
//        saveSentGmail(gmail, user); // 보낸 gamil 5개 db에 저장
//    }

//    private void saveInboxGmail(Gmail gmail, User user) throws IOException {
//        // 받은 이메일 리스트 요청
//        List<Message> inboxMessages = gmail.users().messages().list(userEmail)
//                .setMaxResults(5L)
//                .setLabelIds(List.of("INBOX"))
//                .execute()
//                .getMessages();
//
//        // 받은 이메일 세부사항 확인 및 db에 저장
//        Pattern pattern = Pattern.compile("<(.*?)>");
//        Matcher matcher;
//        for(Message messageInfo : inboxMessages) {
//            // 단일 이메일 조회
//            Message detailMessage = gmail.users().messages().get(userEmail, messageInfo.getId()).execute();
//            String title = null, sender = null;
//            LocalDateTime date = null;
//
//            // 헤더에서 제목, 발신자, 수신 날짜 확인
//            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
//                switch (header.getName()){
//                    case "Subject":
//                        title = header.getValue(); break;
//                    case "From":
//                        String from = header.getValue();
//                        matcher = pattern.matcher(from);
//                        sender = matcher.find() ? matcher.group(1) : from;
//                        break;
//                    case "Date":
//                        // Date를 LocalDateTime 타입에 맞게 변환
//                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
//                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
//                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
//                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
//                        date = koreaTime.toLocalDateTime();
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            // 임시 메일 or 별표 메일인지 확인
//            List<String> labelIds = detailMessage.getLabelIds();
//            boolean isDraft = labelIds.contains("DRAFT");
//            boolean isImportant = labelIds.contains("STARRED");
//
//            // 이메일 엔티티 생성 및 저장
//            Email email = Email.builder()
//                    .title(title)
//                    .content(detailMessage.getSnippet())
//                    .sender(sender)
//                    .receiver(user.getEmail())
//                    .isImportant(isImportant)
//                    .sendAt(null)
//                    .receiveAt(date)
//                    .isDraft(isDraft)
//                    .scheduledAt(null)
//                    .user(user)
//                    .build();
//
//            emailRepository.save(email);
//        }
//    }

//    private void saveSentGmail(Gmail gmail, User user) throws IOException {
//        // 보낸 이메일 리스트 요청
//        List<Message> inboxMessages = gmail.users().messages().list(userEmail)
//                .setMaxResults(5L)
//                .setLabelIds(List.of("SENT"))
//                .execute()
//                .getMessages();
//
//        // 보낸 이메일 세부사항 확인 및 db에 저장
//        Pattern pattern = Pattern.compile("<(.*?)>");
//        Matcher matcher;
//        for(Message messageInfo : inboxMessages) {
//            // 단일 이메일 조회
//            Message detailMessage = gmail.users().messages().get(userEmail, messageInfo.getId()).execute();
//            String title = null, receiver = null;
//            LocalDateTime date = null;
//
//            // 헤더에서 제목, 수신자, 발신 날짜 확인
//            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
//                switch (header.getName()){
//                    case "Subject":
//                        title = header.getValue(); break;
//                    case "To":
//                        String to = header.getValue();
//                        matcher = pattern.matcher(to);
//                        receiver = matcher.find() ? matcher.group(1) : to;
//                        break;
//                    case "Date":
//                        // Date를 LocalDateTime 타입에 맞게 변환
//                        String dateString = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "");
//                        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
//                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter);
//                        ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
//                        date = koreaTime.toLocalDateTime();
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            // 임시 메일 or 별표 메일인지 확인
//            List<String> labelIds = detailMessage.getLabelIds();
//            boolean isDraft = labelIds.contains("DRAFT");
//            boolean isImportant = labelIds.contains("STARRED");
//
//            // 이메일 엔티티 생성 및 저장
//            Email email = Email.builder()
//                    .title(title)
//                    .content(detailMessage.getSnippet())
//                    .sender(user.getEmail())
//                    .receiver(receiver)
//                    .isImportant(isImportant)
//                    .sendAt(date)
//                    .receiveAt(null)
//                    .isDraft(isDraft)
//                    .scheduledAt(null)
//                    .user(user)
//                    .build();
//
//            emailRepository.save(email);
//        }
//    }

    public Map<String, Object> getReceivedGmail(String pageToken, Authentication auth) throws IOException {
        return getEmailsByLabel(pageToken, auth, "INBOX", ReceivedEmailResponseDTO.class);
    }

    public Map<String, Object> getSentGmail(String pageToken, Authentication auth) throws IOException {
        return getEmailsByLabel(pageToken, auth, "SENT", SentEmailResponseDTO.class);
    }

    public Map<String, Object> getImportantGmail(String pageToken, Authentication auth) throws IOException {
        return getEmailsByLabel(pageToken, auth, "STARRED", ImportantEmailResponseDTO.class);
    }

    public Map<String, Object> getSpamGmail(String pageToken, Authentication auth) throws IOException {
        return getEmailsByLabel(pageToken, auth, "SPAM", SpamEmailResponseDTO.class);
    }

    public Map<String, Object> getTrashGmail(String pageToken, Authentication auth) throws IOException {
        return getEmailsByLabel(pageToken, auth, "TRASH", TrashGmailResponseDTO.class);
    }

    public Map<String, Object> getDraftGmail(String pageToken, Authentication auth) throws IOException {
        User user = getUser(auth);
        Gmail gmail = getGmailService(user);
        ListDraftsResponse draftsResponse;

        if(pageToken == null){
            draftsResponse = gmail.users().drafts()
                    .list(user.getEmail())
                    .setMaxResults(10L)
                    .execute();
        }else{
            draftsResponse = gmail.users().drafts()
                    .list(user.getEmail())
                    .setPageToken(pageToken)
                    .setMaxResults(10L)
                    .execute();
        }

        Map<String, Object> result = new HashMap<>();

        List<Draft> drafts = draftsResponse.getDrafts();
        String nextPageToken = draftsResponse.getNextPageToken();

        List<DraftEmailResponseDTO> draftDTOs = new ArrayList<>();

        if (drafts != null) {
            for (Draft draft : drafts) {
                String draftId = draft.getId();

                Message message = gmail.users().messages()
                        .get(user.getEmail(), draft.getMessage().getId())
                        .execute();

                String subject = null, receiver = null;
                LocalDateTime createdAt = null;

                for (MessagePartHeader header : message.getPayload().getHeaders()) {
                    if ("Subject".equals(header.getName())) subject = header.getValue();
                    if ("To".equals(header.getName())) receiver = header.getValue();
                    if ("Date".equals(header.getName())) {
                        String dateStr = header.getValue().replace(" (UTC)", "").replace(" (GMT)", "")
                                .replaceAll("\\s{2,}", " "); // 연속된 공백을 하나로 변환
                        ZonedDateTime zdt = ZonedDateTime.parse(dateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
                        createdAt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
                    }
                }

                List<HashMap<String, String>> attachments = getAttachments(message);

                DraftEmailResponseDTO dto = DraftEmailResponseDTO.builder()
                        .draftId(draftId)
                        .id(message.getId())
                        .title(subject)
                        .content(message.getPayload())
                        .receiver(receiver)
                        .createdAt(createdAt)
                        .isImportant(message.getLabelIds().contains("STARRED"))
                        .fileNameList(attachments)
                        .build();

                draftDTOs.add(dto);
            }
        }

        result.put("emails", draftDTOs);
        result.put("nextPageToken", nextPageToken);

        return result;
    }

    // 내게 쓴 메일함은 라벨이 SENT, INBOX가 동시에 붙어있어서 user entity 조회
    public Map<String, Object> getSelfGmail(String pageToken, Authentication auth) throws IOException {
        User user = getUser(auth);
        Gmail gmail = getGmailService(user);

        ListMessagesResponse messagesResponse;

        if(pageToken == null){
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setLabelIds(List.of("SENT", "INBOX"))
                    .setMaxResults(10L).execute();
        }else{
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setLabelIds(List.of("SENT", "INBOX"))
                    .setPageToken(pageToken)
                    .setMaxResults(10L).execute();
        }

        String nextPageToken = messagesResponse.getNextPageToken();
        List<Message> messages = messagesResponse.getMessages();

        Map<String, Object> result = new HashMap<>();

        List<SelfEmailResponseDTO> emails = new ArrayList<>();
        for (Message msg : messages) {
            emails.add(mapToDTO(gmail, user.getEmail(), msg, SelfEmailResponseDTO.class));
        }

        result.put("emails", emails);
        result.put("nextPageToken", nextPageToken);

        return result;
    }

    // 파일 첨부 리소스 조회
    public String getFileResource(String mailId, String attachmentId, Authentication auth) throws IOException {
        User user = getUser(auth);
        Gmail gmail = getGmailService(user);
        return gmail.users().messages().attachments()
                .get(user.getEmail(), mailId, attachmentId).execute().getData();
    }

    // 특정 이메일 검색
    public Map<String, Object> searchGmailsByUserEmail(SearchGmailsByUserEmailRequestDTO request, String pageToken, Authentication auth) throws IOException {
        User user = getUser(auth);
        Gmail gmail = getGmailService(user);
        ListMessagesResponse messagesResponse;

        String query = String.format("{from: %s AND is:inbox} OR {to: %s AND is:sent}",
                request.getUserEmail(), request.getUserEmail());

        if(pageToken == null){
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setQ(query)
                    .setMaxResults(10L).execute();
        }else{
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setQ(query)
                    .setPageToken(pageToken)
                    .setMaxResults(10L).execute();
        }

        String nextPageToken = messagesResponse.getNextPageToken();
        List<Message> messages = messagesResponse.getMessages();

        List<ImportantEmailResponseDTO> emails = new ArrayList<>();
        for (Message msg : messages) {
            emails.add(mapToDTO(gmail, user.getEmail(), msg, ImportantEmailResponseDTO.class));
        }

        Map<String, Object> result = new HashMap<>();

        result.put("emails", emails);
        result.put("nextPageToken", nextPageToken);

        return result;
    }

    // 유저 조회
    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST));
    }

    // Gmail 생성
    private Gmail getGmailService(User user) {
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));
        return new Gmail.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();
    }

    // mailId로 단일 메시지 조회
    public SentEmailResponseDTO getSentGmailWithMailId(String mailId, Authentication auth) throws IOException {
        User user = getUser(auth);  // 인증된 사용자 가져오기
        Gmail gmail = getGmailService(user);  // Gmail 서비스 생성

        // 메일 상세 정보 조회
        Message message = gmail.users().messages().get(user.getEmail(), mailId).execute();

        // 헤더 정보 파싱
        Map<String, String> headers = new HashMap<>();
        message.getPayload().getHeaders().forEach(h -> headers.put(h.getName(), h.getValue()));

        // 날짜 파싱
        String dateStr = headers.getOrDefault("Date", "").replace(" (UTC)", "").replace(" (GMT)", "")
                .replaceAll("\\s{2,}", " "); // 연속된 공백 제거
        LocalDateTime date = null;
        if (!dateStr.isEmpty()) {
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            date = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        // 첨부파일 파싱
        List<HashMap<String, String>> attachments = getAttachments(message);

        // DTO 생성 및 반환
        return SentEmailResponseDTO.builder()
                .id(message.getId())
                .title(headers.get("Subject"))
                .receiver(headers.get("To"))
                .content(message.getPayload())
                .sendAt(date)
                .isImportant(message.getLabelIds().contains("STARRED"))
                .fileNameList(attachments)
                .build();
    }

    /** 라벨 기준으로 메일 조회
     *
     * @param auth
     * @param label (수송신 임시 스팸 내게쓰기)
     * @param dtoClass
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> Map<String, Object> getEmailsByLabel(String pageToken, Authentication auth, String label, Class<T> dtoClass) throws IOException {
        User user = getUser(auth);
        Gmail gmail = getGmailService(user);
        ListMessagesResponse messagesResponse;

        if(pageToken == null){
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setLabelIds(List.of(label))
                    .setMaxResults(10L).execute();
        }else{
            messagesResponse = gmail.users().messages().list(user.getEmail())
                    .setPageToken(pageToken)
                    .setLabelIds(List.of(label))
                    .setMaxResults(10L).execute();
        }

        String nextPageToken = messagesResponse.getNextPageToken();
        List<Message> messages = messagesResponse.getMessages();

        Map<String, Object> result = new HashMap<>();

        List<T> emailDTOs = new ArrayList<>();
        if (messages != null) {
            for (Message msg : messages) {
                emailDTOs.add(mapToDTO(gmail, user.getEmail(), msg, dtoClass));
            }
        }

        result.put("emails", emailDTOs);
        result.put("nextPageToken", nextPageToken);
        return result;
    }

    // Message to DTO 변환
    private <T> T mapToDTO(Gmail gmail, String userEmail, Message message, Class<T> dtoClass) throws IOException {
        Message detail = gmail.users().messages().get(userEmail, message.getId()).execute();

        Map<String, String> headers = new HashMap<>();
        detail.getPayload().getHeaders().forEach(h -> headers.put(h.getName(), h.getValue()));

        String dateStr = headers.getOrDefault("Date", "").replace(" (UTC)", "").replace(" (GMT)", "")
                .replaceAll("\\s{2,}", " "); // 연속된 공백을 하나로 변환
        LocalDateTime date = null;
        if (!dateStr.isEmpty()) {
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            date = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        List<HashMap<String, String>> attachments = getAttachments(detail);

        // 받은 메일
        if (dtoClass == ReceivedEmailResponseDTO.class) {
            return dtoClass.cast(ReceivedEmailResponseDTO.builder()
                    .id(message.getId())
                    .title(headers.get("Subject"))
                    .sender(headers.get("From"))
                    .content(detail.getPayload())
                    .receiveAt(date)
                    .isImportant(detail.getLabelIds().contains("STARRED"))
                    .fileNameList(attachments).build());
        }
        // 보낸 메일
        if (dtoClass == SentEmailResponseDTO.class) {
            return dtoClass.cast(SentEmailResponseDTO.builder()
                    .id(message.getId())
                    .title(headers.get("Subject"))
                    .receiver(headers.get("To"))
                    .content(detail.getPayload())
                    .sendAt(date)
                    .isImportant(detail.getLabelIds().contains("STARRED"))
                    .fileNameList(attachments).build());
        }

        // 임시 메일
        if (dtoClass == DraftEmailResponseDTO.class) {
            return dtoClass.cast(DraftEmailResponseDTO.builder()
                    .id(message.getId())
                    .title(headers.get("Subject"))
                    .receiver(headers.get("To"))
                    .content(detail.getPayload())
                    .createdAt(date)
                    .isImportant(detail.getLabelIds().contains("STARRED"))
                    .fileNameList(attachments).build());
        }
        // 중요 메일
        if (dtoClass == ImportantEmailResponseDTO.class) {
            String mailType = getMailType(detail);
            return dtoClass.cast(ImportantEmailResponseDTO.builder()
                    .id(message.getId())
                    .mailType(mailType)
                    .title(headers.get("Subject"))
                    .sender(headers.get("From"))
                    .receiver(headers.get("To"))
                    .sendAt(detail.getLabelIds().contains("SENT") ? date : null)
                    .receiveAt(detail.getLabelIds().contains("INBOX") ? date : null)
                    .content(detail.getPayload())
                    .build());
        }
        // 내게 쓴 메일
        if (dtoClass == SelfEmailResponseDTO.class) {
            return dtoClass.cast(SelfEmailResponseDTO.builder()
                    .id(message.getId())
                    .title(headers.get("Subject"))
                    .content(detail.getPayload())
                    .sendAt(date)
                    .isImportant(detail.getLabelIds().contains("STARRED"))
                    .fileNameList(attachments).build());
        }
        // 스팸 메일
        if (dtoClass == SpamEmailResponseDTO.class) {
            return dtoClass.cast(SpamEmailResponseDTO.builder()
                    .id(message.getId())
                    .title(headers.get("Subject"))
                    .sender(headers.get("From"))
                    .content(detail.getPayload())
                    .receiveAt(date).isImportant(detail.getLabelIds().contains("STARRED"))
                    .fileNameList(attachments).build());
        }
        // 휴지통 메일
        if(dtoClass == TrashGmailResponseDTO.class) {
            String mailType;
            if(detail.getLabelIds().contains("SENT")){
                mailType = "sent";
            }else{
                mailType = "received";
            }
            if(headers.get("From").equals(headers.get("To"))) mailType = "self";

            LocalDateTime sendAt, receiveAt;
            if(detail.getLabelIds().contains("SENT")){
                sendAt = date;
                receiveAt = null;
            }else{
                sendAt = null;
                receiveAt = date;
            }
            return dtoClass.cast(TrashGmailResponseDTO.builder()
                    .id(message.getId())
                    .mailType(mailType)
                    .title(headers.get("Subject"))
                    .sender(headers.get("From"))
                    .receiver(headers.get("To"))
                    .sendAt(sendAt)
                    .receiveAt(receiveAt)
                    .content(detail.getPayload())
                    .build()
            );
        }
       
        throw new IllegalArgumentException("Unsupported DTO Type");
    }

    // 메일 임시 삭제
    public List<String> deleteGmailTemporary(DeleteGmailTemporaryRequestDTO requestDTO, Authentication authentication) throws IOException {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();
        String userEmail = user.getEmail();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        List<String> selectedMailIds = requestDTO.getSelectedMailIds();
        List<String> deletedMailIdList = new ArrayList<>();

        for (String mailId : selectedMailIds) {
            deletedMailIdList.add(gmail.users().messages().trash(userEmail, mailId).execute().getId());
        }

        return deletedMailIdList;
    }

    // 메일 영구 삭제
    public List<String> deleteGmailPermanent(DeleteGmailPermanentRequestDTO requestDTO, Authentication authentication) {
        String username = authentication.getName();

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if (op_user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        User user = op_user.get();
        String userEmail = user.getEmail();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();

        List<String> selectedMailIds = requestDTO.getSelectedMailIds();
        List<String> deletedMailIdList = new ArrayList<>();

        for (String mailId : selectedMailIds) {
            try{
                gmail.users().messages().delete(userEmail, mailId).execute();
                deletedMailIdList.add(mailId);
            }catch (IOException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        return deletedMailIdList;
    }

    // 첨부파일 추출
    private List<HashMap<String, String>> getAttachments(Message detail) {
        List<HashMap<String, String>> attachments = new ArrayList<>();
        if (detail.getPayload().getParts() != null) {
            for (MessagePart part : detail.getPayload().getParts()) {
                if (!part.getFilename().isEmpty()) {
                    attachments.add(new HashMap<>() {{
                        put("fileName", part.getFilename());
                        put("attachmentId", part.getBody().getAttachmentId());
                    }});
                }
            }
        }
        return attachments;
    }

    // 메일 타입 추출 메서드
    private static String getMailType(Message message) {
        String mailType;
        if(message.getLabelIds().contains("INBOX") && message.getLabelIds().contains("SENT")){
            mailType = "self";
        }else if(message.getLabelIds().contains("SENT")){
            mailType = "sent";
        }else if (message.getLabelIds().contains("INBOX")){
            mailType = "received";
        }else if (message.getLabelIds().contains("DRAFT")){
            mailType = "draft";
        }else{
            mailType = "unknown";
        }
        return mailType;
    }
}
