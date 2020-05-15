package main

import "github.com/kiegroup/kogito-cloud-operator/test/framework"

func main() {
	namespaces := framework.GetNamespacesInHistory()
	for _, namespace := range namespaces {
		if len(namespace) > 0 {
			framework.DeleteNamespace(namespace)
		}
	}

	framework.ClearNamespaceHistory()
}
