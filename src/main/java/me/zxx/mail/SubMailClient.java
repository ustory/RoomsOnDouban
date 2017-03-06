package me.zxx.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubMailClient {
    private String appId = "11861";
    private String appKey = "*";
    private String templateId = "Ppi3k";
    private String url = "https://api.mysubmail.com/mail/xsend";


    private static ObjectMapper objectMapper = new ObjectMapper();

    public void sendMail(List<String> to, Map variables, Map links) {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("appid", this.appId);
            request.put("to", to.stream().collect(Collectors.joining(",")));
            request.put("project", templateId);
            request.put("vars", objectMapper.writeValueAsString(variables));
            request.put("links", objectMapper.writeValueAsString(links));
            request.put("timestamp", System.currentTimeMillis() / 1000);
            request.put("asynchronous", "true");
            request.put("signature", this.appKey);

            Request.Post(url).bodyString(request.toString(), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
