// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package kogitoservice

// VolumeReference ...
type VolumeReference struct {
	// This must match the Name of a ConfigMap.
	Name string
	// Path within the container at which the volume should be mounted.
	MountPath string
	// FileMode ...
	FileMode *int32
	// Specify whether the Secret or its keys must be defined
	Optional *bool
}

// GetName ...
func (c *VolumeReference) GetName() string {
	return c.Name
}

// SetName ...
func (c *VolumeReference) SetName(name string) {
	c.Name = name
}

// GetMountPath ...
func (c *VolumeReference) GetMountPath() string {
	return c.MountPath
}

// SetMountPath ...
func (c *VolumeReference) SetMountPath(mountPath string) {
	c.MountPath = mountPath
}

// IsOptional ...
func (c *VolumeReference) IsOptional() *bool {
	return c.Optional
}

// SetOptional ....
func (c *VolumeReference) SetOptional(optional *bool) {
	c.Optional = optional
}

// GetFileMode ...
func (c *VolumeReference) GetFileMode() *int32 {
	return c.FileMode
}

// SetFileMode ...
func (c *VolumeReference) SetFileMode(fileMode *int32) {
	c.FileMode = fileMode
}
