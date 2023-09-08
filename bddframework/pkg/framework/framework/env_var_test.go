// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package framework

import (
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"reflect"
	"testing"
)

func TestEnvOverride(t *testing.T) {
	src := []corev1.EnvVar{
		{
			Name:  "test1",
			Value: "value1",
		},
		{
			Name:  "test2",
			Value: "value2",
		},
	}
	dst := []corev1.EnvVar{
		{
			Name:  "test1",
			Value: "valueX",
		},
		{
			Name:  "test3",
			Value: "value3",
		},
	}
	result := EnvOverride(dst, src...)
	assert.Equal(t, 3, len(result))
	assert.Equal(t, result[0], dst[0])
	assert.Equal(t, result[1], dst[1])
	assert.Equal(t, result[2], src[1])
}

func TestGetEnvVar(t *testing.T) {
	vars := []corev1.EnvVar{
		{
			Name:  "test1",
			Value: "value1",
		},
		{
			Name:  "test2",
			Value: "value2",
		},
	}
	pos := GetEnvVar("test1", vars)
	assert.Equal(t, 0, pos)

	pos = GetEnvVar("other", vars)
	assert.Equal(t, -1, pos)
}

func Test_CreateEnvVar(t *testing.T) {
	envVar := CreateEnvVar("key", "value")
	assert.NotNil(t, envVar)
	assert.Equal(t, "key", envVar.Name)
	assert.Equal(t, "value", envVar.Value)
}

func Test_CreateSecretEnvVar(t *testing.T) {
	envVar := CreateSecretEnvVar("var", "name", "key")
	assert.NotNil(t, envVar)
	assert.Equal(t, "var", envVar.Name)
	assert.Equal(t, "key", envVar.ValueFrom.SecretKeyRef.Key)
	assert.Equal(t, "name", envVar.ValueFrom.SecretKeyRef.LocalObjectReference.Name)
}

func TestDiffEnvVar(t *testing.T) {
	type args struct {
		env1 []corev1.EnvVar
		env2 []corev1.EnvVar
	}
	tests := []struct {
		name string
		args args
		want []corev1.EnvVar
	}{
		{"Common case",
			args{
				env1: []corev1.EnvVar{{
					Name:  "Var1",
					Value: "Value1",
				}},
				env2: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}},
			},
			[]corev1.EnvVar{{
				Name:  "Var1",
				Value: "Value1",
			}},
		},
		{"A little bit more",
			args{
				env1: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}, {
					Name:  "Var3",
					Value: "Value3",
				}, {
					Name:  "Var4",
					Value: "Value4",
				}},
				env2: []corev1.EnvVar{{
					Name:  "Var1",
					Value: "Value1",
				}, {
					Name:  "Var2",
					Value: "Value2",
				}},
			},
			[]corev1.EnvVar{{
				Name:  "Var3",
				Value: "Value3",
			}, {
				Name:  "Var4",
				Value: "Value4",
			}},
		},
		{"Nothing here",
			args{
				env1: nil,
				env2: nil,
			},
			nil,
		},
		{"Nothing in there",
			args{
				env1: nil,
				env2: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}},
			},
			nil,
		},
		{"Give what you have",
			args{
				env1: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}},
				env2: nil,
			},
			[]corev1.EnvVar{{
				Name:  "Var2",
				Value: "Value2",
			}},
		},
		{"We are the same",
			args{
				env1: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}},
				env2: []corev1.EnvVar{{
					Name:  "Var2",
					Value: "Value2",
				}},
			},
			nil,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := DiffEnvVar(tt.args.env1, tt.args.env2); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("DiffEnvVar() = %v, want %v", got, tt.want)
			}
		})
	}
}
