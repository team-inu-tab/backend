package com.example.capstoneback.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class SpamEmailService {
    private final Gmail gmail;

    public SpamEmailService(Gmail gmail) {
        this.gmail = gmail;
    }

    public void markMessageAsSpam(String userId, String messageId) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest()
                .setAddLabelIds(Collections.singletonList("SPAM"));

        gmail.users().messages().modify(userId, messageId, mods).execute();
    }
}
