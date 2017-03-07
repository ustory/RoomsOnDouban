package me.zxx.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.util.List;
import java.util.stream.Collectors;

public class SubMailClient {
    private String appId = "11861";
    private String appKey = "*";
    private String url = "https://api.mysubmail.com/mail/send";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public void sendMail(List<String> to, String html) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("appid", this.appId);
        request.put("to", to.stream().collect(Collectors.joining(",")));
        request.put("from", "no-reply@email.livenaked.com");
        request.put("subject", "豆瓣小组筛选消息");
        request.put("html", html);
        request.put("timestamp", System.currentTimeMillis() / 1000);
        request.put("asynchronous", "true");
        request.put("signature", this.appKey);

        try {
            Request.Post(url).bodyString(request.toString(), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
