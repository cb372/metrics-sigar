package com.github.cb372.metrics.sigar;

import com.yammer.metrics.core.MetricsRegistry;

interface CanRegisterGauges {

    /**
     * Register zero or more Gauges in the given registry.
     */
    public void registerGauges(MetricsRegistry registry);

}


