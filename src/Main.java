//TODO; Add verbose parameter for bash command
//TODO; Execute java application over bash script
//TODO; Nicer solution for --help call (--help currently over exception)
//TODO; Clean up Main method, maybe create more other methods

import java.io.*;
import java.net.*;
import java.util.Base64;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("v", "verbose", false, "Show verbose information [NOT IMPLEMENTED]");
        options.addOption("h", "help", false, "Print this help text");
        options.addOption(Option.builder()
                .longOpt("dns-name")
                .desc("Dynamic dns entry to change")
                .required()
                .hasArg()
                .argName("DNS-ENTRY")
                .build());
        options.addOption(Option.builder()
                .longOpt("token")
                .desc("API token to change the entry")
                .required()
                .hasArg()
                .argName("TOKEN")
                .build());

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingOptionException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("update-ddns", options, true);
            System.exit(1);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            formatter.printHelp("update-ddns", options, true);
            System.exit(0);
        }

        String dnsName = cmd.getOptionValue("dns-name");
        String token = cmd.getOptionValue("token");
        String ipv4Pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
        String[] wanURIs = {"http://ifconfig.me/ip", "http://ipecho.net/plain"};
        String currentIP = null;

        try {
            currentIP = Main.getWanIP(wanURIs, ipv4Pattern);
        } catch (IPNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        String lastIP = Main.getDnsIP(dnsName, ipv4Pattern);

        if (compareIP(currentIP, lastIP)) {
            System.out.println("IP is up to date: " + currentIP);
        } else {
            Main.updateDynDns(currentIP, dnsName, token);
        }
    }

    private static String getWanIP(String[] wanURIs, String ipv4Pattern) throws IPNotFoundException {
        String wanIP;
        for (String wanURI:wanURIs) {
            try {
                URL url = new URL(wanURI);
                URLConnection request = url.openConnection();
                BufferedReader response = new BufferedReader(new InputStreamReader(request.getInputStream()));
                wanIP = response.readLine();
                response.close();

                if (wanIP.matches(ipv4Pattern)) {
                    return wanIP;
                } else {
                    throw new IPv4FormatException("Unvalid IPv4 returned from " + wanURI);
                }
            } catch (IOException | IPv4FormatException e) {
                System.out.println(e.getMessage() + "; trying next URI");
            }
        }
        throw new IPNotFoundException("Unable to find WAN IP");
    }

    private static String getDnsIP(String dnsName, String ipv4Pattern) {
        String dnsIP;
        try {
            dnsIP = InetAddress.getByName(dnsName).getHostAddress();

            if (dnsIP.matches(ipv4Pattern)) {
                return dnsIP;
            } else {
                throw new IPv4FormatException("Unvalid IPv4 returned returned from " + dnsName);
            }
        } catch (UnknownHostException | IPv4FormatException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return null;
        }
    }

    private static boolean compareIP(String ip0, String ip1) {
        return ip0.equals(ip1);
    }

    private static void updateDynDns(String ip, String dnsName, String token) throws IOException {
        // web API from spdyn.de
        String dynURI = "https://update.spdyn.de/nic/update?hostname=" + dnsName + "&myip=" + ip;
        String userpass = dnsName + ":" + token;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        URL url = new URL(dynURI);
        URLConnection request = url.openConnection();
        request.setRequestProperty ("Authorization", basicAuth);
        InputStream response = request.getInputStream();
        response.close();
    }
}
