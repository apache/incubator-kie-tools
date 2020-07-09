# Copyright 2020 Red Hat, Inc. and/or its affiliates
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# this Dockerfile is based on https://github.com/operator-framework/community-operators/blob/master/upstream.Dockerfile
# we just changed the third line to include only our manifest in this registry.

FROM quay.io/operator-framework/upstream-registry-builder:v1.5.6 as builder
ARG PERMISSIVE_LOAD=true
COPY build/_output/operatorhub/ manifests
RUN if [ $PERMISSIVE_LOAD = "true" ] ; then ./bin/initializer --permissive -o ./bundles.db ; else ./bin/initializer -o ./bundles.db ; fi

FROM scratch
COPY --from=builder /build/bundles.db /bundles.db
COPY --from=builder /build/bin/registry-server /registry-server
COPY --from=builder /bin/grpc_health_probe /bin/grpc_health_probe
EXPOSE 50051
ENTRYPOINT ["/registry-server"]
CMD ["--database", "/bundles.db"]