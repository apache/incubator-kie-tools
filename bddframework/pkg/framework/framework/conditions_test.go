// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	v1 "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"reflect"
	"testing"
	"time"
)

func TestGetLatestDeploymentCondition(t *testing.T) {
	now := metav1.Now()
	later := metav1.NewTime(metav1.Now().Add(time.Second * 2))
	type args struct {
		conditions []v1.DeploymentCondition
	}
	tests := []struct {
		name string
		args args
		want *v1.DeploymentCondition
	}{
		{
			"Empty conditions",
			args{nil},
			nil,
		},
		{
			"Only one condition",
			args{[]v1.DeploymentCondition{
				{LastUpdateTime: now},
			}},
			&v1.DeploymentCondition{
				LastUpdateTime: now,
			},
		},
		{
			"Two conditions",
			args{[]v1.DeploymentCondition{
				{LastUpdateTime: now},
				{LastUpdateTime: later},
			}},
			&v1.DeploymentCondition{
				LastUpdateTime: later,
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := GetLatestDeploymentCondition(tt.args.conditions); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("GetLatestDeploymentCondition() = %v, want %v", got, tt.want)
			}
		})
	}
}
