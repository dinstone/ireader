
package com.dinstone.ireader.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dinstone.ireader.domain.Status;

public class JacksonSerializerTest {

    @Test
    public void test() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("status", Status.FINISH);
        
        System.out.println(Status.FINISH);

        JacksonSerializer js = new JacksonSerializer();

        try {
            byte[] bytes = js.serialize(data);
            System.out.println(new String(bytes));

            Map ed = js.deserialize(bytes, HashMap.class);
            System.out.println(ed);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
