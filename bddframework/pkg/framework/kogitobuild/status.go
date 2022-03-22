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

package kogitobuild

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	buildv1 "github.com/openshift/api/build/v1"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sort"
	"strings"
)

var (
	buildConditionReason = map[buildv1.BuildPhase]api.KogitoBuildConditionReason{
		buildv1.BuildPhaseError:     api.BuildPhaseErrorReason,
		buildv1.BuildPhaseFailed:    api.BuildPhaseFailedReason,
		buildv1.BuildPhaseCancelled: api.BuildPhaseCancelledReason,
		buildv1.BuildPhaseNew:       api.BuildPhaseNewReason,
		buildv1.BuildPhasePending:   api.BuildPhasePendingReason,
		buildv1.BuildPhaseRunning:   api.BuildPhaseRunningReason,
		buildv1.BuildPhaseComplete:  api.BuildPhaseCompleteReason,
	}
)

// StatusHandler ...
type StatusHandler interface {
	HandleStatusChange(instance api.KogitoBuildInterface, err error)
}

type statusHandler struct {
	operator.Context
	buildHandler manager.KogitoBuildHandler
}

// NewStatusHandler ...
func NewStatusHandler(context operator.Context, buildHandler manager.KogitoBuildHandler) StatusHandler {
	return &statusHandler{
		Context:      context,
		buildHandler: buildHandler,
	}
}

func (s *statusHandler) HandleStatusChange(instance api.KogitoBuildInterface, err error) {
	if instance.GetStatus().GetConditions() == nil {
		instance.GetStatus().SetConditions(&[]metav1.Condition{})
	}
	if err != nil {
		s.setFailedConditions(instance.GetStatus().GetConditions(), api.OperatorFailureReason, err.Error())
	} else {
		if err = s.handleConditionTransition(instance); err != nil {
			s.Log.Error(err, "Failed to update build status")
		}
	}
	if err = s.updateStatus(instance); err != nil {
		s.Log.Error(err, "Failed to update KogitoBuild")
	}
}

// newSuccessfulCondition ...
func (s *statusHandler) newSuccessfulCondition(status metav1.ConditionStatus, reason api.KogitoBuildConditionReason) metav1.Condition {
	return metav1.Condition{
		Type:   string(api.KogitoBuildSuccessful),
		Status: status,
		Reason: string(reason),
	}
}

// newRunningCondition ...
func (s *statusHandler) newRunningCondition(status metav1.ConditionStatus, reason api.KogitoBuildConditionReason) metav1.Condition {
	return metav1.Condition{
		Type:   string(api.KogitoBuildRunning),
		Status: status,
		Reason: string(reason),
	}
}

// NewFailedCondition ...
func (s *statusHandler) newFailedCondition(reason api.KogitoBuildConditionReason, message string) metav1.Condition {
	return metav1.Condition{
		Type:    string(api.KogitoBuildFailure),
		Status:  metav1.ConditionTrue,
		Reason:  string(reason),
		Message: message,
	}
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setSuccessful(conditions *[]metav1.Condition, status metav1.ConditionStatus, reason api.KogitoBuildConditionReason) {
	successfulCondition := s.newSuccessfulCondition(status, reason)
	meta.SetStatusCondition(conditions, successfulCondition)
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setRunning(conditions *[]metav1.Condition, status metav1.ConditionStatus, reason api.KogitoBuildConditionReason) {
	runningCondition := s.newRunningCondition(status, reason)
	meta.SetStatusCondition(conditions, runningCondition)
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setFailed(conditions *[]metav1.Condition, reason api.KogitoBuildConditionReason, message string) {
	failedCondition := s.newFailedCondition(reason, message)
	meta.SetStatusCondition(conditions, failedCondition)
}

func (s *statusHandler) handleConditionTransition(instance api.KogitoBuildInterface) error {
	err := s.updateBuildsStatus(instance)
	if err != nil {
		return err
	}
	builds := &buildv1.BuildList{}
	err = kubernetes.ResourceC(s.Client).ListWithNamespaceAndLabel(
		instance.GetNamespace(), builds,
		map[string]string{
			framework.LabelAppKey: GetApplicationName(instance),
			LabelKeyBuildType:     string(instance.GetSpec().GetType())})
	if err != nil {
		return err
	}
	if len(builds.Items) > 0 {
		sort.SliceStable(builds.Items, func(i, j int) bool {
			return builds.Items[i].CreationTimestamp.After(builds.Items[j].CreationTimestamp.Time)
		})
		latestBuild := builds.Items[0]
		instance.GetStatus().SetLatestBuild(latestBuild.Name)
		s.addCondition(latestBuild, instance.GetStatus().GetConditions())
		return nil
	}
	s.setRunningConditions(instance.GetStatus().GetConditions(), api.BuildNotStartedReason)
	return nil
}

func (s *statusHandler) updateBuildsStatus(instance api.KogitoBuildInterface) (err error) {
	buildConfig := NewBuildHandler(s.Context, s.buildHandler)
	buildsStatus, err := buildConfig.GetBuildsStatusByLabel(
		instance.GetNamespace(),
		strings.Join([]string{
			strings.Join([]string{framework.LabelAppKey, GetApplicationName(instance)}, "="),
			strings.Join([]string{LabelKeyBuildType, string(instance.GetSpec().GetType())}, "="),
		}, ","))
	if err != nil {
		return err
	}
	instance.GetStatus().SetBuilds(buildsStatus)
	return nil
}

func (s *statusHandler) addCondition(build buildv1.Build, conditions *[]metav1.Condition) {
	conditionReason := buildConditionReason[build.Status.Phase]
	switch build.Status.Phase {
	case buildv1.BuildPhaseFailed, buildv1.BuildPhaseCancelled:
		s.setFailedConditions(conditions, conditionReason, build.Status.Message)
	case buildv1.BuildPhaseNew, buildv1.BuildPhasePending, buildv1.BuildPhaseRunning:
		s.setRunningConditions(conditions, conditionReason)
	case buildv1.BuildPhaseComplete:
		s.setSuccessfulConditions(conditions, conditionReason)
	}
}

func (s *statusHandler) setFailedConditions(conditions *[]metav1.Condition, reason api.KogitoBuildConditionReason, message string) {
	s.setFailed(conditions, reason, message)
	s.setRunning(conditions, metav1.ConditionFalse, reason)
	s.setSuccessful(conditions, metav1.ConditionFalse, reason)
}

func (s *statusHandler) setRunningConditions(conditions *[]metav1.Condition, reason api.KogitoBuildConditionReason) {
	s.setRunning(conditions, metav1.ConditionTrue, reason)
	s.setSuccessful(conditions, metav1.ConditionFalse, reason)
}

func (s *statusHandler) setSuccessfulConditions(conditions *[]metav1.Condition, reason api.KogitoBuildConditionReason) {
	s.setRunning(conditions, metav1.ConditionFalse, reason)
	s.setSuccessful(conditions, metav1.ConditionTrue, reason)
}

func (s *statusHandler) updateStatus(instance api.KogitoBuildInterface) error {
	if err := kubernetes.ResourceC(s.Client).UpdateStatus(instance); err != nil {
		return err
	}
	return nil
}
