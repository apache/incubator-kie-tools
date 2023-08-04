#!/bin/bash

export image_id=$1
export image_full_tag=$2

export image_full_name=${image_full_tag%%:*}
export image_full_name=${image_full_name%%@sha256*} # Remove `@sha256` if needed
export image_registry_name=${image_full_name%/*}
export image_name=${image_full_name##*/}
export image_registry=${image_registry_name%/*}
export image_namespace=${image_registry_name##*/}

export image_descriptor_filename=${image_id}-image.yaml

export community_image_id=${image_id/logic-/kogito-}
export community_image_id=${community_image_id/-rhel8/}
