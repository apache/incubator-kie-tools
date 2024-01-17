#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

export image_id=$1
export image_full_tag=$2

export image_full_name=${image_full_tag%%:*}
export image_full_name=${image_full_name%%@sha256*} # Remove `@sha256` if needed
export image_registry_name=${image_full_name%/*}
export image_name=${image_full_name##*/}
export image_registry=${image_registry_name%/*}
export image_namespace=${image_registry_name##*/}

export image_descriptor_filename=${image_id}-image.yaml

export community_image_id=${image_id/logic-/kogito-}
export community_image_id=${community_image_id/-rhel8/}
