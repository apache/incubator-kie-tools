// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package utils

import (
	"k8s.io/client-go/discovery"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var k8sClient client.Client
var k8sDynamicClient *dynamic.DynamicClient
var discoveryClient discovery.DiscoveryInterface

// TODO: consider refactor the internals as we progress adding features to rely on this client instead of passing it through all the functions

// GetClient default client created by the main operator's thread.
// It's safe to use since it's set when the operator main function runs.
func GetClient() client.Client {
	return k8sClient
}

// SetClient is meant for internal use only. Don't call it!
func SetClient(client client.Client) {
	k8sClient = client
}

// GetDynamicClient default dynamic client created by the main operator's thread.
// It's safe to use since it's set when the operator main function runs.
func GetDynamicClient() *dynamic.DynamicClient {
	return k8sDynamicClient
}

// SetDynamicClient is meant for internal use only. Don't call it!
func SetDynamicClient(cli *dynamic.DynamicClient) {
	k8sDynamicClient = cli
}

func GetDiscoveryClient(cfg *rest.Config) (discovery.DiscoveryInterface, error) {
	if discoveryClient == nil {
		if cli, err := discovery.NewDiscoveryClientForConfig(cfg); err != nil {
			return nil, err
		} else {
			discoveryClient = cli
		}
	}
	return discoveryClient, nil
}

func SetDiscoveryClient(cli discovery.DiscoveryInterface) {
	discoveryClient = cli
}
