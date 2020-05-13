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

repoURL = "https://repository.jboss.org/org/kie/kogito/"

Modules = {
    #service-name: module-name(directory in which module's module.yaml file is present)
    #Note: Service name should be same as given in the repository
    "data-index-service": "kogito-data-index",
    "jobs-service": "kogito-jobs-service",
    "management-console": "kogito-management-console"
}


def getMetadataRoot(repoURL):
    '''
    Get the root element from the maven-metadata
    :param repoURL: URL of the repo from which artifacts needs to be updated
    :return: root object
    '''
    metadataURL=repoURL+"maven-metadata.xml"
    mavenMetadata=requests.get(metadataURL)
    with open('maven-metadata.xml', 'wb') as f:
        f.write(mavenMetadata.content)
    tree = ET.parse('maven-metadata.xml')
    root=tree.getroot()
    return root

def getSnapshotVersion(repoURL):
    '''
    parse the xml and finds the snapshotVersion
    :param repoURL: URL of the repo from which artifacts needs to be updated
    :return: snapshotVersion string
    '''
    root=getMetadataRoot(repoURL)
    snapshotVersion=root.find("./versioning/snapshotVersions/snapshotVersion/value").text
    return snapshotVersion

def getArtifactID(repoURL):
    '''
    parse the xml and finds the artifactID
    :param repoURL: URL of the repo from which artifacts needs to be updated
    :return: artifactID string
    '''
    root=getMetadataRoot(repoURL)
    artifactID=root.find("./artifactId").text
    return artifactID

def getRunnerURL(repoURL):
    ''' 
    Creates the updated URL for runner.jar
    :param repoURL: URL of the repo from which artifacts needs to be updated
    :return: url string
    '''
    artifactId=getArtifactID(repoURL)
    snapshotVersion=getSnapshotVersion(repoURL)
    url=repoURL+"{}-{}-runner.jar".format(artifactId,snapshotVersion)
    return url

def getMD5(repoURL):
    '''
    Fetches the md5 code for the latest runner.jar
    :param repoURL: URL of the repo from which artifacts needs to be updated
    :return: runnerMD5 string
    '''
    runnerURL=getRunnerURL(repoURL)
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

def update_artifacts(repoURL,modulePath):
    '''
    Updates the module.yaml file of services with latest artifacts
    :parm repoURL: URL of the repo from which artifacts needs to be updated
    :param modulePath: relative file location of the module.yaml for the kogito service
    '''

    with open(modulePath) as module:
        data=yaml_loader().load(module)
        data['artifacts'][0]['url']=getRunnerURL(repoURL)
        data['artifacts'][0]['md5']=getMD5(repoURL)
    with open(modulePath, 'w') as module:
        yaml_loader().dump(data, module)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Update the snapshot url for kogito services.')
    parser.add_argument('--snapshot-version', dest='snapshotVersion', default='8.0.0-SNAPSHOT', help='Defines the snapshot version of the jboss repository, defaults to 8.0.0-SNAPSHOT')
    args = parser.parse_args()
    
    for service, path in Modules.items():
        update_artifacts(repoURL+"{}/{}/".format(service,args.snapshotVersion),"modules/{}/module.yaml".format(path))
        print("Successfully updated the artifacts for: ",service)

    os.remove("maven-metadata.xml")
