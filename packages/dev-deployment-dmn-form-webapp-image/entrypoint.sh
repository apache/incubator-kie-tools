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

# Copying the DMN Form Webapp assets here is essential for when the container is running with the readOnlyRootFilesystem flag.
# But, just like any other directory modified during runtime, the /var/www/html must be a mounted volume in the container in this case.
cp -R /dmn-form-webapp/app/* /var/www/html

/dmn-form-webapp/image-env-to-json-linux-amd64 --directory /var/www/html --json-schema /dmn-form-webapp/EnvJson.schema.json

httpd -D FOREGROUND