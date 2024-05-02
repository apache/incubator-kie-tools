/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package framework

import (
	"encoding/csv"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"os"
	"reflect"
	"strings"
	"sync"
	"time"

	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/env"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/logger"

	"io/ioutil"

	"k8s.io/api/events/v1beta1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
)

const (
	defaultLogFolder       = "logs"
	logSuffix              = ".log"
	defaultResultsFileName = "results.csv"
)

var (
	logFolder = defaultLogFolder

	monitoredNamespaces sync.Map
	loggerOpts          sync.Map
)

// GetMainLogger returns the main logger
func GetMainLogger() logger.Logger {
	return GetLogger("main")
}

// GetLogger retrieves the logger for a namespace
func GetLogger(namespace string) logger.Logger {
	opts, err := getOrCreateLoggerOpts(namespace)
	if err != nil {
		fallbackLog := log.Log.WithName(namespace)
		fallbackLog.Error(err, "Error getting logger", "namespace", namespace)
		return logger.Logger{
			Logger: fallbackLog,
		}
	}
	return logger.GetLoggerWithOptions(namespace, opts)
}

// FlushLogger flushes a specific logger
func FlushLogger(namespace string) error {
	opts, exists := getLoggerOpts(namespace)
	if !exists {
		return fmt.Errorf("Logger %s does not exist... skipping", namespace)
	}
	if writer, ok := opts.Output.(io.Closer); ok {
		err := writer.Close()
		loggerOpts.Delete(namespace)
		return err
	}
	return nil
}

// FlushAllRemainingLoggers flushes all remaining loggers
func FlushAllRemainingLoggers() {
	loggerOpts.Range(func(logName, _ interface{}) bool {
		if err := FlushLogger(logName.(string)); err != nil {
			GetMainLogger().Error(err, "Error flushing logger", "logName", logName)
		}
		return true
	})
}

func getLoggerOpts(logName string) (*logger.Opts, bool) {
	opts, exists := loggerOpts.Load(logName)
	if exists {
		return opts.(*logger.Opts), exists
	}
	return nil, exists
}

func getOrCreateLoggerOpts(logName string) (*logger.Opts, error) {
	opts, exists := getLoggerOpts(logName)
	if !exists {
		if err := createPrefixedLogFolder(logName); err != nil {
			return nil, fmt.Errorf("Error while creating log folder: %v", err)
		}

		fileWriter, err := os.Create(getLogFile(logName, "test-run"))
		if err != nil {
			return nil, fmt.Errorf("Error while creating filewriter: %v", err)
		}

		opts = &logger.Opts{
			Output:  io.MultiWriter(os.Stdout, fileWriter),
			Verbose: env.GetBoolOSEnv("DEBUG"),
		}
		loggerOpts.Store(logName, opts)
	}

	return opts, nil
}

// RenameLogFolder changes the name of the log folder for a specific namespace
func RenameLogFolder(namespace string, newLogFolderNames ...string) error {
	if len(newLogFolderNames) > 1 {
		newParentFolderName := strings.Join(newLogFolderNames[0:len(newLogFolderNames)-1], "/")
		if err := CreateFolder(GetNamespacedLogFolder(newParentFolderName)); err != nil {
			return err
		}
	}
	return os.Rename(GetNamespacedLogFolder(namespace), GetNamespacedLogFolder(strings.Join(newLogFolderNames, "/")))
}

