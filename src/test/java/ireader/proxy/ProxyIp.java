
package ireader.proxy;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

public class ProxyIp {

    // private static final Logger log = Logger.getLogger(ProxyIp.class);
    // private static final String Continu = null;

    // 为了突破IP限制需要动态替换代理ip。

    public static void setProxy() {
        // String str="";

        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");

        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
        IpProxy p1 = new IpProxy("1", "41.231.53.41", "3128", "突尼斯");
        IpProxy p2 = new IpProxy("2", "114.112.91.135", "3128", "北京市");
        IpProxy p3 = new IpProxy("3", "111.161.126.83", "8080", "天津市 联通");
        IpProxy p4 = new IpProxy("4", "111.161.126.84", "80", "天津市 联通");
        IpProxy p5 = new IpProxy("5", "111.161.126.89", "8080", "天津市 联通");
        IpProxy p6 = new IpProxy("6", "111.161.126.85", "80", "天津市 ");
        IpProxy p7 = new IpProxy("7", "111.161.126.92", "8080", "突尼斯");
        IpProxy p8 = new IpProxy("8", "183.224.1.30", "80", "昆明");
        IpProxy p9 = new IpProxy("9", "111.161.126.88", "8080", "天津");
        IpProxy p10 = new IpProxy("10", "14.18.16.67", "80", "广州");
        IpProxy p11 = new IpProxy("11", "222.246.232.55", "80", "湖南");
        IpProxy p12 = new IpProxy("12", "220.181.32.106", "80", "北京");
        IpProxy p13 = new IpProxy("13", "202.108.23.247", "80", "北京");

        IpProxy p14 = new IpProxy("14", "106.3.40.249", "8081", "北京");
        IpProxy p15 = new IpProxy("15", "58.56.124.192", "80", "济南");
        IpProxy p16 = new IpProxy("16", "223.202.3.49", "8080", "北京");
        IpProxy p17 = new IpProxy("17", "218.4.236.117", "80", "江苏");
        IpProxy p18 = new IpProxy("18", "120.210.202.4", "80", "安徽");
        IpProxy p19 = new IpProxy("19", "121.10.252.139", "3128", "广东省肇庆市");
        IpProxy p20 = new IpProxy("20", "60.250.81.118", "8080", "台湾");
        IpProxy p21 = new IpProxy("21", "113.57.252.107", "80", "武汉");
        IpProxy p22 = new IpProxy("22", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        IpProxy p23 = new IpProxy("23", "115.29.247.115", "8888", "北京市 万网IDC机房");
        IpProxy p24 = new IpProxy("24", "202.106.169.228", "8080", "北京");
        IpProxy p25 = new IpProxy("25", "122.96.59.106", "81", "南京");
        IpProxy p26 = new IpProxy("26", "182.92.77.169", "3128", "浙江省杭州市 阿里巴巴网络有限公司");
        IpProxy p27 = new IpProxy("27", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        IpProxy p28 = new IpProxy("28", "122.96.59.106", "81", "南京");
        IpProxy p29 = new IpProxy("29", "117.21.192.9", "80", "江西省 电信");
        IpProxy p30 = new IpProxy("30", "113.57.230.49", "81", "湖北省武汉市 联通");
        IpProxy p31 = new IpProxy("31", "223.68.6.10", "8000", "江苏省宿迁市 移动");
        IpProxy p32 = new IpProxy("32", "115.28.23.36", "3128", "北京");
        IpProxy p33 = new IpProxy("33", "122.96.59.106", "81", "江苏省南京市 联通");
        IpProxy p34 = new IpProxy("34", "202.108.23.247", "80", "北京");
        IpProxy p35 = new IpProxy("35", "124.207.175.91", "8080", "北京");
        IpProxy p36 = new IpProxy("36", "120.192.200.72", "80", "西安");
        IpProxy p37 = new IpProxy("37", "120.237.91.242", "3128", "北京");
        IpProxy p38 = new IpProxy("38", "125.39.66.76", "80", "北京");
        List<IpProxy> list = new ArrayList();

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p7);
        list.add(p8);
        list.add(p9);
        list.add(p10);
        list.add(p11);
        list.add(p11);
        list.add(p12);
        list.add(p13);
        list.add(p14);
        list.add(p15);
        list.add(p16);
        list.add(p17);
        list.add(p18);
        list.add(p19);
        list.add(p20);
        list.add(p21);
        list.add(p22);
        list.add(p23);
        list.add(p24);
        list.add(p25);
        list.add(p26);
        list.add(p27);
        list.add(p28);
        list.add(p29);
        list.add(p30);
        list.add(p31);
        list.add(p32);
        list.add(p33);
        list.add(p34);
        list.add(p35);
        list.add(p36);
        list.add(p37);
        list.add(p38);
        int i = MathRodom.toRodom(38, 1);
        System.getProperties().setProperty("http.proxyHost", list.get(i).getIp());
        System.getProperties().setProperty("http.proxyPort", list.get(i).getPort());
        System.out.println("代理服务器IP::" + list.get(i).getIp() + "端口：：" + list.get(i).getPort());
        // 确定代理是否设置成功

    }

    public static Document getHtml(String url1) throws Exception {
        // TODO Auto-generated method stub
        Document doc = Jsoup.connect(url1).get();
        return doc;
    }

    private static Document getHtmlStr(String address) throws Exception, RuntimeException {
        StringBuffer html = new StringBuffer();
        String result = null;

        URL url = new URL(address);

        URLConnection conn = (URLConnection) url.openConnection();
        conn.setConnectTimeout(1000 * 40);
        conn.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

        String inputLine;
        byte[] buf = new byte[4096];
        int bytesRead = 0;
        while (bytesRead >= 0) {
            inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");
            html.append(inputLine);
            bytesRead = in.read(buf);
            inputLine = null;
        }
        result = new String(html.toString().trim().getBytes("ISO-8859-1"), "utf-8").toLowerCase();
        buf = null;
        Document doc = Jsoup.parse(result, "", new Parser(new XmlTreeBuilder()));
        // System.out.println(result);
        return doc;
    }

    public static void main(String[] args) throws Exception {
        ProxyIp.setProxy();
        Document htmlStr = ProxyIp.getHtmlStr("http://www.yi-see.com/");
        System.out.println("===" + htmlStr);
        System.out.println("==============");
    }
}
