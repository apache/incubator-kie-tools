#!/bin/bash


# PARAMS

export K8S_TESTING_PATH=/Users/tiagobento/redhat/tmp/k8s-testing
export K8S_YAML_TO_APISERVER_REQUESTS_PATH=/Users/tiagobento/redhat/kie-tools/packages/k8s-yaml-to-apiserver-requests


# MAIN

# colima start --cpu 4 --memory 6 --disk 20 --dns 1.1.1.1
kind delete cluster && \
docker ps -aq | xargs docker stop | xargs docker rm && \
kind create cluster --config $K8S_TESTING_PATH/kind-cluster.yml && \
kubectl apply -f $K8S_TESTING_PATH/service-account.yml && \
kubectl apply -f $K8S_TESTING_PATH/kube-proxy.yml && \
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml && \
sleep 20 && \
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s && \
echo "Done."

#
# Unfortunatelly we create the Ingress Controller with `k8s-yaml-to-apiserver-requests` because the exposed proxy for K8s API Server only works after the Ingress Controller is created.
#
# export K8S__API_SERVER_URL='http://127.0.0.1/kube-apiserver'
# export K8S__NAMESPACE='default'
# export K8S__TOKEN=$(kubectl get secret kie-sandbox-secret -o jsonpath={.data.token} | base64 -d)
# export K8S__YAML_URL='https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml'
# ts-node src/main.ts $K8S__API_SERVER_URL $K8S__NAMESPACE $K8S__TOKEN $K8S__YAML_URL

echo "Deploying K8s Dashboard using k8s-yaml-to-apiserver-requests"
export K8S__API_SERVER_URL='http://127.0.0.1/kube-apiserver'
export K8S__NAMESPACE='default'
export K8S__TOKEN=$(kubectl get secret kie-sandbox-secret -o jsonpath={.data.token} | base64 -d)
export K8S__YAML_URL='https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml'
ts-node $K8S_YAML_TO_APISERVER_REQUESTS_PATH/src/main.ts $K8S__API_SERVER_URL $K8S__NAMESPACE $K8S__TOKEN $K8S__YAML_URL
open "$K8S__API_SERVER_URL/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login"
kubectl get secret kie-sandbox-secret -o jsonpath={.data.token} | base64 -d

