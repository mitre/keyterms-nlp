/*
 * NOTICE
 * This software was produced for the U.S. Government and is subject to the
 * Rights in Data-General Clause 5.227-14 (May 2014).
 * Copyright 2018 The MITRE Corporation. All rights reserved.
 *
 * “Approved for Public Release; Distribution Unlimited” Case  18-2165
 *
 * This project contains content developed by The MITRE Corporation.
 * If this code is used in a deployment or embedded within another project,
 * it is requested that you send an email to opensource@mitre.org
 * in order to let us know where this software is being used.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package keyterms.util.system;

import java.lang.management.ManagementFactory;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;

import keyterms.util.Errors;

/**
 * Methods for interacting with operating system level constructs.
 */
public final class OS {
    /**
     * Determine if the JVM is executing on a windows operating system.
     *
     * @return A flag indicating whether the JVM is executing on a windows operating system.
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * A synchronization lock protecting the OS bean from multi-threaded access.
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * The operating system management extension bean for the local operating system.
     */
    private static OperatingSystemMXBean osBean;

    /**
     * Get the operating system management extension bean for the local operating system.
     *
     * @return The operating system management extension bean for the local operating system.
     */
    private static OperatingSystemMXBean getOsBean() {
        if (osBean == null) {
            LOCK.lock();
            try {
                if (osBean == null) {
                    try {
                        // Connecting this way vs. via an mx-bean proxy service works better on macs.
                        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                    } catch (Exception error) {
                        Errors.ignore(error);
                    }
                }
            } finally {
                LOCK.unlock();
            }
        }
        LoggerFactory.getLogger(OS.class).trace("OS bean connection: osBean={}",
                (osBean != null) ? osBean.hashCode() : null);
        return osBean;
    }

    /**
     * Get the percentage of overall CPU capacity that is in use.
     *
     * @return The percentage of overall CPU capacity that is in use.
     */
    public static Double getCpuLoad() {
        Double load = null;
        LOCK.lock();
        try {
            OperatingSystemMXBean osBean = getOsBean();
            if (osBean != null) {
                double systemLoadAverage = osBean.getSystemCpuLoad();
                load = systemLoadAverage * 100.0;
                if (Double.isNaN(load)) {
                    Errors.ignore(new IllegalStateException("NaN cpu report."));
                    load = 0.0;
                }
                if (load < 0) {
                    Errors.ignore(new IllegalStateException("Negative cpu report."));
                    load = null;
                }
            }
        } catch (Exception error) {
            Errors.ignore(error);
        } finally {
            LOCK.unlock();
        }
        return load;
    }

    /**
     * Get the amount of physical memory on the operating system.
     *
     * @return The amount of physical memory on the operating system.
     */
    public static Long getPhysicalMemory() {
        Long memory = null;
        LOCK.lock();
        try {
            OperatingSystemMXBean osBean = getOsBean();
            if (osBean != null) {
                memory = osBean.getTotalPhysicalMemorySize();
            }
        } catch (Exception error) {
            Errors.ignore(error);
        } finally {
            LOCK.unlock();
        }
        return memory;
    }

    /**
     * Get the percentage of maximum memory that is in use.
     *
     * @return The percentage of maximum memory that is in use.
     */
    public static Double getMemoryLoad() {
        Double load = null;
        LOCK.lock();
        try {
            OperatingSystemMXBean osBean = getOsBean();
            if (osBean != null) {
                double free = osBean.getFreePhysicalMemorySize();
                double total = osBean.getTotalPhysicalMemorySize();
                load = (total - free) * 100.0 / total;
                if (load < 0) {
                    Errors.ignore(new IllegalStateException("negative memory report"));
                    load = null;
                }
            }
        } catch (Exception error) {
            Errors.ignore(error);
        } finally {
            LOCK.unlock();
        }
        return load;
    }

    /**
     * Get the percentage of maximum memory that is in use in the JVM.
     *
     * @return The percentage of maximum memory that is in use in the JVM.
     */
    public static double getJavaMemoryLoad() {
        Runtime runtime = Runtime.getRuntime();
        double max = runtime.maxMemory();
        double allocated = runtime.totalMemory();
        double free = runtime.freeMemory();
        double used = allocated - free;
        return (used * 100.0 / max);
    }

    /**
     * Constructor.
     */
    private OS() {
        super();
    }
}