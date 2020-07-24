#!/usr/bin/python3
#Script responsible for fetching the latest artifacts of kogito services and updating their module.yaml files as well as updating Maven version
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/update-maven-information.py
#
#Dependencies
# ruamel.yaml
# elementpath 

import sys
sys.dont_write_bytecode = True

import common

import xml.etree.ElementTree as ET
import requests
import subprocess as sp
import os
import argparse
from ruamel.yaml import YAML

DEFAULT_REPO_URL = "https://repository.jboss.org/nexus/content/groups/public/"
DEFAULT_VERSION = "8.0.0-SNAPSHOT"
DEFAULT_ARTIFACT_PATH = "org/kie/kogito"

Modules = {
    #service-name: module-name(directory in which module's module.yaml file is present)
    #Note: Service name should be same as given in the repository
    "data-index-service": "kogito-data-index",
    "explainability-service": "kogito-explainability",
    "jobs-service": "kogito-jobs-service",
    "management-console": "kogito-management-console"
}

def isSnapshotVersion(version):
    '''
    Check whether the given version is a snapshot version
    :param version: The version to check
    :return: whether the given version is a snapshot version
    '''
    return version.endswith("-SNAPSHOT")

def getMetadataRoot(service):
    '''
    Get the root element from the maven-metadata
    :param service: Service information (repo_url, version, name)
    :return: root object
    '''
    metadataURL=service["repo_url"]+"maven-metadata.xml"
    mavenMetadata=requests.get(metadataURL)
    with open('maven-metadata.xml', 'wb') as f:
        f.write(mavenMetadata.content)
    tree = ET.parse('maven-metadata.xml')
    root=tree.getroot()
    os.remove("maven-metadata.xml")
    return root

def getSnapshotVersion(service):
    '''
    parse the xml and finds the snapshotVersion
    :param service: Service information (repo_url, version, name)
    :return: snapshotVersion string
    '''
    root=getMetadataRoot(service)
    snapshotVersion=root.find("./versioning/snapshotVersions/snapshotVersion/value").text
    return snapshotVersion

def getRunnerURL(service):
    ''' 
    Creates the updated URL for runner.jar
    :param service: Service information (repo_url, version, name)
    :return: url string
    '''
    finalVersion = service["version"]
    if isSnapshotVersion(finalVersion):
        finalVersion=getSnapshotVersion(service)
    url = service["repo_url"] + "{}-{}-runner.jar".format(service["name"], finalVersion)
    return url

def getMD5(service):
    '''
    Fetches the md5 code for the latest runner.jar
    :param service: Service information (repo_url, version, name)
    :return: runnerMD5 string
    '''
    runnerURL=getRunnerURL(service)
    runnerMD5URL=runnerURL+".md5"
    runnerMD5=sp.getoutput("curl -s  {}".format(runnerMD5URL))
    return runnerMD5

def update_artifacts(service,modulePath):
    '''
    Updates the module.yaml file of services with latest artifacts
    :param service: Service information (repo_url, version, name)
    :param modulePath: relative file location of the module.yaml for the kogito service
    '''

    with open(modulePath) as module:
        data=common.yaml_loader().load(module)
        data['artifacts'][0]['url']=getRunnerURL(service)
        data['artifacts'][0]['md5']=getMD5(service)
    with open(modulePath, 'w') as module:
        common.yaml_loader().dump(data, module)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Update Maven information in repo from the given artifact url and version.')
    parser.add_argument('--repo-url', dest='repo_url', default=DEFAULT_REPO_URL, help='Defines the url of the repository to extract the artifacts from, defaults to {}'.format(DEFAULT_REPO_URL))
    parser.add_argument('--version', dest='version', default=DEFAULT_VERSION, help='Defines the version of artifacts to retrieve from the repository url, defaults to {}'.format(DEFAULT_VERSION))
    args = parser.parse_args()
    
    # Update Kogito Service modules
    for serviceName, modulePath in Modules.items():
        service = {
            "repo_url" : args.repo_url + "{}/{}/{}/".format(DEFAULT_ARTIFACT_PATH, serviceName, args.version),
            "name" : serviceName,
            "version" : args.version
        }
        moduleYamlFile = "modules/{}/module.yaml".format(modulePath)
        
        update_artifacts(service, moduleYamlFile)
        print("Successfully updated the artifacts for: ", serviceName)
    
    # Need also to set the KOGITO_VERSION for artifacts to the given one
    common.update_kogito_version_env_in_modules(args.version)
