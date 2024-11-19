package org.zpi.conferoapi.textrazor;

import com.textrazor.TextRazor;
import com.textrazor.annotations.Topic;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.TextrazorApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class TextrazorController implements TextrazorApi {
    @Value("${textrazor.api.key}")
    private String apiKey;
    private TextRazor client;

    @PostConstruct
    public void init() {
        client = new TextRazor(apiKey);
        client.setExtractors(List.of("topics", "entities"));
        client.setDoCompression(true);
    }

    @Override
    public ResponseEntity<List<String>> getTagsFromText(String text) {
        try {
            return ResponseEntity.ok(client.analyze(text).getResponse().getTopics().stream()
                    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                    .filter(topic -> topic.getScore() > 0.5)
                    .map(Topic::getLabel)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
