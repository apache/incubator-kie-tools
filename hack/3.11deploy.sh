#!/bin/sh
for FILE in deploy/crds/app_v1alpha1_kogitoapp_crd.yaml deploy/crds/app_v1alpha1_kogitodataindex_crd.yaml deploy/role.yaml deploy/service_account.yaml deploy/role_binding.yaml deploy/operator.yaml
do
	oc apply -f ${FILE}
done