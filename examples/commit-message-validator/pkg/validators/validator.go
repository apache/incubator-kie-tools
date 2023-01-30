package validators

type Validation struct {
	Result bool   `json:"result"`
	Reason string `json:"reason,omitempty"`
}

type ValidationFunction func(message string, options string) *Validation
