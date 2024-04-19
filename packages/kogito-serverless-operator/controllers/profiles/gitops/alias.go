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

package gitops

import "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/preview"

// Aliases to preview profile package to avoid cluttering this package with references to preview profile.
// It makes easier to maintain and understand where it comes the references.

var newDeploymentReconciler = preview.NewDeploymentReconciler
var newObjectEnsurers = preview.NewObjectEnsurers

type objectEnsurers = preview.ObjectEnsurers
