package com.github.cb372.metrics.sigar;

import com.github.cb372.metrics.sigar.UlimitMetrics.Ulimit;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class UlimitMetricsTest extends CheckSigarLoadsOk {

    @Test
    public void openFilesLimitIsGreaterThanZero() throws Exception {
        // skip this test on Windows platforms
        assumeThat(System.getProperty("os.name").toLowerCase(), not(containsString("windows")));

        assertThat(SigarMetrics.getInstance().ulimit().ulimit().openFiles(), is(greaterThan(0L)));
    }

}
