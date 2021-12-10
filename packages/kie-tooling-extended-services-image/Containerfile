# Copyright 2021 Red Hat
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM ubuntu:21.10

ENV DEBIAN_FRONTEND=noninteractive
ENV DISPLAY=":99.0"

COPY dist-dev/kie_tooling_extended_services /tmp/kie_tooling_extended_services

RUN mkdir -p /root/.cache /.cache \
  && apt-get update \
  && apt-get install -qqy xvfb libgtk-3-dev libappindicator3-dev gir1.2-appindicator3-0.1 \
  && echo "#!/bin/sh\n/usr/bin/Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 &\n./tmp/kie_tooling_extended_services" > /tmp/run.sh \
  && chmod -R 775 /tmp /root/.cache /.cache

EXPOSE 21345

ENTRYPOINT [ "/tmp/run.sh" ]