// StartPodLogCollector monitors a namespace and stores logs of all pods running in the namespace
func StartPodLogCollector(namespace string) error {
	if isNamespaceMonitored(namespace) {
		return errors.New("namespace is already monitored")
	}

	if err := createPrefixedLogFolder(namespace); err != nil {
		return fmt.Errorf("Error while creating log folder: %v", err)
	}

	monitoredNamespace := &monitoredNamespace{
		pods:           make(map[string]*monitoredPod),
		stopMonitoring: make(chan bool),
	}
	monitoredNamespaces.Store(namespace, monitoredNamespace)

	scanningPeriod := time.NewTicker(5 * time.Second)
	defer scanningPeriod.Stop()
	for {
		select {
		case <-monitoredNamespace.stopMonitoring:
			return nil
		case <-scanningPeriod.C:
			if pods, err := GetPods(namespace); err != nil {
				GetLogger(namespace).Error(err, "Error while getting pods", "namespace", namespace)
			} else {
				for _, pod := range pods.Items {
					if !isPodMonitored(namespace, pod.Name) && IsPodRunning(&pod) {
						initMonitoredPod(namespace, pod.Name)
						for _, container := range pod.Spec.Containers {
							initMonitoredContainer(namespace, pod.Name, container.Name)
							go storeContainerLogWithFollow(namespace, pod.Name, container.Name)
						}
					}
				}
			}
		}
	}
}

// ReportPerformanceMetric reports a new metric with its value and unit to a results file. If the file does not exist,
// it will be created. It depends on the existence of the log folder which is created by the framework before the tests
// are run.
func ReportPerformanceMetric(metric, value, unit string) {
	resultsFile, err := getOrCreateResultsFile()
	if err != nil {
		GetMainLogger().Error(err, "Error when retrieving the results file")
		return
	}
	defer func() {
		err = resultsFile.Close()
		if err != nil {
			GetMainLogger().Error(err, "Error while closing the results file")
		}
	}()

	if err = writeCsvValue(resultsFile, []string{metric, value, unit}); err != nil {
		GetMainLogger().Error(err, "Error writing a new measurement to the results file")
	}
}

func isNamespaceMonitored(namespace string) bool {
	_, exists := monitoredNamespaces.Load(namespace)
	return exists
}

func getOrCreateResultsFile() (*os.File, error) {
	resultsFilePath := GetLogFolder() + "/" + defaultResultsFileName
	resultsFile, err := os.OpenFile(resultsFilePath, os.O_APPEND|os.O_WRONLY, 0)
	if err != nil {
		if os.IsNotExist(err) {
			resultsFile, err = os.Create(resultsFilePath)
			if err != nil {
				return nil, fmt.Errorf("Error creating results file: %v", err)
			}
			if err = writeCsvValue(resultsFile, []string{"Metric", "Value", "Unit"}); err != nil {
				return nil, fmt.Errorf("Error while writing header into the results file: %v", err)
			}
		} else {
			return nil, fmt.Errorf("Error while trying opening the results file: %v", err)
		}
	}
	return resultsFile, nil
}

func writeCsvValue(file *os.File, row []string) error {
	csvWriter := csv.NewWriter(file)
	if err := csvWriter.Write(row); err != nil {
		return fmt.Errorf("Error while writing %s into the results file: %v", row, err)
	}
	csvWriter.Flush()
	return nil
}

func getLogFile(namespace, filename string) string {
	return GetNamespacedLogFolder(namespace) + "/" + filename + logSuffix
}

// GetLogFolder returns the main log folder
func GetLogFolder() string {
	return logFolder
}

// GetNamespacedLogFolder retrieves the log folder for a specific namespace
func GetNamespacedLogFolder(namespace string) string {
	return logFolder + "/" + namespace
}

func createPrefixedLogFolder(namespace string) error {
	return CreateFolder(GetNamespacedLogFolder(namespace))
}

func isPodMonitored(namespace, podName string) bool {
	_, exists := getMonitoredNamespace(namespace).pods[podName]
	return exists
}

func initMonitoredPod(namespace, podName string) {
	monitoredPod := &monitoredPod{
		containers: make(map[string]*monitoredContainer),
	}
	getMonitoredNamespace(namespace).pods[podName] = monitoredPod
}

func initMonitoredContainer(namespace, podName, containerName string) {
	monitoredContainer := &monitoredContainer{loggingFinished: false}
	getMonitoredNamespace(namespace).pods[podName].containers[containerName] = monitoredContainer
}

