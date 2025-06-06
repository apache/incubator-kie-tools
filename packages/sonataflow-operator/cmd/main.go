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

package main

import (
	"crypto/tls"
	"flag"
	"fmt"
	"os"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"k8s.io/client-go/dynamic"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/manager"

	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	"k8s.io/klog/v2/klogr"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	servingv1 "knative.dev/serving/pkg/apis/serving/v1"
	metricsserver "sigs.k8s.io/controller-runtime/pkg/metrics/server"
	"sigs.k8s.io/controller-runtime/pkg/webhook"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"

	ocputil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/openshift"

	// Import all Kubernetes client auth plugins (e.g. Azure, GCP, OIDC, etc.)
	// to ensure that exec-entrypoint and run can make use of them.
	_ "k8s.io/client-go/plugin/pkg/client/auth"

	"k8s.io/apimachinery/pkg/runtime"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/healthz"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	//+kubebuilder:scaffold:imports
)

var (
	scheme = runtime.NewScheme()
)

func init() {
	utilruntime.Must(clientgoscheme.AddToScheme(scheme))
	utilruntime.Must(operatorapi.AddToScheme(scheme))
	utilruntime.Must(sourcesv1.AddToScheme(scheme))
	utilruntime.Must(eventingv1.AddToScheme(scheme))
	utilruntime.Must(servingv1.AddToScheme(scheme))
	utilruntime.Must(prometheus.AddToScheme(scheme))
	//+kubebuilder:scaffold:scheme
}

