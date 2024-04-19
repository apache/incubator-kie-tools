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

package installers

import (
	"fmt"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

const (
	nodeScraperStart = `#!/bin/bash
		CWD=$(dirname $0)
		curl -s node-scraper:8080/start -H 'content-type: application/json' --data-binary @$CWD/.node-scraper-config.json > $RUN_DIR/node-scraper.job`
	nodeScraperStop = `#!/bin/bash
		curl -s node-scraper:8080/stop/$(cat $RUN_DIR/node-scraper.job) > $RUN_DIR/cpu.json
		# jq cannot write into file used as a source, need to move original all.json to a temporary location
		mv $RUN_DIR/all.json $RUN_DIR/all-temp.json
		jq -c -s '{ hyperfoil: .[0], cpu: { "$schema": "urn:node-scraper", data: .[1] }}' $RUN_DIR/all-temp.json $RUN_DIR/cpu.json > $RUN_DIR/all.json`

	// NodeScraperStartConfigMapName ConfigMap name for start script of Node scraper
	NodeScraperStartConfigMapName = "node-scraper-start"
	// NodeScraperStopConfigMapName ConfigMap name for stop script of Node scraper
	NodeScraperStopConfigMapName = "node-scraper-stop"
)

var (
	// hyperfoilNodeScraperYamlNamespacedInstaller installs Hyperfoil node scraper namespaced using YAMLs
	hyperfoilNodeScraperYamlNamespacedInstaller = YamlNamespacedServiceInstaller{
		InstallNamespacedYaml:           installHyperfoilNodeScraper,
		WaitForNamespacedServiceRunning: waitForHyperfoilNodeScraperRunning,
		GetAllNamespaceYamlCrs:          getHyperfoilNodeScraperCrsInNamespace,
		UninstallNamespaceYaml:          uninstallHyperfoilNodeScraper,
		NamespacedYamlServiceName:       hyperfoilNodeScraperServiceName,
	}
	hyperfoilNodeScraperServiceName    = "Hyperfoil Node scraper"
	hyperfoilNodeScraperDeploymentName = "node-scraper"
	hyperfoilNodeScraperImage          = "quay.io/rvansa/node-scraper"
)

// GetHyperfoilNodeScraperInstaller returns Hyperfoil Node scraper installer
func GetHyperfoilNodeScraperInstaller() ServiceInstaller {
	return &hyperfoilNodeScraperYamlNamespacedInstaller
}

func installHyperfoilNodeScraper(namespace string) error {
	framework.GetLogger(namespace).Info("Deploy Hyperfoil Node scraper")

	if err := framework.CreateServiceAccount(namespace, getHyperfoilNodeScraperUniqueName(namespace)); err != nil {
		return err
	}
	if err := createHyperfoilNodeScraperClusterRole(getHyperfoilNodeScraperUniqueName(namespace)); err != nil {
		return err
	}
	if err := createHyperfoilNodeScraperClusterRoleBinding(getHyperfoilNodeScraperUniqueName(namespace), namespace); err != nil {
		return err
	}

	scraperConfig, err := getNodeScraperConfigContent(namespace)
	if err != nil {
		return err
	}

	err = framework.CreateConfigMap(namespace, NodeScraperStartConfigMapName, map[string]string{".node-scraper-config.json": scraperConfig, "99-node-scraper-start.sh": nodeScraperStart}, nil)
	if err != nil {
		return err
	}
	err = framework.CreateConfigMap(namespace, NodeScraperStopConfigMapName, map[string]string{"00-node-scraper-stop.sh": nodeScraperStop}, nil)
	if err != nil {
		return err
	}

	return deployNodeScraper(namespace)
}

func waitForHyperfoilNodeScraperRunning(namespace string) error {
	return framework.WaitForPodsWithLabel(namespace, "app", hyperfoilNodeScraperDeploymentName, 1, 3)
}

func uninstallHyperfoilNodeScraper(namespace string) error {
	var originalError error

	// Delete cluster wide resources, the rest is deleted together with namespace
	crb, err := framework.GetClusterRoleBinding(getHyperfoilNodeScraperUniqueName(namespace))
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot retrieve ClusterRoleBinding %s", getHyperfoilNodeScraperUniqueName(namespace)))
		if originalError == nil {
			originalError = err
		}
	} else {
		if err = framework.DeleteObject(crb); err != nil {
			framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot delete ClusterRoleBinding %s", getHyperfoilNodeScraperUniqueName(namespace)))
			if originalError == nil {
				originalError = err
			}
		}
	}

	cr, err := framework.GetClusterRole(getHyperfoilNodeScraperUniqueName(namespace))
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot retrieve ClusterRole %s", getHyperfoilNodeScraperUniqueName(namespace)))
		if originalError == nil {
			originalError = err
		}
	} else {
		if err = framework.DeleteObject(cr); err != nil {
			framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot delete ClusterRole %s", getHyperfoilNodeScraperUniqueName(namespace)))
			if originalError == nil {
				originalError = err
			}
		}
	}

	return originalError
}

