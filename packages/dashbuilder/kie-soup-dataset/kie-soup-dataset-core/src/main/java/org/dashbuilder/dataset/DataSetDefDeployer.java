/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class looks for Data set definition files within an specific server directory and deploys them.
 */
public class DataSetDefDeployer {

    protected String directory;
    protected int scanIntervalInMillis = 3000;
    protected Logger log = LoggerFactory.getLogger(DataSetDefDeployer.class);
    protected DataSetDefRegistry dataSetDefRegistry;
    protected Thread watcherThread;
    protected DataSetDefJSONMarshaller jsonMarshaller;

    FilenameFilter _deployFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".deploy");
        }
    };

    FilenameFilter _undeployFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".undeploy");
        }
    };

    public DataSetDefDeployer() {
    }

    public DataSetDefDeployer(DataSetDefJSONMarshaller jsonMarshaller, DataSetDefRegistry dataSetDefRegistry) {
        this.jsonMarshaller = jsonMarshaller;
        this.dataSetDefRegistry = dataSetDefRegistry;
    }

    public DataSetDefRegistry getDataSetDefRegistry() {
        return dataSetDefRegistry;
    }

    public void setDataSetDefRegistry(DataSetDefRegistry dataSetDefRegistry) {
        this.dataSetDefRegistry = dataSetDefRegistry;
    }

    public DataSetDefJSONMarshaller getJsonMarshaller() {
        return jsonMarshaller;
    }

    public void setJsonMarshaller(DataSetDefJSONMarshaller jsonMarshaller) {
        this.jsonMarshaller = jsonMarshaller;
    }

    public String getDirectory() {
        return directory;
    }

    public int getScanIntervalInMillis() {
        return scanIntervalInMillis;
    }

    public void setScanIntervalInMillis(int scanIntervalInMillis) {
        this.scanIntervalInMillis = scanIntervalInMillis;
    }

    public boolean isRunning() {
        return !StringUtils.isBlank(directory);
    }

    public synchronized void deploy(final String dir) {

        if (scanIntervalInMillis < 1000) {
            log.error("Polling time can't be lower than 1000 ms");
            return;
        }

        if (!validateDirectory(dir)) {
            log.warn("Data sets deployment directory invalid: " + dir);
            directory = null;
            return;
        }

        log.info("Data sets deployment directory = " + dir);
        directory = dir;
        doDeploy();

        if (scanIntervalInMillis > 0 && watcherThread == null) {
            watcherThread = new Thread(new Runnable() {
                public void run() {
                    // TODO: replace by NIO WatcherService (requires upgrade to Java 7)
                    while (directory != null) {
                        try {
                            Thread.sleep(scanIntervalInMillis);
                            doDeploy();
                        } catch (InterruptedException e) {
                            log.error("Data set watcher thread error.", e);
                        }
                    }
                }
            });
            watcherThread.start();
        }
    }

    public synchronized void stop() {
        directory = null;
    }

    protected boolean validateDirectory(String dir) {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        File rootDir = new File(dir);
        if (!rootDir.exists()) {
            return false;
        }
        if (!rootDir.isDirectory()) {
            return false;
        }
        return true;
    }

    /**
     * Look into the deployment directory and processes any data set definition file found.
     */
    protected synchronized void doDeploy() {
        if (!StringUtils.isBlank(directory)) {

            // Look for data sets deploy
            File[] files = new File(directory).listFiles(_deployFilter);
            if (files != null) {
                for (File f : files) {
                    try {
                        // Avoid repetitions
                        f.delete();

                        // Get the .dset file
                        File dsetFile = new File(f.getAbsolutePath().replace(".deploy", ""));
                        if (!dsetFile.exists()) continue;

                        // Read & parse the data set
                        FileReader fileReader = new FileReader(dsetFile);
                        String json = IOUtils.toString(fileReader);
                        DataSetDef def = jsonMarshaller.fromJson(json);
                        if (StringUtils.isBlank(def.getUUID())) def.setUUID(dsetFile.getName());

                        // CSV specific ...
                        if (def instanceof CSVDataSetDef) {
                            CSVDataSetDef csvDef = (CSVDataSetDef) def;
                            File csvFile = getCSVFile(csvDef);
                            if (csvFile != null) {
                                csvDef.setFilePath(csvFile.getAbsolutePath());
                            } else {
                                log.error("Data set CSV file not found: " + f.getName());
                                continue;
                            }
                        }

                        // Check if the data set really needs to be registered.
                        DataSetDef existingDef = dataSetDefRegistry.getDataSetDef(def.getUUID());
                        if (existingDef != null && jsonMarshaller.toJsonString(existingDef).equals(jsonMarshaller.toJsonString(def))) {
                            // Avoid redundant deployments
                            log.info("Data set already deployed: " + def.getUUID());
                        } else {
                            // Register the data set
                            dataSetDefRegistry.registerDataSetDef(def, "system", "deploy(" + def.getUUID() + ")");
                            log.info("Data set deployed: " + def.getUUID());
                        }
                    } catch (Exception e) {
                        log.error("Data set deployment error: " + f.getName(), e);
                    }
                }
            }

            // Look for data sets undeploy
            files = new File(directory).listFiles(_undeployFilter);
            if (files != null) {
                for (File f : files) {
                    try {
                        // Avoid repetitions
                        f.delete();

                        // Un-deploy the given uuid
                        String uuid = f.getName().replace(".undeploy", "");
                        DataSetDef def = dataSetDefRegistry.getDataSetDef(uuid);
                        if (def != null) {
                            dataSetDefRegistry.removeDataSetDef(uuid, "system", "undeploy(" + uuid + ")");
                            log.info("Data set deleted: " + def.getName());
                        } else {
                            log.error("Data set not found: " + uuid);
                        }
                    } catch (Exception e) {
                        log.error("Data set un-deploy error: " + f.getName(), e);
                    }
                }
            }
        }
    }

    public File getCSVFile(CSVDataSetDef def) throws Exception {
        String path = def.getFilePath();
        if (StringUtils.isBlank(path)) return null;

        File f = new File(path);
        if (f.exists()) return f;

        f = new File(directory, path);
        if (f.exists()) return f;
        return null;
    }
}
