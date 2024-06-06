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
	"fmt"

	apps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// Equals to PostgreSQL 12.7, using digest for image mirroring
	postgresqlImage = "docker.io/library/postgres@sha256:d36e6b8b3e1fae1d36f2fb785005714ad9094c22103c7d5bc5c21635fbb3a0a7"

	postgresqlPort                     = 5432
	postgresqlPersistentVolumeCapacity = "1Gi"

	postgresqlLabelName  = "app"
	postgresqlLabelValue = "postgres"
)

// WaitForPostgresqlInstance waits for Postgresql instance pods to be up
func WaitForPostgresqlInstance(namespace string, nbPods, timeoutInMin int) error {
	return WaitForPodsWithLabel(namespace, postgresqlLabelName, postgresqlLabelValue, nbPods, timeoutInMin)
}

// CreatePostgresqlInstance creates a new Postgresql instance
func CreatePostgresqlInstance(namespace, name string, nbPods int, username, password, databaseName string) error {

	pvc := getPostgresqlPersistentVolumeClaimResource(namespace, name)
	if err := CreateObject(pvc); err != nil {
		return err
	}

	d := getPostgresqlDeploymentResource(namespace, name, nbPods, username, password, databaseName)
	if err := CreateObject(d); err != nil {
		return err
	}

	s := getPostgresqlServiceResource(namespace, name)
	if err := CreateObject(s); err != nil {
		return err
	}

	return nil
}

func getPostgresqlDeploymentResource(namespace, name string, nbPods int, username, password, databaseName string) *apps.Deployment {
	replicas := int32(nbPods)
	return &apps.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: apps.DeploymentSpec{
			Replicas: &replicas,
			Selector: &metav1.LabelSelector{
				MatchLabels: map[string]string{postgresqlLabelName: postgresqlLabelValue},
			},
			Template: v1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: map[string]string{postgresqlLabelName: postgresqlLabelValue},
				},
				Spec: v1.PodSpec{
					Containers: []v1.Container{
						{
							Name:  name,
							Image: postgresqlImage,
							Ports: []v1.ContainerPort{
								{ContainerPort: postgresqlPort},
							},
							Env: []v1.EnvVar{
								{Name: "POSTGRES_USER", Value: username},
								{Name: "POSTGRES_PASSWORD", Value: password},
								{Name: "POSTGRES_DB", Value: databaseName},
								// Needed due to issues with owner of /var/lib/postgresql/data folder
								{Name: "PGDATA", Value: "/var/lib/postgresql/data/pgdata"},
							},
							VolumeMounts: []v1.VolumeMount{
								{MountPath: "/var/lib/postgresql/data", Name: name},
							},
							LivenessProbe: &v1.Probe{
								InitialDelaySeconds: 30,
								PeriodSeconds:       10,
								TimeoutSeconds:      5,
								SuccessThreshold:    1,
								FailureThreshold:    6,
								ProbeHandler: v1.ProbeHandler{
									Exec: &v1.ExecAction{
										Command: []string{"bash", "-ec", "PGPASSWORD=$POSTGRES_PASSWORD psql -w -U '" + username + "' -d '" + databaseName + "'  -h 127.0.0.1 -c 'SELECT 1'"},
									},
								},
							},
							ReadinessProbe: &v1.Probe{
								InitialDelaySeconds: 5,
								PeriodSeconds:       10,
								TimeoutSeconds:      5,
								SuccessThreshold:    1,
								FailureThreshold:    6,
								ProbeHandler: v1.ProbeHandler{
									Exec: &v1.ExecAction{
										Command: []string{"bash", "-ec", "PGPASSWORD=$POSTGRES_PASSWORD psql -w -U '" + username + "' -d '" + databaseName + "'  -h 127.0.0.1 -c 'SELECT 1'"},
									},
								},
							},
						},
					},
					Volumes: []v1.Volume{
						{
							Name:         name,
							VolumeSource: v1.VolumeSource{PersistentVolumeClaim: &v1.PersistentVolumeClaimVolumeSource{ClaimName: name}},
						},
					},
				},
			},
		},
	}
}

func getPostgresqlServiceResource(namespace, name string) *corev1.Service {
	return &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: v1.ServiceSpec{
			Ports:    []v1.ServicePort{{Port: postgresqlPort}},
			Selector: map[string]string{postgresqlLabelName: postgresqlLabelValue},
		},
	}
}

func getPostgresqlPersistentVolumeClaimResource(namespace, name string) *corev1.PersistentVolumeClaim {
	return &corev1.PersistentVolumeClaim{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: v1.PersistentVolumeClaimSpec{
			AccessModes: []v1.PersistentVolumeAccessMode{corev1.ReadWriteOnce},
			Resources: v1.ResourceRequirements{
				Requests: v1.ResourceList{corev1.ResourceStorage: resource.MustParse(postgresqlPersistentVolumeCapacity)},
			},
		},
	}
}

// SetPostgresqlReplicas sets the number of replicas for an Postgresql instance
func SetPostgresqlReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Info("Set Postgresql props for", "name", name, "replica number", nbPods)
	deployment, err := GetDeployment(namespace, name)
	if err != nil {
		return err
	} else if deployment == nil {
		return fmt.Errorf("No Postgresql Deployment found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	deployment.Spec.Replicas = &replicas
	return UpdateObject(deployment)
}
