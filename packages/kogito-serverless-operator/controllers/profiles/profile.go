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

package profiles

import (
	"context"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/workflowproj"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
)

// ProfileReconciler is the public interface to have access to this package and perform the actual reconciliation flow.
//
// There are a few concepts in this package that you need to understand before attempting to maintain it:
//
// 1. ProfileReconciler: it's the main interface that internal structs implement via the baseReconciler.
// Every profile must embed the baseReconciler.
//
// 2. stateSupport: is a struct with a few support objects passed around the reconciliation states like the client and logger.
//
// 3. reconciliationStateMachine: is a struct within the ProfileReconciler that do the actual reconciliation.
// Each part of the reconciliation algorithm is a ReconciliationState that will be executed based on the ReconciliationState.CanReconcile call.
//
// 4. ReconciliationState: is where your business code should be focused on. Each state should react to a specific operatorapi.SonataFlowConditionType.
// The least conditions your state handles, the better.
// The ReconciliationState can provide specific code that will only be triggered if the workflow is in that specific condition.
//
// 5. objectCreator: are functions to create a specific Kubernetes object based on a given workflow instance. This function should return the desired default state.
//
// 6. mutateVisitor: is a function that states can pass to defaultObjectEnsurer that will be applied to a given live object during the reconciliation cycle.
// For example, if you wish to guarantee that an image in a specific container in the Deployment that you control and own won't change, make sure that your
// mutate function guarantees that.
//
// 7. defaultObjectEnsurer: is a struct for a given objectCreator to control the reconciliation and merge conditions to an object.
// A ReconciliationState may or may not have one or more ensurers. Depends on their role. There are states that just read objects, so no need to keep their desired state.
//
// See the already implemented reconciliation profiles to have a better understanding.
//
// While debugging, focus on the ReconciliationState(s), not in the profile implementation since the base algorithm is the same for every profile.
type ProfileReconciler interface {
	Reconcile(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, error)
	GetProfile() metadata.ProfileType
}

// ReconciliationState is an interface implemented internally by different reconciliation algorithms to perform the adequate logic for a given workflow profile
type ReconciliationState interface {
	// CanReconcile checks if this state can perform its reconciliation task
	CanReconcile(workflow *operatorapi.SonataFlow) bool
	// Do perform the reconciliation task. It returns the controller result, the objects updated, and an error if any.
	// Objects can be nil if the reconciliation state doesn't perform any updates in any Kubernetes object.
	Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error)
	// PostReconcile performs the actions to perform after the reconciliation that are not mandatory
	PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error
}

// IsDevProfile is an alias for workflowproj.IsDevProfile
var IsDevProfile = workflowproj.IsDevProfile
