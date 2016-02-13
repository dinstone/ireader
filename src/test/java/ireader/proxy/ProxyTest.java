
package ireader.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ProxyTest {

    private static HttpProxy hp;

    public static void main(String[] args) {
        action();

        // init();
    }

    private static void init() {
        String inFile = "proxy.txt";
        String outFile = "valid.txt";
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                try {
                    // do something
                    String trim = temp.trim();
                    System.out.println("connect " + trim);
                    String[] parts = trim.split(":");
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), 2000);
                    s.close();
                    writer.write(trim);
                    writer.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void action() {
        hp = new HttpProxy();

        int tryCount = 0;
        while (true) {
            try {
                IpProxy ipProxy = hp.config();
                System.out.println(ipProxy);

                String html = getHtml("http://www.yi-see.com/");
                System.out.println(html);

                break;
            } catch (ConnectException e) {
                if (tryCount > 5) {
                    e.printStackTrace();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static String getHtml(String address) throws Exception, RuntimeException {
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
        // System.out.println(result);
        return result;
    }
}
