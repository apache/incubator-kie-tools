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

package v1alpha08

import (
	"context"
	"os"
	"reflect"
	"testing"

	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
	"k8s.io/apimachinery/pkg/runtime"
	"sigs.k8s.io/yaml"
)

const (
	camelCNCFWorkflow   = "testdata/camel.sw.json"
	foreachCNCFWorkflow = "testdata/foreach.sw.json"
	invalidCNCFWorkflow = "testdata/invalid.sw.json"
	camelWorkflowCR     = "testdata/sonataflow-camel.yaml"
	foreachWorkflowCR   = "testdata/sonataflow-foreach.yaml"
	invalidWorkflowCR   = "testdata/sonataflow-invalid.yaml"
)

func getCNCFWorkflow(name string) *cncfmodel.Workflow {
	workflowBytes, err := os.ReadFile(name)
	if err != nil {
		panic(err)
	}
	cncfWorkflow := &cncfmodel.Workflow{}
	err = yaml.Unmarshal(workflowBytes, cncfWorkflow)
	if err != nil {
		panic(err)
	}
	return cncfWorkflow
}

func getWorkflowCR(name string) *SonataFlow {
	crBytes, err := os.ReadFile(name)
	if err != nil {
		panic(err)
	}
	workflowCR := &SonataFlow{}
	if err = yaml.Unmarshal(crBytes, workflowCR); err != nil {
		panic(err)
	}
	return workflowCR
}

func TestFromCNCFWorkflow(t *testing.T) {
	type args struct {
		cncfWorkflow *cncfmodel.Workflow
	}
	tests := []struct {
		name    string
		args    args
		want    *SonataFlow
		wantErr bool
	}{
		{name: "Camel Flow", args: args{getCNCFWorkflow(camelCNCFWorkflow)}, wantErr: false, want: getWorkflowCR(camelWorkflowCR)},
		{name: "ForEach Flow", args: args{getCNCFWorkflow(foreachCNCFWorkflow)}, wantErr: false, want: getWorkflowCR(foreachWorkflowCR)},
		{name: "Invalid Flow", args: args{getCNCFWorkflow(invalidCNCFWorkflow)}, wantErr: false, want: getWorkflowCR(invalidWorkflowCR)},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := FromCNCFWorkflow(tt.args.cncfWorkflow, context.TODO())
			if (err != nil) != tt.wantErr {
				t.Errorf("FromCNCFWorkflow() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			wantUns, err := runtime.DefaultUnstructuredConverter.ToUnstructured(tt.want)
			if err != nil {
				t.Errorf("%v", err)
			}
			gotUns, err := runtime.DefaultUnstructuredConverter.ToUnstructured(got)
			if err != nil {
				t.Errorf("%v", err)
			}
			if !reflect.DeepEqual(gotUns, wantUns) {
				t.Errorf("FromCNCFWorkflow() got = %v, want %v", gotUns, wantUns)
			}
		})
	}
}

func TestToCNCFWorkflow(t *testing.T) {
	type args struct {
		workflowCR *SonataFlow
	}
	tests := []struct {
		name    string
		args    args
		want    *cncfmodel.Workflow
		wantErr bool
	}{
		{name: "Camel Flow", args: args{getWorkflowCR(camelWorkflowCR)}, wantErr: false, want: getCNCFWorkflow(camelCNCFWorkflow)},
		{name: "ForEach Flow", args: args{getWorkflowCR(foreachWorkflowCR)}, wantErr: false, want: getCNCFWorkflow(foreachCNCFWorkflow)},
		{name: "Invalid Flow", args: args{getWorkflowCR(invalidWorkflowCR)}, wantErr: false, want: getCNCFWorkflow(invalidCNCFWorkflow)},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := ToCNCFWorkflow(tt.args.workflowCR, context.TODO())
			if (err != nil) != tt.wantErr {
				t.Errorf("ToCNCFWorkflow() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ToCNCFWorkflow() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_sanitizeNaming(t *testing.T) {
	type args struct {
		name string
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"Success", args{name: "camel-flow"}, "camel-flow"},
		{"Starting Dash", args{name: "-camel-flow"}, "camel-flow"},
		{"All caps", args{name: "CAMEL FLOW"}, "camel-flow"},
		{"Many Dashes", args{name: "--------camel-flow"}, "camel-flow"},
		{"Weird Chars", args{name: "$%#$%$#&#$%#$%#$cm"}, "cm"},
		{"Many Chars", args{name: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque posuere nec sapien ac ultricies. Mauris id quam justo. Donec pellentesque facilisis odio eu gravida. Aliquam nisl felis, tincidunt at dignissim id, malesuada eget erat. Duis tempus sapien."}, "lorem-ipsum-dolor-sit-amet--consectetur-adipiscing-elit--quisque-posuere-nec-sapien-ac-ultricies--mauris-id-quam-justo--donec-pellentesque-facilisis-odio-eu-gravida--aliquam-nisl-felis--tincidunt-at-dignissim-id--malesuada-eget-erat--duis-tempus-sapien-"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := sanitizeNaming(tt.args.name); got != tt.want {
				t.Errorf("sanitizeNaming() = %v, want %v", got, tt.want)
			}
		})
	}
}
