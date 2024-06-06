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

## Install and configure a Tekton pipeline on Openshift

1. Install Red Hat OpenShift Pipelines on `latest` channel from the Operator Hub using the Openshift UI

2. If you want interact via cli with the pipeline you can install locally Tekton cli  
   To interact with the pipelines, you can download from the details of the operator installed (i.e. crc link) :  
   https://tkn-cli-serve-openshift-pipelines.apps-crc.testing/tkn/tkn-linux-amd64.tar.gz  
   The version proposed by the Operator is correctly aligned version with the tekton version.

3. If isn't yet created, create the project `sonataflow-operator-system`

```sh
oc new-project sonataflow-operator-system
```

4. Install the Tekton `kubernetes-actions` task

```sh
kubectl apply -f https://api.hub.tekton.dev/v1/resource/tekton/task/kubernetes-actions/0.2/raw
```

5. Apply the cluster role and cluster role binding

```sh
kubectl create -f tekton/role/cluster_role.yaml
kubectl create -f tekton/role/cluster_role_binding.yaml
```

6. Create the pipeline

```sh
kubectl apply -f tekton/pipeline/kogito_serverless_operator_pipeline.yaml
```

7. Create a pipeline run

```sh
kubectl apply -f tekton/pipeline/kogito_serverless_operator_pipeline_run.yaml
```

or with the Tekton cli:

```sh
tkn pipeline start sonataflow-operator-pipeline \
  -w name=shared-workspace,volumeClaimTemplateFile=https://raw.githubusercontent.com/apache/incubator-kie-sonataflow-operator/main/tekton/volume/persistent_volume.yaml \
  -p deployment-name=sonataflow-operator \
  -p git-url=https://github.com/apache/incubator-kie-tools/packages/sonataflow-operator.git \
  -p git-revision=main \
  -p IMAGE='image-registry.openshift-image-registry.svc:5000/sonataflow-operator-system/sonataflow-operator:latest' \
  --use-param-defaults
```

8. Check the Pipeline execution

Open the Pipeline menu under the namespace/project `sonataflow-operator-system`
or with the Tekton cli (use the pipeline run id):

```sh
tkn pipelinerun logs sonataflow-operator-pipeline-run-<id> -f -n <your-namespace>
```

### How to see the content of the workspace

1. Create the task `show_workspace_content`

```sh
kubectl apply -f tekton/task/show_workspace_content.yaml
```

2. Add the task `show-workspace` in the pipeline after the `fetch-repository` or `build-image`

### How to redeploy

Go to the pipeline runs and ask for a rerun of a previous pipeline run

## Trigger the pipeline on GithubEvents

1. Create the trigger binding

```sh
oc create -f tekton/trigger/trigger_binding.yaml
```

2. Create the trigger template

```sh
oc create -f tekton/trigger/trigger_template.yaml
```

3. Create the trigger resource

```sh
oc create -f tekton/trigger/trigger_resource.yaml
```

4. Add a label to enable the secure HTTPS connection to the Eventlistener resource

```sh
oc label namespace sonataflow-operator-system operator.tekton.dev/enable-annotation=enabled
```

5. Create the Event listener trigger

```sh
oc create -f tekton/trigger/trigger_event_listener.yaml
```

6. Create a route with the re-encrypted TLS termination

```sh
oc create route reencrypt --service=el-sonataflow-operator-webhook --cert=tls.crt --key=tls.key --ca-cert=ca.crt --hostname=<hostname>
```

7. Check the webhook

```sh
tkn el -n sonataflow-operator-pipeline ls
```

```sh
kubectl get pods,svc -n sonataflow-operator-pipeline -l eventlistener=sonataflow-operator-webhook
```

8. Add a webhook in your github/gitlab repo with the url of the listener on openshift

9. Authenticating pipelines using git secret
   https://docs.openshift.com/container-platform/4.12/cicd/pipelines/authenticating-pipelines-using-git-secret.html
