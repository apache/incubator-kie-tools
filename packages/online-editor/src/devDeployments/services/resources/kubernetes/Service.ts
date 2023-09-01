export const createServiceYaml = `
kind: Service
apiVersion: v1
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
spec:
  ports:
    - name: 8080-tcp
      protocol: TCP
      port: 8080
      targetPort: 8080
  selector:
    app: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
    deploymentconfig: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
  type: ClusterIP
`;
