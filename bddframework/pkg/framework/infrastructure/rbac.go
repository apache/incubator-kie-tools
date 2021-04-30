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

package infrastructure

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// RuntimeServiceAccountName ...
	RuntimeServiceAccountName = "kogito-service-viewer"
	roleName                  = "kogito-service-viewer"
	roleBindingName           = "kogito-service-viewer"
	roleAPIGroup              = "rbac.authorization.k8s.io"
)

var serviceViewerRoleVerbs = []string{"list", "get", "watch", "update", "patch"}
var serviceViewerRoleAPIGroups = []string{""}
var serviceViewerRoleResources = []string{"services", "configmaps"}

// RBACHandler ...
type RBACHandler interface {
	SetupRBAC(namespace string) (err error)
}

type rbacHandler struct {
	operator.Context
}

// NewRBACHandler ...
func NewRBACHandler(context operator.Context) RBACHandler {
	return &rbacHandler{
		context,
	}
}

func (r *rbacHandler) SetupRBAC(namespace string) (err error) {
	// create service viewer role
	if err = kubernetes.ResourceC(r.Client).CreateIfNotExists(getServiceViewerRole(namespace)); err != nil {
		r.Log.Error(err, "Fail to create role for service viewer")
		return
	}

	// create service viewer service account
	if err = kubernetes.ResourceC(r.Client).CreateIfNotExists(getServiceViewerServiceAccount(namespace)); err != nil {
		r.Log.Error(err, "Fail to create service account for service viewer")
		return
	}

	// create service viewer rolebinding
	if err = kubernetes.ResourceC(r.Client).CreateIfNotExists(getServiceViewerRoleBinding(namespace)); err != nil {
		r.Log.Error(err, "Fail to create role binding for service viewer")
		return
	}
	return
}

func getServiceViewerServiceAccount(namespace string) kubernetes.ResourceObject {
	return &v1.ServiceAccount{
		ObjectMeta: v12.ObjectMeta{
			Name:      RuntimeServiceAccountName,
			Namespace: namespace,
		},
	}
}

func getServiceViewerRole(namespace string) kubernetes.ResourceObject {
	return &rbac.Role{
		ObjectMeta: v12.ObjectMeta{
			Name:      roleName,
			Namespace: namespace,
		},
		Rules: []rbac.PolicyRule{
			{
				Verbs:     serviceViewerRoleVerbs,
				APIGroups: serviceViewerRoleAPIGroups,
				Resources: serviceViewerRoleResources,
			},
		},
	}
}
func getServiceViewerRoleBinding(namespace string) kubernetes.ResourceObject {
	return &rbac.RoleBinding{
		ObjectMeta: v12.ObjectMeta{
			Name:      roleBindingName,
			Namespace: namespace,
		},
		Subjects: []rbac.Subject{
			{
				Kind: "ServiceAccount",
				Name: RuntimeServiceAccountName,
			},
		},
		RoleRef: rbac.RoleRef{
			APIGroup: roleAPIGroup,
			Name:     roleName,
			Kind:     "Role",
		},
	}
}
