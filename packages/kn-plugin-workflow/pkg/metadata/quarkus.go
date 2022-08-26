package metadata

var QuarkusPlatformGroupId, QuarkusVersion string

type DependenciesVersion struct {
	QuarkusPlatformGroupId string
	QuarkusVersion         string
}

func ResolveQuarkusDependencies() DependenciesVersion {
	return DependenciesVersion{
		QuarkusPlatformGroupId: QuarkusPlatformGroupId,
		QuarkusVersion:         QuarkusVersion,
	}
}
