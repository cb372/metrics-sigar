package com.example;

import com.github.cb372.metrics.sigar.FilesystemMetrics;
import com.github.cb372.metrics.sigar.SigarMetrics;
import com.yammer.metrics.reporting.ConsoleReporter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Author: chris
 * Created: 6/9/12
 */
public class MyApp {

    public static void main(String[] args) throws InterruptedException {
        SigarMetrics sm = SigarMetrics.getInstance();

        // Print out some filesystem info
        System.out.println(String.format("%-40s %-20s %-12s %-12s", "Device", "Mount point", "Total (KB)", "Free(KB)"));
        for (FilesystemMetrics.FileSystem fs : sm.filesystems().filesystems()) {
            System.out.println(String.format("%-40s %-20s %-12s %-12s", //
                    fs.deviceName(), fs.mountPoint(), fs.totalSizeKB(), fs.freeSpaceKB()));
        }
        System.out.println("=====");

        // Register a few of the more interesting metrics as Yammer Metrics gauges
        sm.registerGauges();

        // and print them to the console every 5 seconds
        ConsoleReporter.enable(5, TimeUnit.SECONDS);

        System.out.println("Will print some metrics every 5 seconds.");
        System.out.println("Press Ctrl+C when you get bored...");
        System.out.println();
        new CountDownLatch(1).await();
    }
}
