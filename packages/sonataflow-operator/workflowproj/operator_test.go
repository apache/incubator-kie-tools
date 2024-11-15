// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package workflowproj

import (
	"testing"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestCreateNewManagedPropsConfigMap(t *testing.T) {
	type args struct {
		workflow   *operatorapi.SonataFlow
		properties string
	}
	tests := []struct {
		name string
		args args
		want map[string]string
	}{
		{
			"when workflow has labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name: t.Name(),
				Labels: map[string]string{
					"app.kubernetes.io/name":       t.Name(),
					"app.kubernetes.io/component":  "serverless-workflow",
					"app.kubernetes.io/managed-by": "sonataflow-operator",
					"app.kubernetes.io/part-of":    "someplatform",
				}}}},

			map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"app.kubernetes.io/part-of":         "someplatform",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
		{
			"when the workflow has no labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name:   t.Name(),
				Labels: map[string]string{}}}},

			map[string]string{
				"app":                          t.Name(),
				"app.kubernetes.io/name":       t.Name(),
				"app.kubernetes.io/component":  "serverless-workflow",
				"app.kubernetes.io/managed-by": "sonataflow-operator",
				// if the workflow is missing a platform then the managed properties won't have them
				//"app.kubernetes.io/part-of":   "someplatform",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, CreateNewManagedPropsConfigMap(
				tt.args.workflow, tt.args.properties).GetLabels(),
				"CreateNewManagedPropsConfigMap(%v, %v)", tt.args.workflow, tt.args.properties)
		})
	}
}

func TestCreateNewUserPropsConfigMap(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want *corev1.ConfigMap
	}{
		{
			"when the workflow has no labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name:   t.Name(),
				Labels: map[string]string{}}}},

			&corev1.ConfigMap{
				ObjectMeta: metav1.ObjectMeta{
					Name:      t.Name() + "-props",
					Namespace: "",
					Labels: map[string]string{
						"app":                               t.Name(),
						"app.kubernetes.io/name":            t.Name(),
						"app.kubernetes.io/component":       "serverless-workflow",
						"app.kubernetes.io/managed-by":      "sonataflow-operator",
						"sonataflow.org/workflow-app":       t.Name(),
						"sonataflow.org/workflow-namespace": "",
					},
				},
				Data: map[string]string{
					"application.properties": "",
				},
			},
		},
		{
			"when the workflow has labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name: t.Name(),
				Labels: map[string]string{
					"older-label": t.Name(),
				}}}},

			&corev1.ConfigMap{
				ObjectMeta: metav1.ObjectMeta{
					Name:      t.Name() + "-props",
					Namespace: "",
					Labels: map[string]string{
						"older-label":                       t.Name(),
						"app":                               t.Name(),
						"app.kubernetes.io/name":            t.Name(),
						"app.kubernetes.io/component":       "serverless-workflow",
						"app.kubernetes.io/managed-by":      "sonataflow-operator",
						"sonataflow.org/workflow-app":       t.Name(),
						"sonataflow.org/workflow-namespace": "",
					},
				},
				Data: map[string]string{
					"application.properties": "",
				},
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, CreateNewUserPropsConfigMap(tt.args.workflow), "CreateNewUserPropsConfigMap(%v)", tt.args.workflow)
		})
	}
}

func TestGetDefaultLabels(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want map[string]string
	}{
		{
			"when the workflow has no labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name:   t.Name(),
				Labels: map[string]string{}}}},

			map[string]string{
				"app":                          t.Name(),
				"app.kubernetes.io/name":       t.Name(),
				"app.kubernetes.io/component":  "serverless-workflow",
				"app.kubernetes.io/managed-by": "sonataflow-operator",
				// if the workflow is missing a platform then the managed properties won't have them
				//"app.kubernetes.io/part-of":   "someplatform",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
		{
			"when workflow has labels",
			args{workflow: &operatorapi.SonataFlow{ObjectMeta: v1.ObjectMeta{
				Name: t.Name(),
				Labels: map[string]string{
					"some-older-label-should-not-be-included": t.Name(),
				}}}},

			map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetDefaultLabels(tt.args.workflow), "GetDefaultLabels(%v)", tt.args.workflow)
		})
	}
}

func TestGetManagedPropertiesFileName(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{
			name: "test",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
				},
			}},
			want: "application-prod.properties",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetManagedPropertiesFileName(tt.args.workflow), "GetManagedPropertiesFileName(%v)", tt.args.workflow)
		})
	}
}

func TestGetMergedLabels(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want map[string]string
	}{
		{
			name: "when workflow has no labels",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
				},
			}},
			want: map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
		{
			name: "when workflow has labels",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
					Labels: map[string]string{
						"some-older-label": t.Name(),
					},
				},
			}},
			want: map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
				"some-older-label":                  t.Name(),
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetMergedLabels(tt.args.workflow), "GetMergedLabels(%v)", tt.args.workflow)
		})
	}
}

func TestGetSelectorLabels(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want map[string]string
	}{
		{
			name: "when workflow has no labels",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
				},
			}},
			want: map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
		{
			name: "when workflow has labels",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
					Labels: map[string]string{
						"app":                               t.Name(),
						"app.kubernetes.io/name":            t.Name(),
						"app.kubernetes.io/component":       "serverless-workflow",
						"app.kubernetes.io/managed-by":      "sonataflow-operator",
						"sonataflow.org/workflow-app":       t.Name(),
						"sonataflow.org/workflow-namespace": "",
					},
				},
			}},
			want: map[string]string{
				"app":                               t.Name(),
				"app.kubernetes.io/name":            t.Name(),
				"app.kubernetes.io/component":       "serverless-workflow",
				"app.kubernetes.io/managed-by":      "sonataflow-operator",
				"sonataflow.org/workflow-app":       t.Name(),
				"sonataflow.org/workflow-namespace": "",
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetSelectorLabels(tt.args.workflow), "GetSelectorLabels(%v)", tt.args.workflow)
		})
	}
}

func TestGetWorkflowManagedPropertiesConfigMapName(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	var tests = []struct {
		name string
		args args
		want string
	}{
		{
			name: "managed props configMap name",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
				},
			}},
			want: t.Name() + "-managed-props",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetWorkflowManagedPropertiesConfigMapName(tt.args.workflow), "GetWorkflowManagedPropertiesConfigMapName(%v)", tt.args.workflow)
		})
	}
}

func TestGetWorkflowUserPropertiesConfigMapName(t *testing.T) {
	type args struct {
		workflow *operatorapi.SonataFlow
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{
			name: "test",
			args: args{workflow: &operatorapi.SonataFlow{
				ObjectMeta: v1.ObjectMeta{
					Name: t.Name(),
				},
			}},
			want: t.Name() + "-props",
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetWorkflowUserPropertiesConfigMapName(tt.args.workflow), "GetWorkflowUserPropertiesConfigMapName(%v)", tt.args.workflow)
		})
	}
}
