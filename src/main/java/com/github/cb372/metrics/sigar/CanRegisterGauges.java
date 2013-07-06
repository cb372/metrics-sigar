package com.github.cb372.metrics.sigar;

import com.codahale.metrics.MetricRegistry;

interface CanRegisterGauges {

    /**
     * Register zero or more Gauges in the given registry.
     */
    public void registerGauges(MetricRegistry registry);

}


