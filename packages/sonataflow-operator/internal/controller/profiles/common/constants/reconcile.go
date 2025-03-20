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

package constants

import "time"

const (
	RequeueAfterFailure          = 3 * time.Minute
	RequeueAfterFollowDeployment = 5 * time.Second
	RequeueAfterIsRunning        = 1 * time.Minute
	// RecoverDeploymentErrorRetries how many times the operator should try to recover from a failure before giving up
	RecoverDeploymentErrorRetries = 3
	// RequeueRecoverDeploymentErrorInterval interval between recovering from failures
	RequeueRecoverDeploymentErrorInterval = RecoverDeploymentErrorInterval * time.Minute
	RecoverDeploymentErrorInterval        = 10
	DefaultHTTPWorkflowPortInt            = 8080
	// MaxWorkflowFinalizerAttempts how many times the operator will try to execute a SonataFlow CRD finalizer.
	MaxWorkflowFinalizerAttempts = 3
	// WorkflowFinalizerRetryInterval interval between SonataFlow CRD finalizer execution attempts.
	WorkflowFinalizerRetryInterval = 5 * time.Second
	// WorkflowFinalizerSchedulingRetryInterval interval for the operator to retry to schedule a failing finalizer scheduling.
	WorkflowFinalizerSchedulingRetryInterval = 5 * time.Second
	// EventDeliveryTimeout delivery timeout for the cloud events produced by the operator.
	EventDeliveryTimeout = 30 * time.Second
)
