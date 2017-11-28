/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.*;
import org.dashbuilder.dataset.date.TimeAmount;

import java.util.*;

/**
 * Generates metrics on an emulated cluster
 */
public class ClusterMetricsGenerator implements DataSetGenerator {

    public static final String COLUMN_SERVER = "server";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_CPU0 = "cpu0";
    public static final String COLUMN_CPU1 = "cpu1";
    public static final String COLUMN_MEMORY_FREE = "mem_free";
    public static final String COLUMN_MEMORY_USED = "mem_used";
    public static final String COLUMN_NETWORK_TX = "tx";
    public static final String COLUMN_NETWORK_RX = "rx";
    public static final String COLUMN_PROCESSES_RUNNING = "p_running";
    public static final String COLUMN_PROCESSES_SLEEPING  = "p_sleeping";
    public static final String COLUMN_DISK_FREE = "disk_free";
    public static final String COLUMN_DISK_USED = "disk_used";

    
    DataSet dataSet = null;
    long timeFrameMillis = 100000;
    List<String> aliveNodes = new ArrayList<>();
    List<String> overloadedNodes = new ArrayList<>();

    public ClusterMetricsGenerator() {
        dataSet = DataSetFactory.newDataSetBuilder()
                .column(COLUMN_SERVER, ColumnType.LABEL)
                .column(COLUMN_TIMESTAMP, ColumnType.DATE)
                .column(COLUMN_CPU0, ColumnType.NUMBER)
                .column(COLUMN_CPU1, ColumnType.NUMBER)
                .column(COLUMN_MEMORY_FREE, ColumnType.NUMBER)
                .column(COLUMN_MEMORY_USED, ColumnType.NUMBER)
                .column(COLUMN_NETWORK_TX, ColumnType.NUMBER)
                .column(COLUMN_NETWORK_RX, ColumnType.NUMBER)
                .column(COLUMN_PROCESSES_RUNNING, ColumnType.NUMBER)
                .column(COLUMN_PROCESSES_SLEEPING, ColumnType.NUMBER)
                .column(COLUMN_DISK_FREE, ColumnType.NUMBER)
                .column(COLUMN_DISK_USED, ColumnType.NUMBER)
                .buildDataSet();
    }