func storeContainerLogWithFollow(namespace, podName, containerName string) {
	log, err := getContainerLogWithFollow(namespace, podName, containerName)
	if err != nil {
		GetLogger(namespace).Error(err, "Error while retrieving log of pod", "podName", podName)
		return
	}

	if isContainerLoggingFinished(namespace, podName, containerName) {
		GetLogger(namespace).Debug("Logging already finished, retrieved log will be ignored.", "container", containerName, "podName", podName)
	} else {
		markContainerLoggingAsFinished(namespace, podName, containerName)
		if err := writeLogIntoTheFile(namespace, podName, containerName, log); err != nil {
			GetLogger(namespace).Error(err, "Error while writing log into the file")
		}
	}
}

// Log is returned once container is terminated
func getContainerLogWithFollow(namespace, podName, containerName string) (string, error) {
	return kubernetes.PodC(kubeClient).GetLogsWithFollow(namespace, podName, containerName)
}

func isContainerLoggingFinished(namespace, podName, containerName string) bool {
	monitoredContainer := getMonitoredNamespace(namespace).pods[podName].containers[containerName]
	return monitoredContainer.loggingFinished
}

func markContainerLoggingAsFinished(namespace, podName, containerName string) {
	monitoredContainer := getMonitoredNamespace(namespace).pods[podName].containers[containerName]
	monitoredContainer.loggingFinished = true
}

func writeLogIntoTheFile(namespace, podName, containerName, log string) error {
	return ioutil.WriteFile(getLogFile(namespace, podName+"-"+containerName), []byte(log), 0644)
}

// StopPodLogCollector waits until all logs are stored on disc
func StopPodLogCollector(namespace string) error {
	if !isNamespaceMonitored(namespace) {
		return errors.New("namespace is not monitored")
	}
	stopNamespaceMonitoring(namespace)
	storeUnfinishedContainersLog(namespace)
	return nil
}

func stopNamespaceMonitoring(namespace string) {
	getMonitoredNamespace(namespace).stopMonitoring <- true
	close(getMonitoredNamespace(namespace).stopMonitoring)
}

// Write log of all containers of pods in namespace which didn't store their log yet
func storeUnfinishedContainersLog(namespace string) {
	for podName, pod := range getMonitoredNamespace(namespace).pods {
		for containerName, container := range pod.containers {
			if !container.loggingFinished {
				storeContainerLog(namespace, podName, containerName)
			}
		}
	}
}

// Write container log into filesystem
func storeContainerLog(namespace string, podName, containerName string) {
	if isContainerLoggingFinished(namespace, podName, containerName) {
		GetLogger(namespace).Info("Logging already finished, retrieved log will be ignored.", "container", containerName, "podName", podName)
	} else {
		log, err := GetContainerLog(namespace, podName, containerName)
		if err != nil {
			GetLogger(namespace).Error(err, "Error while retrieving log", "container", containerName, "podName", podName)
			return
		}

		markContainerLoggingAsFinished(namespace, podName, containerName)
		if err := writeLogIntoTheFile(namespace, podName, containerName, log); err != nil {
			GetLogger(namespace).Error(err, "Error while writing log into the file")
		}
	}
}

// GetContainerLog exported for Zookeeper workaround, can be unexported once https://github.com/strimzi/strimzi-kafka-operator/issues/3092 is fixed
func GetContainerLog(namespace, podName, containerName string) (string, error) {
	return kubernetes.PodC(kubeClient).GetLogs(namespace, podName, containerName)
}

func getMonitoredNamespace(namespace string) *monitoredNamespace {
	loadedNamespace, exists := monitoredNamespaces.Load(namespace)
	if exists {
		return loadedNamespace.(*monitoredNamespace)
	}
	return nil
}

type monitoredNamespace struct {
	pods           map[string]*monitoredPod
	stopMonitoring chan bool
}

type monitoredPod struct {
	containers map[string]*monitoredContainer
}

type monitoredContainer struct {
	loggingFinished bool
}

/////////////////////////////////////////////////////////////////////////
// Events logging
/////////////////////////////////////////////////////////////////////////

