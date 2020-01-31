// vars/imgBuildNexus.groovy
def call(String imageName, String imageTag = env.BUILD_NUMBER, String target = ".", String dockerFile="Dockerfile", Closure body) {
  def dockerReg = "workshop"
  def label = "img-${UUID.randomUUID().toString()}"
  def podYaml = libraryResource 'podtemplates/imageBuildPushNexus.yml'
  podTemplate(name: 'img', label: label, yaml: podYaml) {
    node(label) {
      body()
      imageNameTag()
      gitShortCommit()
      container('img') {
        sh """
          img build --build-arg buildNumber=${BUILD_NUMBER} --build-arg shortCommit=${env.SHORT_COMMIT} --build-arg commitAuthor="${env.COMMIT_AUTHOR}" -t ${dockerReg}/${imageName}:${imageTag} ${pwd()}
          sleep 360
        """
      }
    }
  }
}
