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

package config

// ImageCacheMode defines whether image cache should be used for runtime image or images should be rather built manually.
type ImageCacheMode string

var (
	// UseImageCacheAlways Always use image cache
	UseImageCacheAlways = ImageCacheMode("always")
	// UseImageCacheNever don't use image cache, build always manually
	UseImageCacheNever = ImageCacheMode("never")
	// UseImageCacheIfAvailable use image cache if image is available there, otherwise build locally
	UseImageCacheIfAvailable = ImageCacheMode("if-available")
)

// IsValid returns true if image cache mode value is one of valid expected modes
func (imageCacheMode ImageCacheMode) IsValid() bool {
	switch imageCacheMode {
	case UseImageCacheAlways, UseImageCacheNever, UseImageCacheIfAvailable:
		return true
	}
	return false
}