func main() {
	var metricsAddr string
	var enableLeaderElection bool
	var probeAddr string
	var secureMetrics bool
	var enableHTTP2 bool
	var controllerCfgPath string
	klog.InitFlags(nil)
	flag.StringVar(&metricsAddr, "metrics-bind-address", ":8080", "The address the metric endpoint binds to.")
	flag.StringVar(&probeAddr, "health-probe-bind-address", ":8081", "The address the probe endpoint binds to.")
	flag.BoolVar(&enableLeaderElection, "leader-elect", false,
		"Enable leader election for controller manager. "+
			"Enabling this will ensure there is only one active controller manager.")
	flag.BoolVar(&secureMetrics, "metrics-secure", false,
		"If set the metrics endpoint is served securely")
	flag.BoolVar(&enableHTTP2, "enable-http2", false,
		"If set, HTTP/2 will be enabled for the metrics and webhook servers")
	flag.StringVar(&controllerCfgPath, "controller-cfg-path", "", "The controller config file path.")
	flag.Parse()

	manager.SetOperatorStartTime()

	ctrl.SetLogger(klogr.New().WithName(controller.ComponentName))

	// if the enable-http2 flag is false (the default), http/2 should be disabled
	// due to its vulnerabilities. More specifically, disabling http/2 will
	// prevent from being vulnerable to the HTTP/2 Stream Cancellation and
	// Rapid Reset CVEs. For more information see:
	// - https://github.com/advisories/GHSA-qppj-fm5r-hxr3
	// - https://github.com/advisories/GHSA-4374-p667-p6c8
	disableHTTP2 := func(c *tls.Config) {
		klog.V(log.I).Info("disabling http/2")
		c.NextProtos = []string{"http/1.1"}
	}

	tlsOpts := []func(*tls.Config){}
	if !enableHTTP2 {
		tlsOpts = append(tlsOpts, disableHTTP2)
	}

	webhookServer := webhook.NewServer(webhook.Options{
		TLSOpts: tlsOpts,
	})

	mgr, err := ctrl.NewManager(ctrl.GetConfigOrDie(), ctrl.Options{
		Scheme: scheme,
		Metrics: metricsserver.Options{
			BindAddress:   metricsAddr,
			SecureServing: secureMetrics,
			TLSOpts:       tlsOpts,
		},
		WebhookServer:          webhookServer,
		HealthProbeBindAddress: probeAddr,
		LeaderElection:         enableLeaderElection,
		LeaderElectionID:       "1be5e57d.kie.org",
		// LeaderElectionReleaseOnCancel defines if the leader should step down voluntarily
		// when the Manager ends. This requires the binary to immediately end when the
		// Manager is stopped, otherwise, this setting is unsafe. Setting this significantly
		// speeds up voluntary leader transitions as the new leader don't have to wait
		// LeaseDuration time first.
		//
		// In the default scaffold provided, the program ends immediately after
		// the manager stops, so would be fine to enable this option. However,
		// if you are doing or is intended to do any operation such as perform cleanups
		// after the manager stops then its usage might be unsafe.
		// LeaderElectionReleaseOnCancel: true,
	})
	if err != nil {
		klog.V(log.E).ErrorS(err, "unable to start manager")
		os.Exit(1)
	}

	// Set global assessors
	utils.SetIsOpenShift(mgr.GetConfig())
	utils.SetClient(mgr.GetClient())
	cli, err := dynamic.NewForConfig(mgr.GetConfig())
	if err != nil {
		// shouldn't fail, since config is provided by the cluster, if fails, SetIsOpenShift should probably fail before.
		panic(fmt.Sprintf("Impossible to get new dynamic client for config to support controller operations: %s", err))
	}
	utils.SetDynamicClient(cli)

	// Fail fast, we can change this behavior in the future to read from defaults instead.
	if _, err = cfg.InitializeControllersCfgAt(controllerCfgPath); err != nil {
		klog.V(log.E).ErrorS(err, "unable to read controllers configuration file")
		os.Exit(1)
	}

	// Initialize the worker used by the SonataFlow reconciliations to execute auxiliary async operations.
	manager.InitializeSFCWorker(manager.SonataFlowControllerWorkerSize)

	if err = (&controller.SonataFlowReconciler{
		Client:   mgr.GetClient(),
		Scheme:   mgr.GetScheme(),
		Config:   mgr.GetConfig(),
		Recorder: mgr.GetEventRecorderFor("workflow-controller"),
	}).SetupWithManager(mgr); err != nil {
		klog.V(log.E).ErrorS(err, "unable to create controller", "controller", "SonataFlow")
		os.Exit(1)
	}
	if err = (&controller.SonataFlowBuildReconciler{
		Client:   mgr.GetClient(),
		Scheme:   mgr.GetScheme(),
		Config:   mgr.GetConfig(),
		Recorder: mgr.GetEventRecorderFor("build-controller"),
	}).SetupWithManager(mgr); err != nil {
		klog.V(log.E).ErrorS(err, "unable to create controller", "controller", "SonataFlowBuild")
		os.Exit(1)
	}

	if err = (&controller.SonataFlowPlatformReconciler{
		Client:   mgr.GetClient(),
		Scheme:   mgr.GetScheme(),
		Reader:   mgr.GetAPIReader(),
		Config:   mgr.GetConfig(),
		Recorder: mgr.GetEventRecorderFor("platform-controller"),
	}).SetupWithManager(mgr); err != nil {
		klog.V(log.E).ErrorS(err, "unable to create controller", "controller", "SonataFlowPlatform")
		os.Exit(1)
	}
	if err = (&controller.SonataFlowClusterPlatformReconciler{
		Client:   mgr.GetClient(),
		Scheme:   mgr.GetScheme(),
		Reader:   mgr.GetAPIReader(),
		Config:   mgr.GetConfig(),
		Recorder: mgr.GetEventRecorderFor("cluster-platform-controller"),
	}).SetupWithManager(mgr); err != nil {
		klog.V(log.E).ErrorS(err, "unable to create controller", "controller", "SonataFlowClusterPlatform")
		os.Exit(1)
	}
	//+kubebuilder:scaffold:builder

	if utils.IsOpenShift() {
		ocputil.MustAddToScheme(mgr.GetScheme())
	}

	if err := mgr.AddHealthzCheck("healthz", healthz.Ping); err != nil {
		klog.V(log.E).ErrorS(err, "unable to set up health check")
		os.Exit(1)
	}
	if err := mgr.AddReadyzCheck("readyz", healthz.Ping); err != nil {
		klog.V(log.E).ErrorS(err, "unable to set up ready check")
		os.Exit(1)
	}

	klog.V(log.I).InfoS("starting manager", "version:", version.GetOperatorVersion())
	if err := mgr.Start(ctrl.SetupSignalHandler()); err != nil {
		klog.V(log.E).ErrorS(err, "problem running manager")
		os.Exit(1)
	}

}
