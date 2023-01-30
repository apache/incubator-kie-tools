package pkg

import (
	"strings"

	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg/metadata"
	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg/validators"
)

var validatorsMap = map[string]validators.ValidationFunction{
	"Length":      validators.Length,
	"IssuePrefix": validators.IssuePrefix,
}

func Validate(message string) *validators.Validation {
	var enabledValidators = strings.Split(metadata.Validators, ";")

	var result bool = true
	var reason string = ""

	for _, validatorNameAndOptions := range enabledValidators {
		var validator = strings.Split(validatorNameAndOptions, ":")
		var validationResult = validatorsMap[validator[0]](message, validator[1])

		if !validationResult.Result {
			result = validationResult.Result
			reason = reason + "\n" + validationResult.Reason
		}
	}

	return &validators.Validation{
		Result: result,
		Reason: reason,
	}
}
