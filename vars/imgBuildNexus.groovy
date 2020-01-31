// vars/imgBuildNexus.groovy
def call(String imageName, String repoOwner, String registry, String imageTag = env.BUILD_NUMBER, String target = ".", String dockerFile="Dockerfile", Closure body) {
  def label = "img-${UUID.randomUUID().toString()}"
  def podYaml = libraryResource 'podtemplates/imageBuildPushNexus.yml'
  podTemplate(name: 'img', label: label, yaml: podYaml) {
    node(label) {
      body()
      imageNameTag()
      gitShortCommit()
      container('img') {
        sh """
          ls ~
          ls
          img build --build-arg buildNumber=${BUILD_NUMBER} --build-arg shortCommit=${env.SHORT_COMMIT} --build-arg commitAuthor="${env.COMMIT_AUTHOR}" -t ${registry}/${repoOwner}/${imageName}:${imageTag} ${pwd()}
          img push ${registry}/${repoOwner}/${imageName}:${imageTag}
        """
      }
    }
  }
}
