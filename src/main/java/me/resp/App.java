package me.resp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class App
{
    public static void printout(Object o) {
        System.out.println(o);
    }
    public static void main( String[] args ) throws IOException, InterruptedException {    ProcessBuilder builder = new ProcessBuilder();
        List<String> commands = new ArrayList<>();

        for(Entry<String, String> entry: builder.environment().entrySet()) {
            log.info(entry.getKey() + "=" + entry.getValue());
        }

        commands.add("ansible-playbook");
        commands.add("--vault-id");
        commands.add("hetzner@/opt/respme/hetzner.pwd");


        commands.add("--extra-vars");
        String extraVars = "{\"vpn_server_ip\": \"" + args[0] + "\", \"max_conf\": 250, \"provider\": \"vultr\", \"continent\": \"ASIA\"}";
        printout(extraVars);
        commands.add(extraVars);

        // Add play book name at last.
        commands.add("install-algo-to.yml");

        builder.command(commands);
        builder.redirectErrorStream(true);
//        builder.directory(new File(getWorkingDirectory()));
        Process process = builder.start();
        StreamGobbler streamGobbler =
            new StreamGobbler(process.getInputStream(), line -> {
                printout(line);
            });
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exit =  process.waitFor();
        printout( exit );
    }
}
