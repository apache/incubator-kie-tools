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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/client/openshift"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	buildv1 "github.com/openshift/api/build/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"reflect"
	"sort"
	"strings"
)

const (
	// maxConditionsBuffer describes the max count of Conditions in the status field
	maxConditionsBuffer = 5
)

var (
	buildConditionStatus = map[buildv1.BuildPhase]api.KogitoBuildConditionType{
		buildv1.BuildPhaseError:     api.KogitoBuildFailure,
		buildv1.BuildPhaseFailed:    api.KogitoBuildFailure,
		buildv1.BuildPhaseCancelled: api.KogitoBuildFailure,
		buildv1.BuildPhaseNew:       api.KogitoBuildRunning,
		buildv1.BuildPhasePending:   api.KogitoBuildRunning,
		buildv1.BuildPhaseRunning:   api.KogitoBuildRunning,
		buildv1.BuildPhaseComplete:  api.KogitoBuildSuccessful,
	}
)

// StatusHandler ...
type StatusHandler interface {
	HandleStatusChange(instance api.KogitoBuildInterface, err error)
}

type statusHandler struct {
	*operator.Context
}

// NewStatusHandler ...
func NewStatusHandler(context *operator.Context) StatusHandler {
	return &statusHandler{
		Context: context,
	}
}

func (s *statusHandler) HandleStatusChange(instance api.KogitoBuildInterface, err error) {
	needUpdate := false
	sortConditionsByTransitionTime(instance)
	if err != nil {
		needUpdate = true
		addConditionError(instance, err)
	} else {
		if needUpdate, err = handleConditionTransition(instance, s.Client); err != nil {
			s.Log.Error(err, "Failed to update build status")
		}
	}
	trimConditions(instance)
	if needUpdate {
		if err = s.updateStatus(instance); err != nil {
			s.Log.Error(err, "Failed to update KogitoBuild")
		}
	}
}

func sortConditionsByTransitionTime(instance api.KogitoBuildInterface) {
	sort.SliceStable(instance.GetStatus().GetConditions(), func(i, j int) bool {
		firstTransitionTime := instance.GetStatus().GetConditions()[i].GetLastTransitionTime()
		secondTransitionTime := instance.GetStatus().GetConditions()[j].GetLastTransitionTime()
		return firstTransitionTime.Before(&secondTransitionTime)
	})
}

func addConditionError(instance api.KogitoBuildInterface, err error) {
	if err != nil {
		instance.GetStatus().AddCondition(v1beta1.KogitoBuildConditions{
			Type:               api.KogitoBuildFailure,
			Status:             v1.ConditionFalse,
			LastTransitionTime: metav1.Now(),
			Reason:             api.OperatorFailureReason,
			Message:            err.Error(),
		})
	}
}

func handleConditionTransition(instance api.KogitoBuildInterface, client *client.Client) (changed bool, err error) {
	if changed, err = updateBuildsStatus(instance, client); err != nil {
		return false, err
	}
	builds := &buildv1.BuildList{}
	err = kubernetes.ResourceC(client).ListWithNamespaceAndLabel(
		instance.GetNamespace(), builds,
		map[string]string{
			framework.LabelAppKey: GetApplicationName(instance),
			LabelKeyBuildType:     string(instance.GetSpec().GetType())})
	if err != nil {
		return changed, err
	}
	if len(builds.Items) > 0 {
		sort.SliceStable(builds.Items, func(i, j int) bool {
			return builds.Items[i].CreationTimestamp.After(builds.Items[j].CreationTimestamp.Time)
		})
		instance.GetStatus().SetLatestBuild(builds.Items[0].Name)
		condition := buildConditionStatus[builds.Items[0].Status.Phase]
		if condition == api.KogitoBuildFailure {
			return addCondition(instance, condition, api.BuildFailureReason, builds.Items[0].Status.Message), nil
		}
		return addCondition(instance, condition, "", builds.Items[0].Status.Message), nil
	}
	return addCondition(
		instance,
		api.KogitoBuildRunning,
		"", "") || changed, nil
}

func updateBuildsStatus(instance api.KogitoBuildInterface, client *client.Client) (changed bool, err error) {
	buildsStatus, err := openshift.BuildConfigC(client).GetBuildsStatusByLabel(
		instance.GetNamespace(),
		strings.Join([]string{
			strings.Join([]string{framework.LabelAppKey, GetApplicationName(instance)}, "="),
			strings.Join([]string{LabelKeyBuildType, string(instance.GetSpec().GetType())}, "="),
		}, ","))
	if err != nil {
		return false, err
	}
	if buildsStatus != nil && !reflect.DeepEqual(buildsStatus, instance.GetStatus().GetBuilds()) {
		instance.GetStatus().SetBuilds(buildsStatus)
		return true, nil
	}
	return false, nil
}

func addCondition(instance api.KogitoBuildInterface, condition api.KogitoBuildConditionType, reason api.KogitoBuildConditionReason, message string) bool {
	if len(instance.GetStatus().GetConditions()) == 0 ||
		instance.GetStatus().GetConditions()[len(instance.GetStatus().GetConditions())-1].GetType() != condition {
		instance.GetStatus().AddCondition(v1beta1.KogitoBuildConditions{
			Type:               condition,
			Status:             v1.ConditionTrue,
			LastTransitionTime: metav1.Now(),
			Reason:             reason,
			Message:            message,
		})
		return true
	}
	return false
}

func trimConditions(instance api.KogitoBuildInterface) {
	if len(instance.GetStatus().GetConditions()) > maxConditionsBuffer {
		low := len(instance.GetStatus().GetConditions()) - maxConditionsBuffer
		high := len(instance.GetStatus().GetConditions())
		instance.GetStatus().SetConditions(instance.GetStatus().GetConditions()[low:high])
	}
}

func (s *statusHandler) updateStatus(instance api.KogitoBuildInterface) error {
	if err := kubernetes.ResourceC(s.Client).UpdateStatus(instance); err != nil {
		return err
	}
	return nil
}
