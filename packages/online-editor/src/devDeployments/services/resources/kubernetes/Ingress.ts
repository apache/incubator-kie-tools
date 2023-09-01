export const createIngressYaml = `
kind: Ingress
apiVersion: networking.k8s.io/v1
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
    nginx.ingress.kubernetes.io/backend-protocol: HTTP
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
spec:
  rules:
    - http:
        paths:
          - path: /\${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
            pathType: Prefix
            backend:
              service:
                name: \${{ devDeployment.name }}-\${{ devDeployment.uniqueId }}
                port:
                  number: 8080
`;
