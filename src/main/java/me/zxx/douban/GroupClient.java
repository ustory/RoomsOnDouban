package me.zxx.douban;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupClient {
    private List<String> groups = new ArrayList<String>() {
        {
            add("146409");
            add("467799");
            add("76231");
            add("259227");
        }
    };
    private String url = "https://api.douban.com/v2/group/%s/topics?start=0&count=100";

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Map<String, List<Topic>> searchTopics(List<String> keys) {
        List<Topic> topics = new ArrayList<>();
        groups.forEach(it -> topics.addAll(getTopics(it)));

        Map<String, List<Topic>> result = new HashMap<>();
        keys.forEach(key -> result.put(key,
                topics.stream().filter(it -> it.getTitle().contains(key)
                        || it.getContent().contains(key))
                        .sorted((a, b) -> b.getUpdated().compareTo(a.getUpdated()))
                        .collect(Collectors.toList())));

        return result;
    }

    private List<Topic> getTopics(String groupId) {
        String requestUrl = String.format(url, groupId);

        List<Topic> topics = new ArrayList<>();
        try {
            String originResponse = Request.Get(requestUrl).execute().returnContent().asString();
            ObjectNode response = objectMapper.readValue(originResponse, ObjectNode.class);
            topics = objectMapper.readValue(response.get("topics").toString(),
                    new TypeReference<List<Topic>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return topics;
    }
}
