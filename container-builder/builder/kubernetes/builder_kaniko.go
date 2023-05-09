package kubernetes

import (
	"path"

	corev1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"
)

type kanikoScheduler struct {
	*scheduler
	KanikoTask *api.KanikoTask
}

type kanikoSchedulerHandler struct {
}

var _ schedulerHandler = &kanikoSchedulerHandler{}

func (k kanikoSchedulerHandler) CreateScheduler(info BuilderInfo, buildCtx buildContext) Scheduler {
	kanikoTask := api.KanikoTask{
		BaseTask: api.BaseTask{Name: "KanikoTask"},
		PublishTask: api.PublishTask{
			ContextDir: path.Join("/builder", info.BuildUniqueName, "context"),
			BaseImage:  info.Platform.Spec.BaseImage,
			Image:      info.FinalImageName,
			Registry:   info.Platform.Spec.Registry,
		},
		Cache: api.KanikoTaskCache{},
	}

	buildCtx.Build = &api.Build{
		Spec: api.BuildSpec{
			Tasks:    []api.Task{{Kaniko: &kanikoTask}},
			Strategy: api.BuildStrategyPod,
			Timeout:  *info.Platform.Spec.Timeout,
		},
	}
	buildCtx.Build.Name = info.BuildUniqueName
	buildCtx.Build.Namespace = info.Platform.Namespace

	sched := &kanikoScheduler{
		&scheduler{
			builder: builder{
				L:       log.WithName(util.ComponentName),
				Context: buildCtx,
			},
			Resources: make([]resource, 0),
		},
		&kanikoTask,
	}
	// we hold our own reference for the default methods to return the right object
	sched.Scheduler = sched
	return sched
}

func (k kanikoSchedulerHandler) CanHandle(info BuilderInfo) bool {
	return info.Platform.Spec.BuildStrategy == api.BuildStrategyPod && info.Platform.Spec.PublishStrategy == api.PlatformBuildPublishStrategyKaniko
}

func (sk *kanikoScheduler) WithProperty(property BuilderProperty, object interface{}) Scheduler {
	if property == KanikoCache {
		sk.KanikoTask.Cache = object.(api.KanikoTaskCache)
	}
	return sk
}

func (sk *kanikoScheduler) WithResourceRequirements(res corev1.ResourceRequirements) Scheduler {
	sk.KanikoTask.Resources = res
	return sk
}

func (sk *kanikoScheduler) WithAdditionalArgs(flags []string) Scheduler {
	sk.KanikoTask.AdditionalFlags = flags
	return sk
}

func (sk *kanikoScheduler) Schedule() (*api.Build, error) {
	// verify if we really need this
	for _, task := range sk.builder.Context.Build.Spec.Tasks {
		if task.Kaniko != nil {
			task.Kaniko = sk.KanikoTask
			break
		}
	}
	return sk.scheduler.Schedule()
}
