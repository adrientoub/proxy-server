import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adrien on 16/06/2016.
 */
public class Proxy implements Runnable {
    private Socket client;

    public Proxy(Socket client) {
        this.client = client;
    }

    public String getURLFromRequest(String requestLine) {
        String[] requestSplit = requestLine.split(" ");
        if (requestSplit.length != 3)
            return null;
        String[] splitedURL = requestSplit[1].split("\\?");
        if (splitedURL.length != 2)
            return null;
        String parameterURL = splitedURL[1];
        String[] parameters = parameterURL.split("&");
        Map<String, String> urlParams = new HashMap<>();
        for (String param: parameters) {
            String[] keyval = param.split("=");
            if (keyval.length == 2) {
                try {
                    urlParams.put(keyval[0], URLDecoder.decode(keyval[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return urlParams.get("url");
    }

    @Override
    public void run() {
        try {
            InputStream input = client.getInputStream();
            byte[] header = new byte[1024];
            int read;
            StringBuilder sb = new StringBuilder();
            if ((read = input.read(header, 0, 1024)) > 0) {
                sb.append(new String(header, 0, read));
            }
            String requestLine = sb.toString().split("\r\n")[0];
            System.out.println(requestLine);
            String url = getURLFromRequest(requestLine);
            System.out.println(url);
            OutputStream output = client.getOutputStream();

            String content;
            if (url == null)
                content = "HTTP/1.1 403 Forbidden\r\n\r\n";
            else
                content = Client.getPage(url);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
