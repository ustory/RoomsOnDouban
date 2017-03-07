package me.zxx;

import me.zxx.douban.GroupClient;
import me.zxx.douban.Topic;
import me.zxx.mail.SubMailClient;
import me.zxx.redis.RedisClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


        Map<String, List<Topic>> topics = searchTopics(keys);
        sendMail(topics);
    }

    private static Map<String, List<Topic>> searchTopics(List<String> keys) {
        GroupClient groupClient = new GroupClient();
        return groupClient.searchTopics(keys);
    }

    private static void sendMail(Map<String, List<Topic>> topics) {
        List<String> sendTo = new ArrayList<String>() {
            {
                add("xinxin.zhong@nakedhub.com");
                //add("**@qq.com");
            }
        };

        String content = "<p><h1>豆瓣小组筛选消息</h1></p>";
        for (String key : topics.keySet()) {
            content += fillUpEmailContent(key, topics.get(key));
        }

        if (content.length() < 30) {//没有匹配到数据
            return;
        }

        SubMailClient subMailClient = new SubMailClient();
        subMailClient.sendMail(sendTo, content);
    }

    private static String fillUpEmailContent(String key, List<Topic> topics) {
        String title = String.format("<p><h2>关键字：%s</h2></p>", key);
        String contents = topics.stream().filter(Main::sendThisTopic).limit(30)
                .map(it -> {
                    setMailSent(it);
                    return String.format("<p>%s -- <a href='%s'>%s</a></p><hr/>",
                            it.getUpdated().toString(), it.getAlt(), it.getTitle());
                }).collect(Collectors.joining());
        return contents.equals("") ? "" : title + contents;
    }

    private static boolean sendThisTopic(Topic topic) {
        return RedisClient.get(topic.getId()) == null && RedisClient.get(topic.getAuthor().getId()) == null;
    }

    private static void setMailSent(Topic topic) {
        RedisClient.set(topic.getId(), LocalDateTime.now().toString());
        RedisClient.set(topic.getAuthor().getId(), LocalDateTime.now().toString());
    }
}
