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

package v1

// VolumeReference represents the source of a volume to mount.
type VolumeReference struct {
	// This must match the Name of a ConfigMap.
	Name string `json:"name" protobuf:"bytes,1,opt,name=name"`
	// Path within the container at which the volume should be mounted.  Must
	// not contain ':'. Default mount path is /home/kogito/config
	// +optional
	MountPath string `json:"mountPath,omitempty" protobuf:"bytes,3,opt,name=mountPath"`
	// Permission on the file mounted as volume on deployment.
	// Must be an octal value between 0000 and 0777 or a decimal value between 0 and 511.
	// YAML accepts both octal and decimal values, JSON requires decimal values
	// for mode bits. Defaults to 0644.
	// +optional
	FileMode *int32 `json:"fileMode,omitempty" protobuf:"bytes,4,opt,name=fileMode"`
	// Specify whether the Secret or its keys must be defined
	// +optional
	Optional *bool `json:"optional,omitempty" protobuf:"varint,5,opt,name=optional"`
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
