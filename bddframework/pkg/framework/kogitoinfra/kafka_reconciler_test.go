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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	"github.com/stretchr/testify/assert"
	"reflect"
	"testing"
	time "time"
)

func Test_mustParseKafkaTransition(t *testing.T) {
	timeOk, err := time.Parse(v1beta2.KafkaLastTransitionTimeLayout, "2020-10-14T19:07:20.459925Z")
	assert.NoError(t, err)
	timeUnsafe, err := time.Parse(v1beta2.KafkaLastTransitionTimeLayout, "2020-10-14T19:08:05Z")
	assert.NoError(t, err)
	type args struct {
		transitionTime string
	}
	tests := []struct {
		name  string
		args  args
		want  *time.Time
		want1 bool
	}{
		{"Safe Date", args{"2020-10-14T19:07:20.459925Z"}, &timeOk, true},
		{"Not Safe Date", args{"2020-10-14T19:08:05+0000"}, &timeUnsafe, true},
	}
	r := kafkaInfraReconciler{}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 := r.mustParseKafkaTransition(tt.args.transitionTime)
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("mustParseKafkaTransition() got = %v, want %v", got, tt.want)
			}
			if got1 != tt.want1 {
				t.Errorf("mustParseKafkaTransition() got1 = %v, want %v", got1, tt.want1)
			}
		})
	}
}
