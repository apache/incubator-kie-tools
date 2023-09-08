package env

import (
	"os"
	"strconv"
)

func GetEnvUsername() string {
	return GetOSEnv("USERNAME", "nouser")
}

// GetOSEnv gets a env variable
func GetOSEnv(key, fallback string) string {
	value, exists := os.LookupEnv(key)
	if !exists {
		value = fallback
	}
	return value
}

// GetBoolOSEnv gets a env variable as a boolean
func GetBoolOSEnv(key string) bool {
	val := GetOSEnv(key, "false")
	ret, err := strconv.ParseBool(val)
	if err != nil {
		return false
	}
	return ret
}
