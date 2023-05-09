package kubernetes

import (
	"context"
	"fmt"

	corev1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"
)

type BuilderProperty string

const KanikoCache BuilderProperty = "kaniko-cache"

type BuilderInfo struct {
	FinalImageName  string
	BuildUniqueName string
	Platform        api.PlatformBuild
}

type resource struct {
	Target  string
	Content []byte
}

type buildContext struct {
	client.Client
	C         context.Context
	Build     *api.Build
	BaseImage string
}

type builder struct {
	L       log.Logger
	Context buildContext
}

type scheduler struct {
	Scheduler
	builder   builder
	Resources []resource
}

var _ Scheduler = &scheduler{}
var _ Builder = &builder{}

// available schedulers, add them in priority order
var schedulers = map[string]schedulerHandler{
	"kaniko": &kanikoSchedulerHandler{},
}

// Scheduler provides an interface to add resources and schedule a new build
type Scheduler interface {
	// WithResource the actual file/resource to add to the builder. Might be called multiple times.
	WithResource(target string, content []byte) Scheduler
	WithClient(client client.Client) Scheduler
	// WithResourceRequirements Kubernetes resource requirements to be passed to the underlying builder if necessary. For example, a builder pod might require specific resources underneath.
	WithResourceRequirements(res corev1.ResourceRequirements) Scheduler
	// WithAdditionalArgs array of strings to pass to the underlying builder. For example "--myarg=myvalue" or "MY_ENV=MY_VALUE". The args are passed separated by spaces.
	WithAdditionalArgs(args []string) Scheduler
	// WithProperty specialized property known by inner implementations for additional properties to configure the underlying builder
	WithProperty(property BuilderProperty, object interface{}) Scheduler
	Schedule() (*api.Build, error)
}

type Builder interface {
	WithClient(client client.Client) Builder
	CancelBuild() (*api.Build, error)
	Reconcile() (*api.Build, error)
}

type schedulerHandler interface {
	CreateScheduler(info BuilderInfo, buildCtx buildContext) Scheduler
	CanHandle(info BuilderInfo) bool
}

func FromBuild(build *api.Build) Builder {
	return &builder{
		L: log.WithName(util.ComponentName),
		Context: buildContext{
			Build: build,
			C:     context.TODO(),
		},
	}
}

// NewBuild is the API entry for the Builder. Create a new Build instance based on PlatformBuild.
func NewBuild(info BuilderInfo) Scheduler {
	ctx := buildContext{
		BaseImage: info.Platform.Spec.BaseImage,
		C:         context.TODO(),
	}

	for _, v := range schedulers {
		if v.CanHandle(info) {
			return v.CreateScheduler(info, ctx)
		}
	}
	panic(fmt.Errorf("BuildStrategy %s with PublishStrategy %s is not supported", info.Platform.Spec.BuildStrategy, info.Platform.Spec.PublishStrategy))
}

func (s *scheduler) WithClient(client client.Client) Scheduler {
	s.builder.WithClient(client)
	return s.Scheduler
}

func (s *scheduler) WithResource(target string, content []byte) Scheduler {
	s.Resources = append(s.Resources, resource{target, content})
	return s.Scheduler
}

func (s *scheduler) WithResourceRequirements(res corev1.ResourceRequirements) Scheduler {
	// no default implementation.
	return s.Scheduler
}

func (s *scheduler) WithAdditionalArgs(args []string) Scheduler {
	// no default implementation.
	return s.Scheduler
}

func (s *scheduler) WithProperty(property BuilderProperty, object interface{}) Scheduler {
	// no default implementation
	return s.Scheduler
}

// Schedule schedules a new build in the platform
func (s *scheduler) Schedule() (*api.Build, error) {
	// TODO: create a handler to mount the resources according to the platform/context options (for now we only have CM, PoC level)
	if err := mountResourcesWithConfigMap(&s.builder.Context, &s.Resources); err != nil {
		return nil, err
	}
	return s.builder.Reconcile()
}

func (b *builder) WithClient(client client.Client) Builder {
	b.Context.Client = client
	return b
}

// Reconcile idempotent build flow control.
// Can be called many times to check/update the current status of the build instance, indexed by the Platform and Build Name.
func (b *builder) Reconcile() (*api.Build, error) {
	var actions []Action
	switch b.Context.Build.Spec.Strategy {
	case api.BuildStrategyPod:
		// build the action flow:
		actions = []Action{
			newInitializePodAction(),
			newScheduleAction(),
			newMonitorPodAction(),
			newErrorRecoveryAction(),
		}
	}

	target := b.Context.Build.DeepCopy()

	for _, a := range actions {
		a.InjectLogger(b.L)
		a.InjectClient(b.Context.Client)

		if a.CanHandle(target) {
			b.L.Infof("Invoking action %s", a.Name())
			newTarget, err := a.Handle(b.Context.C, target)
			if err != nil {
				b.L.Errorf(err, "Failed to invoke action %s", a.Name())
				return nil, err
			}

			if newTarget != nil {
				if newTarget.Status.Phase != target.Status.Phase {
					b.L.Info(
						"state transition",
						"phase-from", target.Status.Phase,
						"phase-to", newTarget.Status.Phase,
					)
				}

				target = newTarget
			}
			break
		}
	}

	return target, nil
}

func (b *builder) CancelBuild() (*api.Build, error) {
	//TODO implement me
	panic("implement me")
}
