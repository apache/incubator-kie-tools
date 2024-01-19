# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Build the manager binary
FROM docker.io/library/golang:1.21.6 as builder

ARG SOURCE_DATE_EPOCH

WORKDIR /workspace
# Copy the Go Modules manifests
COPY go.mod go.mod
COPY go.sum go.sum

# Copy internal dependency
COPY api/ api/
COPY workflowproj/ workflowproj/
COPY container-builder/ container-builder/

# cache deps before building and copying source so that we don't need to re-download as much
# and so that source changes don't invalidate our downloaded layer
RUN go mod download

# Copy the go source
COPY main.go main.go
COPY controllers/ controllers/
COPY utils/ utils/
COPY version/ version/
COPY log/ log/

# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -trimpath -ldflags=-buildid= -a -o manager main.go

FROM registry.access.redhat.com/ubi9/ubi-micro:9.3-9

ARG SOURCE_DATE_EPOCH

WORKDIR /usr/local/bin

COPY --from=builder /workspace/manager /usr/local/bin/manager
# We force a timestamp to the output to guarantee a reproducible build, once we have BuildKit 0.12, this won't be needed anymore.
# The workaround to force the date format is because docker cli is expecting an int from this parameter (the timestamp).
RUN touch -d $(date '+%FT%H:%M:%S' -d @${SOURCE_DATE_EPOCH}) /usr/local/bin/manager

USER 65532:65532

ENTRYPOINT ["manager"]