func getHyperfoilNodeScraperCrsInNamespace(namespace string) ([]client.Object, error) {
	return []client.Object{}, nil
}

// Helper functions

func getHyperfoilNodeScraperUniqueName(namespace string) string {
	return "node-scraper-" + namespace
}

func createHyperfoilNodeScraperClusterRole(name string) error {
	clusterRole := &rbac.ClusterRole{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
		Rules: []rbac.PolicyRule{
			{
				Verbs:           []string{"get"},
				NonResourceURLs: []string{"/metrics"},
			},
		},
	}

	return framework.CreateObject(clusterRole)
}

func createHyperfoilNodeScraperClusterRoleBinding(name, namespace string) error {
	clusterRoleBinding := &rbac.ClusterRoleBinding{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
		RoleRef: rbac.RoleRef{
			APIGroup: "rbac.authorization.k8s.io",
			Kind:     "ClusterRole",
			Name:     getHyperfoilNodeScraperUniqueName(namespace),
		},
		Subjects: []rbac.Subject{
			{
				Kind:      "ServiceAccount",
				Name:      getHyperfoilNodeScraperUniqueName(namespace),
				Namespace: namespace,
			},
		},
	}

	return framework.CreateObject(clusterRoleBinding)
}

func getNodeScraperConfigContent(namespace string) (scraperConfig string, err error) {
	nodes, err := framework.CreateCommand("oc", "get", "node", "-l", "node-role.kubernetes.io/worker", "-o", "json").WithLoggerContext(namespace).Execute()
	if err != nil {
		return
	}
	tempFilePath, err := framework.CreateTemporaryFile("cluster-worker-nodes*.yaml", nodes)
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while storing worker nodes to temporary file")
		return
	}

	return framework.CreateCommand("jq", "-c", "{ nodes: [ .items[] | { node : .metadata.name | split(\".\") | .[0], url: (\"https://\" + .status.addresses[0].address + \":9100/metrics\") }] , scrapeInterval: 5000}", tempFilePath).WithLoggerContext(namespace).Execute()
}

func deployNodeScraper(namespace string) error {
	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      hyperfoilNodeScraperDeploymentName,
			Namespace: namespace,
		},
		Spec: appsv1.DeploymentSpec{
			Selector: &metav1.LabelSelector{MatchLabels: map[string]string{"app": hyperfoilNodeScraperDeploymentName}},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: map[string]string{"app": hyperfoilNodeScraperDeploymentName},
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name:  hyperfoilNodeScraperDeploymentName,
							Image: hyperfoilNodeScraperImage,
						},
					},
					ServiceAccountName: getHyperfoilNodeScraperUniqueName(namespace),
				},
			},
		},
	}

	if err := framework.CreateObject(deployment); err != nil {
		return err
	}

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      hyperfoilNodeScraperDeploymentName,
			Namespace: namespace,
		},
		Spec: corev1.ServiceSpec{
			Ports: []corev1.ServicePort{
				{
					Protocol:   "TCP",
					Port:       8080,
					TargetPort: intstr.FromInt(8080),
				},
			},
			Selector: deployment.Spec.Selector.MatchLabels,
		},
	}

	if err := framework.CreateObject(service); err != nil {
		return err
	}
	return nil
}
