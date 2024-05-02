// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package utils

import "sigs.k8s.io/controller-runtime/pkg/client"

var k8sClient client.Client

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
