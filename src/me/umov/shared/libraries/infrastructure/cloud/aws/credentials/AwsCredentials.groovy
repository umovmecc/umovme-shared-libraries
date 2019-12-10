package me.umov.shared.libraries.infrastructure.cloud.aws.credentials


import static me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsContextFactory.getJenkinsContext

class AwsCredentials {

    static AwsKeys getCredentials() {
        getJenkinsContext().withCredentials([getJenkinsContext().usernamePassword(credentialsId: getJenkinsContext().env.AWS_CREDENTIALS_ID, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]){
            return new AwsKeys(getJenkinsContext().env.AWS_ACCESS_KEY_ID, getJenkinsContext().env.AWS_SECRET_ACCESS_KEY)
        }
    }

}
