import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by adrien on 16/06/2016.
 */
public class Client {
    private static Jedis jedis = new Jedis("localhost");
    private final static String prefix = "proxy-";

    public static String getPage(String domain) {
        String page = jedis.get(prefix + domain);
        if (page != null) {
            System.out.println("Cache hit for " + domain);
            return page;
        }
        page = getPageFromURL(domain);
        jedis.set(prefix + domain, page);

        return page;
    }

    private static String getPageFromURL(String domain) {
        try {
            URL url = new URL(domain);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            int code = httpURLConnection.getResponseCode();
            System.out.println("Response Code: " + httpURLConnection.getResponseCode());
            InputStream is;
            if (code < 200 || code > 399)
                is = httpURLConnection.getErrorStream();
            else
                is = httpURLConnection.getInputStream();

            String content = getPageContent(is);

            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 ").append(httpURLConnection.getResponseCode()).append(" ")
              .append(httpURLConnection.getResponseMessage()).append("\r\n");

            Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                if (entry.getKey() == null || entry.getKey().equals("Transfer-Encoding"))
                    continue;
                sb.append(entry.getKey()).append(": ")
                  .append(String.join(";", entry.getValue())).append("\r\n");
            }
            sb.append("\r\n");

            sb.append(content);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getPageContent(InputStream is) throws IOException {
        if (is == null)
            return "";
        StringBuilder content = new StringBuilder();

        byte[] c = new byte[1024];
        int read;

        while ((read = is.read(c, 0, 1024)) > 0) {
            content.append(new String(c, 0, read));
        }
        return content.toString();
    }
}
