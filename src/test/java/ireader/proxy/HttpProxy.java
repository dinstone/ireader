
package ireader.proxy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpProxy {

    private List<IpProxy> proxyList = new ArrayList<IpProxy>();

    public HttpProxy() {
        init();
    }

    public void init() {
        String inFile = "valid.txt";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                // do something
                String trim = temp.trim();
                String[] parts = trim.split(":");
                IpProxy ip = new IpProxy(trim, parts[0], parts[1], "");
                proxyList.add(ip);
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public IpProxy config() {
        IpProxy proxy = proxyList.get(randomIndex(0, proxyList.size() - 1));

        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", proxy.getIp());
        System.getProperties().setProperty("http.proxyPort", proxy.getPort());

        return proxy;
    }

    public static int randomIndex(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }
}
