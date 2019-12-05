import { GitHubPageType } from "../../github/GitHubPageType";
import { PrInfo } from "../pr/IsolatedPrEditor";
import * as dependencies__ from "../../dependencies";

class RepoInfo {
    public owner: string;
    public repo: string;
    public gitref: string;
}

function uriMatches(regex: string) {
    return !!window.location.pathname.match(new RegExp(regex));
}

function discoverCurrentGitHubPageType() {
    if (uriMatches(`.*/.*/edit/.*`)) {
        return GitHubPageType.EDIT;
    }

    if (uriMatches(`.*/.*/blob/.*`)) {
        return GitHubPageType.VIEW;
    }

    if (uriMatches(`.*/.*/pull/[0-9]+/files.*`)) {
        return GitHubPageType.PR;
    }

    if (uriMatches(`.*/.*/pull/[0-9]+/commits.*`)) {
        return GitHubPageType.PR;
    }

    return GitHubPageType.ANY;
}

function parseRepoInfo(): RepoInfo {
    const info = window.location.pathname.split('/');
    if (info.length >= 5) {
        return {
            owner: info[1],
            repo: info[2],
            gitref: info[4]
        };
    }
    throw new Error("Not able to retrieve repository information.");
}

function parsePrInfo(): PrInfo {
    const prInfos = dependencies__.all.array.pr__prInfoContainer()!.map(e => e.textContent!);

    const targetOrganization = window.location.pathname.split("/")[1];
    const repository = window.location.pathname.split("/")[2];

    // PR is within the same organization
    if (prInfos.length < 6) {
        return {
            repo: repository,
            targetOrg: targetOrganization,
            targetGitRef: prInfos[1],
            org: targetOrganization,
            gitRef: prInfos[3]
        };
    }

    // PR is from a fork to an upstream
    return {
        repo: repository,
        targetOrg: targetOrganization,
        targetGitRef: prInfos[2],
        org: prInfos[4],
        gitRef: prInfos[5]
    };
}

export { RepoInfo, parseRepoInfo, parsePrInfo, discoverCurrentGitHubPageType };