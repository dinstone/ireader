
package com.dinstone.ireader.service;

import java.util.Date;

import com.dinstone.ireader.domain.Article;

public class SerializeService {

    public static void main(String[] args) throws Exception {
        JacksonSerializer serializer = new JacksonSerializer();
        Article data = new Article();
        data.name = "《乡长和女警在野外……被我发现了，我该怎么办啊！》 ";
        data.href = "http://www.yi-see.com/art_23290_5613.html";
        data.update = new Date();

        byte[] bytes = serializer.serialize(data);
        System.out.println("serialize  = " + new String(bytes));

        data = serializer.deserialize(bytes, Article.class);
        System.out.println("name=" + data.name + " url=" + data.href + " update=" + data.update);
    }
}
