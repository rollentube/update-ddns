import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Base64;

public class DnsHandler {
    private final String dnsName, token;
    private final String ipv4Pattern;
    private final String[] wanURIs;

    public DnsHandler(String dnsName, String token) {
        this.dnsName = dnsName;
        this.token = token;
        this.ipv4Pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
        this.wanURIs = new String[]{"http://ifconfig.me/ip", "http://ipecho.net/plain"};
    }

    // web request to public wan ip checkers
    public String getWanIP() throws IPNotFoundException {
        String wanIP;
        for (String wanURI : wanURIs) {
            try {
                URL url = new URL(wanURI);
                URLConnection request = url.openConnection();
                BufferedReader response = new BufferedReader(new InputStreamReader(request.getInputStream()));
                wanIP = response.readLine();
                response.close();

                if (wanIP.matches(ipv4Pattern))
                    return wanIP;
                else
                    throw new IPv4FormatException("Unvalid IPv4 returned from " + wanURI);
            } catch (IOException | IPv4FormatException e) {
                System.out.println(e.getMessage() + "; trying next URI");
            }
        }
        throw new IPNotFoundException("Unable to find WAN IP");
    }

    // resolve hostname and regex check for IPv4
    public String getDnsIP() throws UnknownHostException, IPv4FormatException {
        String dnsIP;
        dnsIP = InetAddress.getByName(dnsName).getHostAddress();

        if (dnsIP.matches(ipv4Pattern))
            return dnsIP;
        else
            throw new IPv4FormatException("Unvalid IPv4 returned returned from " + dnsName);
    }

    // update dyndns over web api
    public void updateDynDns(String ip) throws IOException {
        // Web API from spdyn.de
        String dynURI = "https://update.spdyn.de/nic/update?hostname=" + dnsName + "&myip=" + ip;
        String userpass = dnsName + ":" + token;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        URL url = new URL(dynURI);
        URLConnection request = url.openConnection();
        request.setRequestProperty ("Authorization", basicAuth);
        InputStream response = request.getInputStream();
        response.close();
    }

    public boolean compareIP(String ip0, String ip1) {
        return ip0.equals(ip1);
    }
}
