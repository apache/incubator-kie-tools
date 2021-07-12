// Copyright 2021 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package steps

import (
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"github.com/kiegroup/kogito-operator/test/pkg/steps/mappers"
	apps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

/*
	DataTable for PostgreSQL:
	| username | developer |
	| password | mypass    |
	| database | kogito    |
*/

const (
	// Equals to PostgreSQL 12.7, using digest for image mirroring
	postgresqlImage = "docker.io/library/postgres@sha256:d36e6b8b3e1fae1d36f2fb785005714ad9094c22103c7d5bc5c21635fbb3a0a7"

	postgresqlPort                     = 5432
	postgresqlPersistentVolumeCapacity = "1Gi"
	postgresqlLabelName                = "app"
	postgresqlLabelValue               = "postgres"
)

func registerPostgresqlSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^PostgreSQL instance "([^"]*)" is deployed within (\d+) (?:minute|minutes) with configuration:$`, data.postgresqlInstanceIsDeployedWithConfiguration)
}

func (data *Data) postgresqlInstanceIsDeployedWithConfiguration(name string, timeOutInMin int, table *godog.Table) error {
	creds := &mappers.PostgresqlCredentialsConfig{}
	if err := mappers.MapPostgresqlCredentialsFromTable(table, creds); err != nil {
		return err
	}

	pvc := getPostgresqlPersistentVolumeClaimResource(name, data.Namespace)
	if err := framework.CreateObject(pvc); err != nil {
		return err
	}

	d := getPostgresqlDeploymentResource(name, data.Namespace, creds.Username, creds.Password, creds.Database)
	if err := framework.CreateObject(d); err != nil {
		return err
	}

	s := getPostgresqlServiceResource(name, data.Namespace)
	if err := framework.CreateObject(s); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, postgresqlLabelName, postgresqlLabelValue, 1, 3)
}

func getPostgresqlDeploymentResource(name, namespace, username, password, databaseName string) *apps.Deployment {
	replicas := int32(1)
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

func getPostgresqlServiceResource(name, namespace string) *corev1.Service {
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

func getPostgresqlPersistentVolumeClaimResource(name, namespace string) *corev1.PersistentVolumeClaim {
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
