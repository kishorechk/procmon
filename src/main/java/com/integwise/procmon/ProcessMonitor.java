package com.integwise.procmon;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProcessMonitor extends TimerTask {

    private Set<String> processNames;
    private static final Logger logger = LoggerFactory.getLogger(ProcessMonitor.class);

    public ProcessMonitor(Set<String> processNames) {
        this.processNames = processNames;
    }

    public void setProcessNames(Set<String> processNames) {
        this.processNames = processNames;
    }

    public void run() {
        try {
            List<String> processes = new ArrayList<>();
            String line;
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c",
                    "ps -efa | grep child |grep -v 'grep ' | awk '{print $NF}'");
            Process process = pb.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                processes.add(line.substring(line.lastIndexOf("/") + 1));
            }

            List<String> downProcesses = this.processNames.stream().filter(entry -> !processes.contains(entry))
                    .collect(Collectors.toList());
            onDown(downProcesses);
            input.close();
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

    protected abstract void onDown(List<String> processNames);

}