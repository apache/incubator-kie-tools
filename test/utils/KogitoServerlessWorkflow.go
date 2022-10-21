package utils

import (
	"bytes"
	apiv08 "github.com/kiegroup/kogito-serverless-operator/api/v08"
	"io/ioutil"
	"k8s.io/apimachinery/pkg/util/yaml"
	"log"
)

func GetKogitoServerlessWorkflow(path string) (*apiv08.KogitoServerlessWorkflow, error) {

	ksw := &apiv08.KogitoServerlessWorkflow{}
	yamlFile, err := ioutil.ReadFile(path)
	if err != nil {
		log.Fatalf("yamlFile.Get err   #%v ", err)
		return nil, err
	}
	// Important: Here we are reading the CR deployment file from a given path and creating a &apiv08.KogitoServerlessWorkflow struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksw)
	if err != nil {
		log.Fatalf("Unmarshal: %v", err)
		return nil, err
	}
	log.Printf("Successfully read KSW  #%v ", ksw)
	return ksw, err
}
