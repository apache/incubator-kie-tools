package version

// Don't change it manually, use the make bump-version <version>
const operatorVersion = "main"

// GetOperatorVersion gets the current operator version
func GetOperatorVersion() string {
	return operatorVersion
}
