package com.github.cb372.metrics.sigar;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Gauge;

import org.hyperic.sigar.ResourceLimit;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class UlimitMetrics extends AbstractSigarMetric {
    private final long infinity;

    protected UlimitMetrics(Sigar sigar) {
        super(sigar);
        infinity = ResourceLimit.INFINITY();
    }

    public static final class Ulimit {
        private final long coreFileSize, dataSegSize,
                fileSize, pipeSize, 
                memSize, openFiles,
                stackSize, cpuTime,
                processes, virtMem;
    
        private Ulimit(//
                long coreFileSize, long dataSegSize, //
                long fileSize, long pipeSize, //
                long memSize, long openFiles, //
                long stackSize, long cpuTime, //
                long processes, long virtMem) {
            this.coreFileSize = coreFileSize;
            this.dataSegSize = dataSegSize;
            this.fileSize = fileSize;
            this.pipeSize = pipeSize;
            this.memSize = memSize;
            this.openFiles = openFiles;
            this.stackSize = stackSize;
            this.cpuTime = cpuTime;
            this.processes = processes;
            this.virtMem = virtMem;
        }

        public static Ulimit fromSigarBean(ResourceLimit lim, long infinity) {
            return new Ulimit( //
                    replaceInfinity(lim.getCoreCur(), infinity), //
                    replaceInfinity(lim.getDataCur(), infinity), //
                    replaceInfinity(lim.getFileSizeCur(), infinity), //
                    replaceInfinity(lim.getPipeSizeCur(), infinity), //
                    replaceInfinity(lim.getMemoryCur(), infinity), //
                    replaceInfinity(lim.getOpenFilesCur(), infinity), //
                    replaceInfinity(lim.getStackCur(), infinity), //
                    replaceInfinity(lim.getCpuCur(), infinity), //
                    replaceInfinity(lim.getProcessesCur(), infinity), //
                    replaceInfinity(lim.getVirtualMemoryCur(), infinity)); 
        }

        public static Ulimit undef() {
            return new Ulimit(-1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L);
        }

        public long coreFileSize() { return coreFileSize; }
        public long dataSegSize() { return dataSegSize; }
        public long fileSize() { return fileSize; }
        public long pipeSize() { return pipeSize; }
        public long memSize() { return memSize; }
        public long openFiles() { return openFiles; }
        public long stackSize() { return stackSize; }
        public long cpuTime() { return cpuTime; }
        public long processes() { return processes; }
        public long virtMemSize() { return virtMem; }

        private static long replaceInfinity(long value, long infinity) {
          if (value == infinity) {
            return -1L;
          } else {
            return value;
          }
        }
    }

    public Ulimit ulimit() {
        try {
            return Ulimit.fromSigarBean(sigar.getResourceLimit(), infinity);
        } catch (SigarException e) {
            return Ulimit.undef();
        }
    }

    public void registerGauges(MetricRegistry registry) {
        registerUlimitOpenFiles(registry);
        registerUlimitStackSize(registry);
    }

    public void registerUlimitOpenFiles(MetricRegistry registry) {
        registerUlimitOpenFiles(registry, MetricRegistry.name(getClass(), "ulimit-open-files"));
    }

    public void registerUlimitOpenFiles(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            public Long getValue() {
                return ulimit().openFiles();
            }
        });
    }

    public void registerUlimitStackSize(MetricRegistry registry) {
        registerUlimitStackSize(registry, MetricRegistry.name(getClass(), "ulimit-stack-size"));
    }

    public void registerUlimitStackSize(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            public Long getValue() {
                return ulimit().stackSize();
            }
        });
    }

}
