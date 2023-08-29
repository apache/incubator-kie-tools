/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.scheduler;

public class SchedulerTaskFactory {

    public static SchedulerTask newTask(String key, String summary, Runnable action) {
        return new SimpleSchedulerTask(key, summary, action);
    }

    public static SchedulerTask newTask(String key, Runnable action) {
        return new SimpleSchedulerTask(key, key, action);
    }

    public static class SimpleSchedulerTask extends SchedulerTask {

        private String key;
        private String description;
        Runnable wrappedAction;

        public SimpleSchedulerTask(String key, String description, Runnable wrappedAction) {
            super();
            this.key = key;
            this.description = description;
            this.wrappedAction = wrappedAction;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return description;
        }

        @Override
        public void execute() {
            wrappedAction.run();

        }

    }

}
