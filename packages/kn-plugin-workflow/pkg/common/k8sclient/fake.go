package k8sclient

type Fake struct{}

func (m Fake) GetKubectlNamespace() (string, error) {
	return "default", nil
}

func (m Fake) CheckKubectlContext() (string, error) {
	return "default", nil
}

func (m Fake) ExecuteKubectlApply(crd, namespace string) error {
	return nil
}

func (m Fake) ExecuteKubectlDelete(crd, namespace string) error {
	return nil
}
