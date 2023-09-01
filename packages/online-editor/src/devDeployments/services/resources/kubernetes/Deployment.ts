export const createDeploymentYaml = `
kind: Deployment
apiVersion: apps/v1
metadata:
  name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
  namespace: \${{ devDeployment.kubernetes.namespace }}
  labels:
    app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/component: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/instance: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    app.kubernetes.io/part-of: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    \${{ devDeployment.labels.createdBy }}: kie-tools
  annotations:
    \${{ devDeployment.annotations.uri }}: \${{ devDeployment.workspace.resourceName }}
    \${{ devDeployment.annotations.workspaceId }}: \${{ devDeployment.workspace.id }}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
  template:
    metadata:
      labels:
        app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
        deploymentconfig: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    spec:
      containers:
        - name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
          image: \${{ devDeployment.defaultContainerImageUrl }}
          ports:
            - containerPort: 8080
              protocol: TCP
          env:
            - name: BASE_URL
              value: http://localhost/\${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
            - name: QUARKUS_PLATFORM_VERSION
              value: 2.16.7.Final
            - name: KOGITO_RUNTIME_VERSION
              value: 1.40.0.Final
            - name: ROOT_PATH
              value: /\${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
          resources: {}
          imagePullPolicy: Always
`;

export const getDeploymentListApiPath = (namespace: string, labelSelector?: string) => {
  const selector = labelSelector ? `?labelSelector=${labelSelector}` : "";
  return `/apis/app/v1/namespaces/${namespace}/deployments${selector}`;
};
