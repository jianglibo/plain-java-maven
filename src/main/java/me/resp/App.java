package me.resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 * ssh root@104.238.149.23 ls -l ~
 */
@Slf4j
public class App {

  public static void printout(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    List<String> commands = new ArrayList<>();

    if ("cmd".equals(args[0])) {
      commands.add("cmd.exe");
      commands.add("/c");
      commands.add(args[1]);
    } else if ("sh".equals(args[0])) {
      commands.add("sh");
      commands.add("-c");
      commands.add(args[1]);
    } else if ("bash".equals(args[0])) {
      commands.add("bash");
      commands.add("-c");
      commands.add(args[1]);
    } else {
      for (Entry<String, String> entry : builder.environment().entrySet()) {
        log.info(entry.getKey() + "=" + entry.getValue());
      }

      commands.add("ansible-playbook");
      commands.add("--vault-id");
      commands.add("hetzner@/opt/respme/hetzner.pwd");

      commands.add("--extra-vars");
      String extraVars = "{\"vpn_server_ip\": \"" + args[0]
          + "\", \"max_conf\": 250, \"provider\": \"vultr\", \"continent\": \"ASIA\"}";
      printout(extraVars);
      commands.add(extraVars);

      // Add play book name at last.
      commands.add("install-algo-to.yml");

      commands.add("-vvv");
    }
    builder.command(commands);
    builder.redirectErrorStream(true);
    // builder.directory(new File(getWorkingDirectory()));

    Process process = builder.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

    String line = null;
    while ((line = reader.readLine()) != null) {
      log.info("line: {}", line);
    }

    // StreamGobbler streamGobbler =
    // new StreamGobbler(process.getInputStream(), line -> {
    // if (line == null) {
    // return;
    // }
    // log.info("line: {}", line);
    // });
    // Executors.newSingleThreadExecutor().submit(streamGobbler);
    int exit = process.waitFor();
    printout(exit);
  }
}
