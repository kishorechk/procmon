package com.integwise.procmon;

import java.util.*;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessOrchestrator {

    private Map<String, String> processVersionMap = new HashMap<>();
    private Map<String, Process> processMap = new HashMap<>();
    private File file;
    private static final Logger logger = LoggerFactory.getLogger(ProcessOrchestrator.class);

    public ProcessOrchestrator(String inputFileName) {
        try {
            this.file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + inputFileName);

            for (String processName : FileUtils.getFileContents(file)) {
                startProcess(processName);
            }
        } catch (FileNotFoundException e) {
            logger.error("{}", e);
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

    public void start() {
        TimerTask processMonitor = new ProcessMonitor(processMap.keySet()) {
            protected void onDown(List<String> processes) {
                for (String processName : processes) {
                    logger.info(processName + " is down!");
                    try {
                        /*
                         * if (processVersionMap.get(processName.substring(0,
                         * processName.lastIndexOf("-"))) != null && processMap.get(processName) !=
                         * null) { return; } else {
                         */
                        startProcess(processName);
                        // }
                    } catch (IOException e) {
                        logger.error("{}", e);
                    }
                }
            }
        };

        Timer processMonitorTimer = new Timer();
        processMonitorTimer.schedule(processMonitor, new Date(), 1000);

        TimerTask fileWatcher = new FileWatcher(file) {
            protected void onChange(File file, List<String> newEntries, List<String> missingEntries) {
                logger.info("File " + file.getName() + " have changed!");
                try {
                    for (String processName : missingEntries) {
                        logger.info("Process entry missing in input file.");
                        killProcess(processName);
                    }
                    for (String processName : newEntries) {
                        logger.info("Process already exists, kill old version and start new version.");
                        if (processVersionMap.keySet().stream().anyMatch(
                                entry -> entry.contains(processName.substring(0, processName.lastIndexOf("-"))))) {
                            killProcess(processName.substring(0, processName.lastIndexOf("-")) + "-"
                                    + processVersionMap.get(processName.substring(0, processName.lastIndexOf("-"))));
                            startProcess(processName);
                        } else {
                            logger.info("Process doesn't exist and start initial version.");
                            startProcess(processName);
                        }
                    }
                    ((ProcessMonitor) processMonitor).setProcessNames(processMap.keySet());

                } catch (IOException e) {
                    logger.error("{}", e);
                }
            }
        };

        Timer fileWatcherTimer = new Timer();
        fileWatcherTimer.schedule(fileWatcher, new Date(), 1000);

    }

    private void startProcess(String name) throws IOException {
        new Thread(new Runnable() {

            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            System.getProperty("user.dir") + System.getProperty("file.separator") + name);
                    logger.info("Starting process: " + name);
                    Process process = pb.start();
                    processMap.put(name, process);
                    processVersionMap.put(name.substring(0, name.lastIndexOf("-")),
                            name.substring(name.lastIndexOf("-") + 1));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info(line);
                    }

                    int exitCode = process.waitFor();
                    logger.info("\nProcess exited with error code : " + exitCode);
                    logger.info("Started process: " + name);
                } catch (IOException e) {
                    logger.error("{}", e);
                } catch (InterruptedException e) {
                    logger.error("{}", e);
                }
            }
        }).start();
    }

    private void killProcess(String name) {
        Process process = processMap.get(name);
        System.out.println("Killing process: " + name);
        process.destroyForcibly();
        processMap.remove(name);
        processVersionMap.remove(name.substring(0, name.lastIndexOf("-")));
        System.out.println("Killed process: " + name);
    }
}