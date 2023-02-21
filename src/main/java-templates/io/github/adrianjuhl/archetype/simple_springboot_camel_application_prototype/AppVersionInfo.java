package io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype;

public class AppVersionInfo {

  private String projectGroupId = "${project.groupId}";
  private String projectArtifactId = "${project.artifactId}";
  private String projectVersion = "${project.version}";
  private String gitCommitHash = "${gitCommitHash}";
  private String gitCommitDatetime = "${gitCommitDatetime}";
  private String gitCommitTags = "${gitCommitTags}";
  private String gitCommitBranch = "${gitCommitBranch}";
  private String mvnBuildDatetime = "${mvnBuildDatetime}";

  public String toJsonString() {
    return new StringBuilder()
      .append("{\"appVersionInfo\":")
      .append(    "{")
      .append(        "\"projectGroupId\":\"").append(projectGroupId).append("\",")
      .append(        "\"projectArtifactId\":\"").append(projectArtifactId).append("\",")
      .append(        "\"projectVersion\":\"").append(projectVersion).append("\",")
      .append(        "\"gitCommitHash\":\"").append(gitCommitHash).append("\",")
      .append(        "\"gitCommitDatetime\":\"").append(gitCommitDatetime).append("\",")
      .append(        "\"gitCommitTags\":\"").append(gitCommitTags).append("\",")
      .append(        "\"gitCommitBranch\":\"").append(gitCommitBranch).append("\",")
      .append(        "\"mvnBuildDatetime\":\"").append(mvnBuildDatetime).append("\"")
      .append(    "}")
      .append("}")
      .toString();
  }

}
