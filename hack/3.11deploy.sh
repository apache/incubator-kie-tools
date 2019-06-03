#!/bin/sh
for FILE in deploy/crds/kogitoapp.crd.yaml deploy/role.yaml deploy/service_account.yaml deploy/role_binding.yaml deploy/operator.yaml
do
	oc apply -f ${FILE}
done