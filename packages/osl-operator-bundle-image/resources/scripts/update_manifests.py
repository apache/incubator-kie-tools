import sys
import ruamel.yaml
from ruamel.yaml.scalarstring import DoubleQuotedScalarString

KUSTOMIZATION_DEFAULT_PATH = "dist/config/default/kustomization.yaml"
KUSTOMIZATION_MANIFESTS_PATH = "dist/config/manifests/kustomization.yaml"
CONTROLLERS_CONFIG_PATH = "dist/config/manager/controllers_cfg.yaml"

def update_default_kustomization(new_namespace, new_name_prefix):
    yaml = ruamel.yaml.YAML()
    yaml.preserve_quotes = True

    with open(KUSTOMIZATION_DEFAULT_PATH, "r") as f:
        kustomization = yaml.load(f)

    kustomization["namespace"] = new_namespace
    kustomization["namePrefix"] = f"{new_name_prefix}-"

    with open(KUSTOMIZATION_DEFAULT_PATH, "w") as f:
        yaml.dump(kustomization, f)

    print(f"{KUSTOMIZATION_DEFAULT_PATH} updated successfully.")

def update_manifests_kustomization(new_csv_prefix):
    yaml = ruamel.yaml.YAML()
    yaml.preserve_quotes = True

    with open(KUSTOMIZATION_MANIFESTS_PATH, "r") as f:
        kustomization = yaml.load(f)

    if 'resources' in kustomization:
        kustomization["resources"] = [
            replace_csv_prefix(resource, new_csv_prefix)
            for resource in kustomization["resources"]
        ]

    with open(KUSTOMIZATION_MANIFESTS_PATH, "w") as f:
        yaml.dump(kustomization, f)

    print(f"{KUSTOMIZATION_MANIFESTS_PATH} updated successfully.")

def replace_csv_prefix(resource, new_csv_prefix):
    if resource.endswith("clusterserviceversion.yaml"):
        return resource.replace("sonataflow-operator", new_csv_prefix)
    return resource

def update_controllers_cfg(new_name_prefix):
    yaml = ruamel.yaml.YAML(typ="rt")
    yaml.preserve_quotes = True

    # 🔑 REQUIRED to keep sequence indentation stable
    yaml.indent(mapping=2, sequence=4, offset=2)

    with open(CONTROLLERS_CONFIG_PATH, "r") as f:
        controllers_cfg = yaml.load(f)

    if "builderConfigMapName" not in controllers_cfg:
        raise KeyError(f"builderConfigMapName not found in YAML file {CONTROLLERS_CONFIG_PATH}")

    new_value = f"{new_name_prefix}-builder-config"
    # 🔧 Mutate the scalar *in place*, ensure that preserves the quotes around a string when writing it back.
    controllers_cfg["builderConfigMapName"] = DoubleQuotedScalarString(new_value)

    with open(CONTROLLERS_CONFIG_PATH, "w") as f:
        yaml.dump(controllers_cfg, f)

    print(f'{CONTROLLERS_CONFIG_PATH} was updated setting builderConfigMapName to "{new_name_prefix}-builder-config"')

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python update_manifests.py <new_namespace> <new_name_prefix>")
        sys.exit(1)

    new_namespace = sys.argv[1]
    new_name_prefix = sys.argv[2]

    update_default_kustomization(new_namespace, new_name_prefix)
    update_manifests_kustomization(new_name_prefix)
    update_controllers_cfg(new_name_prefix)