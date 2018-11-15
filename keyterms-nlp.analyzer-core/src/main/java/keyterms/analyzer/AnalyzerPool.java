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

package keyterms.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import keyterms.util.system.Threads;
import keyterms.util.text.Strings;

/**
 * A thread pool for running a single type of analyzer in a thread safe way.
 */
public class AnalyzerPool
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 3649353732430837738L;

    /**
     * The thread group for the pool's execution threads.
     */
    private final ThreadGroup threadGroup;

    /**
     * The internal thread pool executor service.
     */
    private final ThreadPoolExecutor threadPool;

    /**
     * A synchronization lock for the analyzer instance collection.
     */
    private final ReentrantReadWriteLock instanceLock = new ReentrantReadWriteLock();

    /**
     * The analyzer instances which are available.
     */
    private final List<Analyzer> instances = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param factory The factory used to create new instances of the underlying analyzer.
     */
    public AnalyzerPool(AnalyzerFactory factory) {
        super(factory.getInputClasses(), factory.getOutputFeatures(),
                factory.producesRankings(), factory.producesScores());
        int numThreads = Runtime.getRuntime().availableProcessors();
        for (int t = 0; t < numThreads; t++) {
            instances.add(factory.newInstance());
        }
        Analyzer baseAnalyzer = instances.get(0);
        String groupName = Strings.trim(factory.getId().toString());
        if (Strings.isBlank(groupName)) {
            groupName = baseAnalyzer.getClass().getSimpleName() + "@" + baseAnalyzer.hashCode();
        }
        threadGroup = new ThreadGroup(groupName);
        threadPool = new ThreadPoolExecutor(numThreads, numThreads, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new PoolThreads());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        if (accepts(input.getClass())) {
            Future<?> future = threadPool.submit(new PoolWorker(input, collector));
            while ((!future.isDone()) && (!future.isCancelled())) {
                Thread.yield();
            }
        }
    }

    /**
     * Get the number of available analyzer instances.
     *
     * @return The number of available analyzer instances.
     */
    private int available() {
        instanceLock.readLock().lock();
        try {
            return instances.size();
        } finally {
            instanceLock.readLock().unlock();
        }
    }

    /**
     * Allocate an analyzer instance for use by a worker.
     *
     * @return The allocated analyzer instance.
     */
    protected Analyzer allocate() {
        Analyzer analyzer = null;
        while (analyzer == null) {
            if (available() > 0) {
                instanceLock.writeLock().lock();
                try {
                    if (available() > 0) {
                        analyzer = instances.remove(0);
                    }
                } finally {
                    instanceLock.writeLock().unlock();
                }
            }
        }
        return analyzer;
    }

    /**
     * Release the specified analyzer instance to the pool.
     *
     * @param analyzer The analyzer instance.
     */
    protected void release(Analyzer analyzer) {
        instanceLock.writeLock().lock();
        try {
            instances.add(analyzer);
        } finally {
            instanceLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        threadPool.shutdown();
        while (available() < threadPool.getMaximumPoolSize()) {
            Threads.sleep(10);
        }
        instanceLock.writeLock().lock();
        try {
            instances.forEach(Analyzer::dispose);
        } finally {
            instanceLock.writeLock().unlock();
        }
    }

    /**
     * The analyzer pool's thread factory.
     */
    private class PoolThreads
            implements ThreadFactory {
        /**
         * The next worker number.
         */
        private int workerNumber = 0;

        /**
         * Constructor.
         */
        private PoolThreads() {
            super();
        }

        /**
         * Get the next worker number.
         *
         * @return The next worker number.
         */
        private synchronized int nextWorkerNumber() {
            return ++workerNumber;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Thread newThread(Runnable runnable) {
            String threadName = "Worker-" + nextWorkerNumber();
            Thread thread = new Thread(threadGroup, runnable, threadName);
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((t, e) ->
                    getLogger().error("Uncaught exception on Thread[{}, {}]", threadGroup, threadName, e));
            return thread;
        }
    }

    /**
     * A pool worker.
     */
    private class PoolWorker
            implements Runnable {
        /**
         * The input data to analyze.
         */
        private final Object input;

        /**
         * The collector of analysis results.
         */
        private final Consumer<Analysis> collector;

        /**
         * Constructor.
         *
         * @param input The input data to analyze.
         * @param collector The collector of analysis results.
         */
        private PoolWorker(Object input, Consumer<Analysis> collector) {
            super();
            this.input = input;
            this.collector = collector;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            Analyzer analyzer = allocate();
            try {
                analyzer._analyze(input, collector);
            } catch (Exception error) {
                getLogger().error("Error analyzing data: {}", input, error);
            } finally {
                release(analyzer);
            }
        }
    }
}