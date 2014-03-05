package com.github.cb372.metrics.sigar;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Gauge;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class MemoryMetrics extends AbstractSigarMetric {

    protected MemoryMetrics(Sigar sigar) {
        super(sigar);
    }

    public static abstract class MemSegment {
        protected final long total;
        protected final long used;
        protected final long free;
        
        private MemSegment(long total, long used, long free) {
            this.total = total;
            this.used = used;
            this.free = free;
        }
        public long total() { return total; }
        public long used() { return used; }
        public long free() { return free; }
    }

    public static final class MainMemory extends MemSegment {
        private final long actualUsed, actualFree;
    
        private MainMemory(//
                long total, long used, long free, //
                long actualUsed, long actualFree) {
            super(total, used, free);
            this.actualUsed = actualUsed;
            this.actualFree = actualFree;
        }

        public static MainMemory fromSigarBean(Mem mem) {
            return new MainMemory( //
                    mem.getTotal(), mem.getUsed(), mem.getFree(), //
                    mem.getActualUsed(), mem.getActualFree()); 
        }

        private static MainMemory undef() {
            return new MainMemory(-1L, -1L, -1L, -1L, -1L);
        }
        
        public long actualUsed() { return actualUsed; }
        public long actualFree() { return actualFree; }
    }

    public static final class SwapSpace extends MemSegment {
        private final long pagesIn, pagesOut;

        private SwapSpace( //
                long total, long used, long free, //
                long pagesIn, long pagesOut) {
            super(total, used, free);
            this.pagesIn = pagesIn;
            this.pagesOut = pagesOut;
        }

        public static SwapSpace fromSigarBean(Swap swap) {
            return new SwapSpace( //
                    swap.getTotal(), swap.getUsed(), swap.getFree(), //
                    swap.getPageIn(), swap.getPageOut()); 
        }

        private static SwapSpace undef() {
            return new SwapSpace(-1L, -1L, -1L, -1L, -1L);
        }

        public long pagesIn() { return pagesIn; }
        public long pagesOut() { return pagesOut; }
    }

    public MainMemory mem() {
        try {
            return MainMemory.fromSigarBean(sigar.getMem());
        } catch (SigarException e) {
            return MainMemory.undef();
        }
    }

    public SwapSpace swap() {
        try {
            return SwapSpace.fromSigarBean(sigar.getSwap());
        } catch (SigarException e) {
            return SwapSpace.undef();
        }
    }

    public long ramInMB() {
        try {
            return sigar.getMem().getRam();
        } catch (SigarException e) {
            return -1L;
        }
    }

    public void registerGauges(MetricRegistry registry) {
        registerMemoryFree(registry);
        registerMemoryActualFree(registry);
        registerSwapFree(registry);
        registerSwapPagesIn(registry);
        registerSwapPagesOut(registry);
    }

    public void registerMemoryFree(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "memory-free"), new Gauge<Long>() {
            public Long getValue() {
                return mem().free();
            }
        });
    }

    public void registerMemoryActualFree(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "memory-actual-free"), new Gauge<Long>() {
            public Long getValue() {
                return mem().actualFree();
            }
        });
    }

    public void registerSwapFree(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "swap-free"), new Gauge<Long>() {
            public Long getValue() {
                return swap().free();
            }
        });
    }

    public void registerSwapPagesIn(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "swap-pages-in"), new Gauge<Long>() {
            public Long getValue() {
                return swap().pagesIn();
            }
        });
    }

    public void registerSwapPagesOut(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "swap-pages-out"), new Gauge<Long>() {
            public Long getValue() {
                return swap().pagesOut();
            }
        });
    }

}
