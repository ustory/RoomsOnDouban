package me.zxx;

import me.zxx.douban.GroupClient;
import me.zxx.douban.Topic;
import me.zxx.mail.SubMailClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> keys = new ArrayList<String>() {
            {
                add("华鑫天地");
                add("宜州");
                add("田林");
                add("虹漕");
                add("桂林");
                add("漕河泾");
                add("虹梅");
            }
        };


        Map<String, List<Topic>> topics = getTopics(keys);
        keys.forEach(it -> sendMail(topics.get(it), it));
    }

    private static Map<String, List<Topic>> getTopics(List<String> keys) {
        GroupClient groupClient = new GroupClient();
        return groupClient.searchTopics(keys);
    }

    private static void sendMail(List<Topic> topics, String key) {
        if (topics.size() == 0) {
            return;
        }

        List<String> sendTo = new ArrayList<String>() {
            {
                add("xinxin.zhong@nakedhub.com");
                //add("**@qq.com");
            }
        };

        Map<String, Object> variables = new HashMap<String, Object>() {{
            put("key", key);
        }};

        Map<String, Object> links = new HashMap<>();

        for (int i = 0; i < 20; i++) {
            String baseKey = String.format("topics%d.", i + 1);
            variables.put(baseKey + "updated", topics.size() <= i ? "无数据" : topics.get(i).getUpdated().toString());
            variables.put(baseKey + "title", topics.size() <= i ? "无数据" : topics.get(i).getTitle());

            links.put(baseKey + "url", topics.size() <= i ? "无数据" : topics.get(i).getAlt());

        }

        SubMailClient subMailClient = new SubMailClient();
        subMailClient.sendMail(sendTo, variables, links);
    }
}
