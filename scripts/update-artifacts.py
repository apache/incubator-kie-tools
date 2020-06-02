#!/usr/bin/python3
#Script responsible for fetching the latest artifacts of kogito services and updating their module.yaml files
#Should be run from root directory of the repository
#Sample usage:  python3 scripts/update-artifacts.py
#
#Dependencies
# ruamel.yaml
# elementpath 


import xml.etree.ElementTree as ET
import requests
import subprocess as sp
import os
import argparse
from ruamel.yaml import YAML

DEFAULT_REPO_URL = "https://repository.jboss.org/"
DEFAULT_VERSION = "8.0.0-SNAPSHOT"
DEFAULT_ARTIFACT_PATH = "org/kie/kogito"

Modules = {
    #service-name: module-name(directory in which module's module.yaml file is present)
    #Note: Service name should be same as given in the repository
    "data-index-service": "kogito-data-index",
    "jobs-service": "kogito-jobs-service",
    "management-console": "kogito-management-console"
}


def getMetadataRoot(serviceUrl):
    '''
    Get the root element from the maven-metadata
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :return: root object
    '''
    metadataURL=serviceUrl+"maven-metadata.xml"
    mavenMetadata=requests.get(metadataURL)
    with open('maven-metadata.xml', 'wb') as f:
        f.write(mavenMetadata.content)
    tree = ET.parse('maven-metadata.xml')
    root=tree.getroot()
    return root

def getSnapshotVersion(serviceUrl):
    '''
    parse the xml and finds the snapshotVersion
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :return: snapshotVersion string
    '''
    root=getMetadataRoot(serviceUrl)
    snapshotVersion=root.find("./versioning/snapshotVersions/snapshotVersion/value").text
    return snapshotVersion

def getArtifactID(serviceUrl):
    '''
    parse the xml and finds the artifactID
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :return: artifactID string
    '''
    root=getMetadataRoot(serviceUrl)
    artifactID=root.find("./artifactId").text
    return artifactID

def getRunnerURL(serviceUrl):
    ''' 
    Creates the updated URL for runner.jar
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :return: url string
    '''
    artifactId=getArtifactID(serviceUrl)
    snapshotVersion=getSnapshotVersion(serviceUrl)
    url=serviceUrl+"{}-{}-runner.jar".format(artifactId,snapshotVersion)
    return url

def getMD5(serviceUrl):
    '''
    Fetches the md5 code for the latest runner.jar
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :return: runnerMD5 string
    '''
    runnerURL=getRunnerURL(serviceUrl)
    runnerMD5URL=runnerURL+".md5"
    runnerMD5=sp.getoutput("curl -s  {}".format(runnerMD5URL))
    return runnerMD5

def yaml_loader():
    '''
    default yaml Loader
    :return: yaml object
    '''
    yaml = YAML()
    yaml.preserve_quotes = True
    yaml.width = 1024
    yaml.indent(mapping=2, sequence=4, offset=2)
    return yaml

def update_artifacts(serviceUrl,modulePath):
    '''
    Updates the module.yaml file of services with latest artifacts
    :parm serviceUrl: URL of the repository's service from which artifacts needs to be updated
    :param modulePath: relative file location of the module.yaml for the kogito service
    '''

    with open(modulePath) as module:
        data=yaml_loader().load(module)
        data['artifacts'][0]['url']=getRunnerURL(serviceUrl)
        data['artifacts'][0]['md5']=getMD5(serviceUrl)
    with open(modulePath, 'w') as module:
        yaml_loader().dump(data, module)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Update the snapshot url for kogito services.')
    parser.add_argument('--repo-url', dest='repoUrl', default=DEFAULT_REPO_URL, help='Defines the url of the repository to extract the artifacts from, defaults to {}'.format(DEFAULT_REPO_URL))
    parser.add_argument('--snapshot-version', dest='snapshotVersion', default=DEFAULT_VERSION, help='Defines the snapshot version of artifacts to retrieve from the repository url, defaults to {}'.format(DEFAULT_VERSION))
    args = parser.parse_args()
    
    for service, path in Modules.items():
        serviceUrl = args.repoUrl+"{}/{}/{}/".format(DEFAULT_ARTIFACT_PATH, service, args.snapshotVersion)
        moduleYamlFile = "modules/{}/module.yaml".format(path)
        
        update_artifacts(serviceUrl,moduleYamlFile)
        print("Successfully updated the artifacts for: ",service)

    os.remove("maven-metadata.xml")
