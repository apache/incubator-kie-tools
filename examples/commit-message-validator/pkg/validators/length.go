package validators

import (
	"fmt"
	"strconv"
	"strings"
)

func Length(message string, options string) *Validation {
	var minMaxValues = strings.Split(options, "-")

	var minLength, err1 = strconv.Atoi(minMaxValues[0])
	var maxLength, err2 = strconv.Atoi(minMaxValues[1])
	if err1 != nil || err2 != nil || maxLength < minLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Misconfigured length validation parameter (%s). Min and Max length required in the format: \"minLength-maxLength\".", options),
		}
	}
	if len(message) > maxLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message is longer than %d characters.", maxLength),
		}
	}

	if len(message) < minLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message is shorter than %d characters.", minLength),
		}
	}

	return &Validation{
		Result: true,
	}
}
