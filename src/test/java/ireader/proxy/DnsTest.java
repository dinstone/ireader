
package ireader.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.alibaba.dcm.DnsCacheManipulator;

public class DnsTest {

    public static void main(String[] args) throws UnknownHostException {
        DnsCacheManipulator.loadDnsCacheConfig();
        System.out.println("www.yi-see.com = " + InetAddress.getByName("www.yi-see.com").getHostAddress());

        DnsCacheManipulator.setDnsCache("www.hello.com", "192.168.1.1");
        // 支持IPv6地址
        DnsCacheManipulator.setDnsCache("www.world.com", "1234:5678:0:0:0:0:0:200e");

        // 上面设置全局生效，之后Java中的所有的域名解析逻辑都会是上面设定的IP。
        // 下面用一个简单获取域名对应的IP，来演示一下：

        String ip = InetAddress.getByName("www.hello.com").getHostAddress();
        // ip = "192.168.1.1"
        System.out.println("ip = " + ip);
        String ipv6 = InetAddress.getByName("www.world.com").getHostAddress();
        // ipv6 = "1234:5678:0:0:0:0:0:200e"
        System.out.println("ipv6 = " + ipv6);
        // 可以设置多个IP
        DnsCacheManipulator.setDnsCache("www.hello-world.com", "192.168.2.1", "192.168.2.2");

        String ipHw = InetAddress.getByName("www.hello-world.com").getHostAddress();
        // ipHw = 192.168.2.1 ，读到第一个IP
        System.out.println("iphw = " + ipHw);
        InetAddress[] allIps = InetAddress.getAllByName("www.hello-world.com");
        // 上面读到设置的多个IP
        System.out.println("iphws = " + allIps);

        // 设置失效时间，单元毫秒
        DnsCacheManipulator.setDnsCache(3600 * 1000, "www.hello-hell.com", "192.168.1.1", "192.168.1.2");
    }

}
