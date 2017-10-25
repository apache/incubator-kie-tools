/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.rest.backend;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.rest.client.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JobResultManager {

    private static final Logger logger = LoggerFactory.getLogger(JobResultManager.class);
    private static AtomicInteger created = new AtomicInteger(0);

    private static class Cache extends LinkedHashMap<String, JobResult> {

        private int maxSize = 1000;

        public Cache(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, JobResult> stringFutureEntry) {
            return size() > maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }

    private Map<String, JobResult> jobs = null;

    private int maxCacheSize = 10000;

    @PostConstruct
    public void start() {
        if (!created.compareAndSet(0,
                                   1)) {
            throw new IllegalStateException("Only 1 JobResultManager instance is allowed per container!");
        }
        Cache cache = new Cache(maxCacheSize);
        jobs = Collections.synchronizedMap(cache);
    }

    public JobResult getJob(String jobId) {
        return jobs.get(jobId);
    }

    public void putJob(JobResult job) {
        jobs.put(job.getJobId(),
                 job);
    }

    public JobResult removeJob(String jobId) {
        return jobs.remove(jobId);
    }
}