    public synchronized DataSet buildDataSet(Map<String,String> params) {
        // Check if the data set is up to date.
        long now = System.currentTimeMillis();
        long last = dataSet.getRowCount() > 0 ? ((Date)dataSet.getValueAt(0, 1)).getTime() : -1;
        long diff = now-last;
        if (last != -1 && diff < 1000) {
            return dataSet;
        }

        if (!StringUtils.isBlank(params.get("timeFrame"))) {
            String p = params.get("timeFrame");
            if (p != null && p.trim().length() > 0) {
                TimeAmount timeFrame = TimeAmount.parse(p);
                timeFrameMillis = timeFrame.toMillis();
            }
        }
        if (params.containsKey("aliveNodes")) {
            aliveNodes.clear();
            aliveNodes.addAll(Arrays.asList(StringUtils.split(params.get("aliveNodes"), ",")));
        }
        if (params.containsKey("overloadedNodes")) {
            overloadedNodes.clear();
            overloadedNodes.addAll(Arrays.asList(StringUtils.split(params.get("overloadedNodes"), ",")));
        }
        if (aliveNodes.isEmpty()) {
            return dataSet;
        }
        if (diff > timeFrameMillis) {
            diff = timeFrameMillis;
        }

        // Create a new data set containing the missing metrics since the last update.
        if (last == -1) last = now-timeFrameMillis;
        DataSet newDataSet = dataSet.cloneEmpty();
        long seconds = diff / 1000;

        for (long i = 1; i <=seconds; i++) {
            long metricTime = last + i*1000;
            for (int j = 0; j < aliveNodes.size(); j++) {
                Double lastCpu0 = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 2) : null;
                Double lastCpu1 = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 3) : null;
                Double lastFreeMem = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 4) : null;
                Double lastTx = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 6) : null;
                Double lastRx = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 7) : null;
                Double lastRunningProc = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 8) : null;
                Double lastSleepingProc = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 9) : null;
                Double lastFreeDisk = dataSet.getRowCount() > j ? (Double) dataSet.getValueAt(j, 10) : null;

                String node = aliveNodes.get(j);
                double memFree = mem(node, lastFreeMem, 16d, 12d);
                double diskFree = disk(node, lastFreeDisk, 4000d, 3600d);

                newDataSet.addValuesAt(0, node, new Date(metricTime),
                        cpu(node, lastCpu0, 100d, 90d), cpu(node, lastCpu1, 100d, 90d),
                        memFree, 16-memFree,
                        net(node, lastTx, 4000d, 3000d), net(node, lastRx, 2000d, 1800d),
                        proc(node, lastRunningProc, 1500d, 1024d), proc(node, lastSleepingProc, 500d, 400d),
                        diskFree, 4000-diskFree);
            }
        }
        // Add the remain metric history
        boolean outOfBounds = false;
        Date threshold = new Date(now - timeFrameMillis);
        for (int i = 0; i < dataSet.getRowCount() && !outOfBounds; i++) {
            Date metricTime = (Date)dataSet.getValueAt(i, 1);
            if (metricTime.after(threshold)) {
                newDataSet.addValues(
                        dataSet.getValueAt(i, 0),
                        dataSet.getValueAt(i, 1),
                        dataSet.getValueAt(i, 2),
                        dataSet.getValueAt(i, 3),
                        dataSet.getValueAt(i, 4),
                        dataSet.getValueAt(i, 5),
                        dataSet.getValueAt(i, 6),
                        dataSet.getValueAt(i, 7),
                        dataSet.getValueAt(i, 8),
                        dataSet.getValueAt(i, 9),
                        dataSet.getValueAt(i, 10),
                        dataSet.getValueAt(i, 11));
            } else {
                outOfBounds = true;
            }
        }
        return dataSet = newDataSet;
    }

    /**
     * Network (kbps)
     */
    public Double net(String node, Double last, Double max, Double overloaded) {
        double r = Math.random() - 0.5;
        if (overloadedNodes.contains(node)) {
            if (last == null) {
                return max + 100 * r;
            } else {
                double v = last + 100 * r;
                if (v > max) return max;
                if (v < overloaded) return overloaded;
                return v;
            }
        }
        if (last == null) {
            return 1000 + 100 * r;
        } else {
            double v = last + 100 * r;
            if (v > max) return max;
            if (v < 0) return 0d;
            return v;
        }
    }

    /**
     * Processes (count)
     * Overloaded values : from 1024 to 1500
     */
    public Double proc(String node, Double last, Double max, Double overloaded) {
        double r = Math.random() - 0.5;
        if (overloadedNodes.contains(node)) {
            if (last == null) {
                return overloaded;
            } else {
                double v = last + 100 * r;
                if (v > max) return max;
                if (v < overloaded) return overloaded;
                return v;
            }
        }
        if (last == null) {
            return 280 + 10 * r;
        } else {
            double v = last + 10 * r;
            if (v > max) return max;
            if (v < 0) return 0d;
            return v;
        }
    }

    /**
     * Disk space (Gb) 
     */
    public Double disk(String node, Double last, Double max, Double overloaded) {
        double r = Math.random() - 0.5;
        if (overloadedNodes.contains(node)) {
            if (last == null) {
                return overloaded + 400 * r;
            } else {
                double v = last + 400 * r;
                if (v > max) return max;
                if (v < overloaded) return overloaded;
                return v;
            }
        }
        if (last == null) {
            return 500 + 400 * r;
        } else {
            double v = last + 400 * r;
            if (v > max) return max;
            if (v < 0) return 0d;
            return v;
        }
    }

    /**
     * CPU (%) 
     * Overloaded values : from 90% to 100% 
     */
    public Double cpu(String node, Double last, Double max, Double overloaded) {
        double r = Math.random() - 0.5;
        if (overloadedNodes.contains(node)) {
            if (last == null) {
                return overloaded + 10 * r;
            } else {
                double v = last + 10 * r;
                if (v > max) return max;
                if (v < overloaded) return overloaded;
                return v;
            }
        }
        if (last == null) {
            return 20 + 20 * r;
        } else {
            double v = last + 10 * r;
            if (v > max) return max;
            if (v < 0) return 0d;
            return v;
        }
    }

    /**
     * Memory (Gb) 
     * Overloaded values : from 3Gb to 4Gb 
     */
    public Double mem(String node, Double last, Double max, Double overloaded) {
        double r = Math.random() - 0.5;
        if (overloadedNodes.contains(node)) {
            if (last == null) {
                return overloaded + r;
            } else {
                double v = last + r;
                if (v > max) return max;
                if (v < overloaded) return overloaded;
                return v;
            }
        }
        if (last == null) {
            return 1 + r;
        } else {
            double v = last + r;
            if (v > max) return max;
            if (v < 0) return 0d;
            return v;
        }
    }

    public static void main(String[] args) throws Exception {
        ClusterMetricsGenerator g = new ClusterMetricsGenerator();
        Map<String,String> params = new HashMap<>();
        params.put("aliveNodes", "server1");
        params.put("timeFrame", "10second");
        System.out.println("************* Single node not overloaded *******************************");
        for (int i = 0; i < 5; i++) {
            DataSet dataSet = g.buildDataSet(params);
            printDataSet(dataSet);
            Thread.sleep(1000);
        }

        System.out.println("************* Two nodes and the second one overloaded *******************************");
        g = new ClusterMetricsGenerator();
        params = new HashMap<>();
        params.put("aliveNodes", "server1,server2");
        params.put("overloadedNodes", "server2");
        params.put("timeFrame", "10second");
        for (int i = 0; i < 5; i++) {
            DataSet dataSet = g.buildDataSet(params);
            printDataSet(dataSet);
            Thread.sleep(1000);
        }
    }

    /**
     * Helper method to print to standard output the dataset values.
     */
    protected static void printDataSet(DataSet dataSet) {
        final String SPACER = "| \t |";

        if (dataSet == null) System.out.println("DataSet is null");
        if (dataSet.getRowCount() == 0) System.out.println("DataSet is empty");

        List<DataColumn> dataSetColumns = dataSet.getColumns();
        int colColunt = dataSetColumns.size();
        int rowCount = dataSet.getRowCount();

        System.out.println("********************************************************************************************************************************************************");
        for (int row = 0; row < rowCount; row++) {
            System.out.print(SPACER);
            for (int col= 0; col< colColunt; col++) {
                Object value = dataSet.getValueAt(row, col);
                String colId = dataSet.getColumnByIndex(col).getId();
                System.out.print(colId + ": " +  value);
                System.out.print(SPACER);
            }
            System.out.println("");
        }
        System.out.println("********************************************************************************************************************************************************");
    }
}
