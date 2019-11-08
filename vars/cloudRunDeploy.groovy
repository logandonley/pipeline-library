// vars/cloudRunDeploy.groovy
def call(Map config) {
  def podYaml = libraryResource 'podtemplates/cloud-run.yml'
  def label = "cloudrun-${UUID.randomUUID().toString()}"
  podTemplate(name: 'cloud-run-pod', label: label, yaml: podYaml) {
    node(label) {
      container(name: 'gcp-sdk') {
        if (config.deployType == "gke") {
          sh "gcloud beta run deploy ${config.serviceName} --image ${config.image} --platform gke --cluster ${config.clusterName} --cluster-location ${config.region} --namespace ${config.namespace}"
        }
        else if (config.deployType == "vmware") {
          sh "gcloud auth activate-service-account --key-file=${env.GOOGLE_CLOUD_KEYFILE_JSON}"
          sh "gcloud beta run deploy ${config.serviceName} --image ${config.image} --platform kubernetes --namespace ${config.namespace} --kubeconfig /.config/kube/config"
        }
        else {
          sh "gcloud beta run deploy ${config.serviceName} --image ${config.image} --allow-unauthenticated --region ${config.region} --platform managed"
        } 
      }
    }
  }
}