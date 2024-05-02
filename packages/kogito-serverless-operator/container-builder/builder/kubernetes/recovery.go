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

package kubernetes

import (
	"context"
	"time"

	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"

	"github.com/jpillora/backoff"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func newErrorRecoveryAction() Action {
	// TODO: externalize options
	return &errorRecoveryAction{
		backOff: backoff.Backoff{
			Min:    5 * time.Second,
			Max:    1 * time.Minute,
			Factor: 2,
			Jitter: false,
		},
	}
}

type errorRecoveryAction struct {
	baseAction
	backOff backoff.Backoff
}

func (action *errorRecoveryAction) Name() string {
	return "error-recovery"
}

func (action *errorRecoveryAction) CanHandle(build *api.ContainerBuild) bool {
	return build.Status.Phase == api.ContainerBuildPhaseFailed
}

func (action *errorRecoveryAction) Handle(ctx context.Context, build *api.ContainerBuild) (*api.ContainerBuild, error) {
	if build.Status.Failure == nil {
		build.Status.Failure = &api.ContainerBuildFailure{
			Reason: build.Status.Error,
			Time:   metav1.Now(),
			Recovery: api.ContainerBuildFailureRecovery{
				Attempt:    0,
				AttemptMax: 5,
			},
		}
		return build, nil
	}

	if build.Status.Failure.Recovery.Attempt >= build.Status.Failure.Recovery.AttemptMax {
		build.Status.Phase = api.ContainerBuildPhaseError
		return build, nil
	}

	lastAttempt := build.Status.Failure.Recovery.AttemptTime.Time
	if lastAttempt.IsZero() {
		lastAttempt = build.Status.Failure.Time.Time
	}

	elapsed := time.Since(lastAttempt).Seconds()
	elapsedMin := action.backOff.ForAttempt(float64(build.Status.Failure.Recovery.Attempt)).Seconds()

	if elapsed < elapsedMin {
		return nil, nil
	}

	build.Status.Phase = api.ContainerBuildPhaseInitialization
	build.Status.Failure.Recovery.Attempt++
	build.Status.Failure.Recovery.AttemptTime = metav1.Now()

	klog.V(log.I).InfoS("Recovery attempt",
		"attempt", build.Status.Failure.Recovery.Attempt,
		"attemptMax", build.Status.Failure.Recovery.AttemptMax,
	)

	return build, nil
}
