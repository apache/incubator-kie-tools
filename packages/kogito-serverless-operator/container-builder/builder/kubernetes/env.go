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
	"fmt"

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
)

// FromEnvToArgs converts an EnvVar array into an args string slice. E.g. name=value,name=value
func FromEnvToArgs(c client.Client, ns string, envVars ...v1.EnvVar) ([]string, error) {
	args := make([]string, 0)
	for _, env := range envVars {
		if env.ValueFrom == nil {
			args = append(args, fmt.Sprintf("%s=%s", env.Name, env.Value))
		} else {
			if env.ValueFrom.ConfigMapKeyRef != nil {
				cm := &v1.ConfigMap{}
				if err := c.Get(context.TODO(), types.NamespacedName{Name: env.ValueFrom.ConfigMapKeyRef.Name, Namespace: ns}, cm); err != nil {
					t := true
					if errors.IsNotFound(err) && env.ValueFrom.ConfigMapKeyRef.Optional == &t {
						continue
					}
					return nil, err
				}
				args = append(args, fmt.Sprintf("%s=%s", env.Name, cm.Data[env.ValueFrom.ConfigMapKeyRef.Key]))
				continue
			}
			if env.ValueFrom.SecretKeyRef != nil {
				secret := &v1.Secret{}
				if err := c.Get(context.TODO(), types.NamespacedName{Name: env.ValueFrom.SecretKeyRef.Name, Namespace: ns}, secret); err != nil {
					t := true
					if errors.IsNotFound(err) && env.ValueFrom.SecretKeyRef.Optional == &t {
						continue
					}
					return nil, err
				}
				args = append(args, fmt.Sprintf("%s=%s", env.Name, secret.Data[env.ValueFrom.SecretKeyRef.Key]))
				continue
			}
			return nil, fmt.Errorf("can't convert to args the env var %s on namespace %s. Only Secrets and ConfigMaps are supported", env.Name, ns)
		}
	}
	return args, nil
}
