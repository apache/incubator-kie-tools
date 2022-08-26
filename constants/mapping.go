package constants

func MetadataKeys() func(string) string {
	// innerMap is captured in the closure returned below
	innerMap := map[string]string{
		"key":             "kie.kogito.sw.org/key",
		"name":            "kie.kogito.sw.org/name",
		"description":     "kie.kogito.sw.org/description",
		"annotations":     "kie.kogito.sw.org/annotations",
		"dataInputSchema": "kie.kogito.sw.org/dataInputSchema",
		"expressionLang":  "kie.kogito.sw.org/expressionLang",
		"metadata":        "kie.kogito.sw.org/metadata",
		"version":         "kie.kogito.sw.org/version",
	}

	return func(key string) string {
		return innerMap[key]
	}
}
