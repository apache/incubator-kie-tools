package common

import (
	"testing"
)

var testCasesCheckOperatorRunning = []struct {
	name     string
	input    string
	expected bool
}{
	{
		name: "pod running",
		input: `NAME   READY   STATUS    RESTARTS   AGE
kogito-serverless-operator-controller-manager-78cb446b89-gj9jz   2/2     Running   0          95m`,
		expected: true,
	},
	{
		name:     "no pods",
		input:    "No resources found in kogito-serverless-operator-system namespace.",
		expected: false,
	},
	{
		name:     "no pods - empty string",
		input:    "",
		expected: false,
	},
	{
		name:     "no pods - - empty string 2",
		input:    " ",
		expected: false,
	},
	{
		name:     "no pods - some return",
		input:    " some weird return ",
		expected: false,
	},
}

func TestCheckOperatorRunning(t *testing.T) {
	for _, tc := range testCasesCheckOperatorRunning {
		t.Run(tc.name, func(t *testing.T) {
			result := checkOperatorRunning(tc.input)
			if result != tc.expected {
				t.Errorf("Expected %v, but got %v", tc.expected, result)
			}
		})
	}
}
