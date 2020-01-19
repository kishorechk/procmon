package com.integwise.procmon;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileWatcher extends TimerTask {

    private long timeStamp;
    private File file;
    private List<String> entries;
    private static final Logger logger = LoggerFactory.getLogger(ProcessMonitor.class);

    public FileWatcher(File file) {
        try {
            this.file = file;
            this.timeStamp = file.lastModified();
            this.entries = FileUtils.getFileContents(file);
        } catch (FileNotFoundException e) {
            logger.error("{}", e);
        }
    }

    public void run() {
        try {
            long timeStamp = file.lastModified();
            if (this.timeStamp != timeStamp) {
                this.timeStamp = timeStamp;
                List<String> entries = FileUtils.getFileContents(file);
                List<String> newEntries = entries.stream().filter(entry -> !this.entries.contains(entry))
                        .collect(Collectors.toList());
                List<String> missingEntries = this.entries.stream().filter(entry -> !entries.contains(entry))
                        .collect(Collectors.toList());
                this.entries = entries;
                onChange(file, newEntries, missingEntries);
            }
        } catch (FileNotFoundException e) {
            logger.error("{}", e);
        }
    }

    protected abstract void onChange(File file, List<String> newEntries, List<String> missingEntries);
}