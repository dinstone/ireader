
package ireader;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launch {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        System.in.read();

        context.close();
    }

}
