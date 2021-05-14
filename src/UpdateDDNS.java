//TODO; Add verbose parameter for shell command
//TODO; Execute java application over bash script

import java.io.*;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.cli.*;

public class UpdateDDNS {
    public static void main(String[] args) throws IOException {
        //region Commons CLI
        Options options = new Options();
        options.addOption("v", "verbose", false, "Show verbose information [NOT IMPLEMENTED YET]");
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

        if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("--help")) {
            formatter.printHelp("update-ddns", options, true);
            System.exit(0);
        }

        try {
            cmd = parser.parse(options, args);
        } catch (MissingOptionException e) {
            System.out.println(e.getMessage());
            System.out.println("Try 'update-ddns --help' for more information.");
            System.exit(1);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        //endregion

        DnsHandler dnsHandler = new DnsHandler(cmd.getOptionValue("dns-name"), cmd.getOptionValue("token"));
        try {
            //get current wan ip
            String currentIP = dnsHandler.getWanIP();
            //get current ip of the dyn dns
            String lastIP = dnsHandler.getDnsIP();

            //update dyn dns entry if not up to date
            if (!dnsHandler.compareIP(currentIP, lastIP))
                dnsHandler.updateDynDns(currentIP);
            else
                System.out.println("IP is up to date: " + currentIP);
        } catch (IPNotFoundException | UnknownHostException | IPv4FormatException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