const (
	eventLastSeenKey   = "LAST_SEEN"
	eventFirstSeenKey  = "FIRST_SEEN"
	eventCountKey      = "COUNT"
	eventNameKey       = "NAME"
	eventKindKey       = "KIND"
	eventSubObjectKey  = "SUBOBJECT"
	eventTypeKey       = "TYPE"
	eventReasonKey     = "REASON"
	eventActionKey     = "ACTION"
	eventControllerKey = "CONTROLLER"
	eventInstanceKey   = "INSTANCE"
	eventMessageKey    = "MESSAGE"
)

var eventKeys = []string{
	eventLastSeenKey,
	eventFirstSeenKey,
	eventCountKey,
	eventNameKey,
	eventKindKey,
	eventSubObjectKey,
	eventTypeKey,
	eventReasonKey,
	eventActionKey,
	eventControllerKey,
	eventInstanceKey,
	eventMessageKey,
}

// BumpEvents will bump all events into events.log file
func BumpEvents(namespace string) error {
	eventList, err := kubernetes.EventC(kubeClient).GetEvents(namespace)
	if err != nil {
		return fmt.Errorf("Error retrieving events from namespace %s: %v", namespace, err)
	}
	fileWriter, err := os.Create(getLogFile(namespace, "events"))
	if err != nil {
		return fmt.Errorf("Error while creating filewriter: %v", err)
	}

	if err := PrintDataMap(eventKeys, mapEvents(eventList), fileWriter); err != nil {
		return err
	}

	if err := fileWriter.Close(); err != nil {
		return fmt.Errorf("Error while closing filewriter: %v", err)
	}
	return nil
}

func mapEvents(eventList *v1beta1.EventList) []map[string]string {
	eventMaps := []map[string]string{}

	for _, event := range eventList.Items {
		eventMap := make(map[string]string)
		eventMap[eventLastSeenKey] = getDefaultIfNull(event.DeprecatedLastTimestamp.Format("2006-01-02 15:04:05"))
		eventMap[eventFirstSeenKey] = getDefaultIfNull(event.DeprecatedFirstTimestamp.Format("2006-01-02 15:04:05"))
		eventMap[eventNameKey] = getDefaultIfNull(event.GetName())
		eventMap[eventKindKey] = getDefaultIfNull(event.TypeMeta.Kind)
		eventMap[eventSubObjectKey] = getDefaultIfNull(event.Regarding.FieldPath)
		eventMap[eventTypeKey] = getDefaultIfNull(event.Type)
		eventMap[eventReasonKey] = getDefaultIfNull(event.Reason)
		eventMap[eventActionKey] = getDefaultIfNull(event.Action)
		eventMap[eventControllerKey] = getDefaultIfNull(event.ReportingController)
		eventMap[eventInstanceKey] = getDefaultIfNull(event.ReportingInstance)
		eventMap[eventMessageKey] = getDefaultIfNull(event.Note)

		eventMaps = append(eventMaps, eventMap)
	}
	return eventMaps
}

func getDefaultIfNull(value string) string {
	if len(value) <= 0 {
		return "-"
	}
	return value
}

// LogKubernetesObjects log Kubernetes objects for test analysis
func LogKubernetesObjects(namespace string, runtimeObjects ...client.ObjectList) error {
	for _, runtimeObject := range runtimeObjects {
		objectName := reflect.TypeOf(runtimeObject).Elem().Name()

		// Fetch list
		err := kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, runtimeObject)
		if err != nil {
			GetLogger(namespace).Warn("Error logging Kubernetes objects", "namespace", namespace, "error message", err.Error())
			continue
		}

		// Format JSON to readable format
		json, err := json.MarshalIndent(runtimeObject, "", "  ")
		if err != nil {
			return fmt.Errorf("Error marshalling %s in namespace %s: %v", objectName, namespace, err)
		}

		// Write to log folder
		err = ioutil.WriteFile(getLogFile(namespace, objectName), []byte(json), 0644)
		if err != nil {
			return fmt.Errorf("Error storing %s into the file: %v", objectName, err)
		}
	}

	return nil
}
