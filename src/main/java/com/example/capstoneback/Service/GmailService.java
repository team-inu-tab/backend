package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.EmailRepository;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GmailService {

    private final EmailRepository emailRepository;

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
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, sender = null;
            LocalDateTime date = null;
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "From":
                        String from = header.getValue();
                        matcher = pattern.matcher(from);
                        sender = matcher.find() ? matcher.group(1) : null;
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "");
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
            Message detailMessage = gmail.users().messages().get("me", messageInfo.getId()).execute();
            String title = null, receiver = null;
            LocalDateTime date = null;
            for(MessagePartHeader header : detailMessage.getPayload().getHeaders()){
                switch (header.getName()){
                    case "Subject":
                        title = header.getValue(); break;
                    case "To":
                        String to = header.getValue();
                        matcher = pattern.matcher(to);
                        receiver = matcher.find() ? matcher.group(1) : null;
                        break;
                    case "Date":
                        // Date를 LocalDateTime 타입에 맞게 변환
                        String dateString = header.getValue().replace(" (UTC)", "");
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
            boolean isDraft = labelIds.contains("DRAFT");
            boolean isImportant = labelIds.contains("STARRED");

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
}
