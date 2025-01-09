<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# DashBuilder

DashBuilder is a general purpose dashboard and reporting web app which allows for:

- Visual configuration and personalization of dashboards
- Support for different types of visualizations using several charting libraries
- Full featured editor for the definition of chart visualizations
- Definition of interactive report tables
- Data extraction from external systems, through different protocols
- Support for both analytics and real-time dashboards

Licensed under the Apache License, Version 2.0

For further information, please visit the project web site <a href="http://dashbuilder.org" target="_blank">dashbuilder.org</a>

# Architecture

- Not tied to any chart rendering technology. Pluggable renderers and components
- No tied to any data storage.
- Ability to read data from: CSV files, Databases, Elastic Search, Prometheus, Kafka orJava generators.
- Decoupled client & server layers. Ability to build pure lightweight client dashboards.
- Ability to push & handle data sets on client for better performance.
- Based on <a href="http://www.uberfireframework.org" target="_blank">Uberfire</a>, a framework for building rich workbench styled apps on the web.
- Cloud-native Runtime environment.

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
