package validators

import (
	"fmt"
	"regexp"
	"strings"
)

func IssuePrefix(message string, prefix string) *Validation {
	if prefix == "" {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Misconfigured IssuePrefix validation parameter (%s). Prefix required, replacing numbers with \"*\", examples: \"JIRA-*\", \"#*\", \"kie-issues#*\"", prefix),
		}
	}

	var regex = "^" + strings.ReplaceAll(prefix, "*", "[0-9]+")

	match, _ := regexp.MatchString(regex, message)

	if !match {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message missing or wrong desired prefix: \"%s\".", prefix),
		}
	}

	return &Validation{
		Result: true,
	}
}
