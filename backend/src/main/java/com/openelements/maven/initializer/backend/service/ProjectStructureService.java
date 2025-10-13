package com.openelements.maven.initializer.backend.service;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectStructureService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectStructureService.class);

  public void createStructure(Path projectRoot, ProjectRequestDTO req) throws IOException {
    createDirectories(projectRoot, req.getGroupId(), req.getArtifactId());
    createGitignoreFile(projectRoot);
    createMainClass(projectRoot, req);
    updatePomFile(projectRoot, req);
  }

  private void createDirectories(Path root, String groupId, String artifactId) throws IOException {
    String packagePath = groupId.replace(".", "/");
    Path javaDir = root.resolve("src/main/java/" + packagePath + "/" + artifactId.toLowerCase());
    Files.createDirectories(javaDir);
  }

  private void createGitignoreFile(Path root) throws IOException {
    String content =
        """
                        target/
                        pom.xml.tag
                        pom.xml.releaseBackup
                        pom.xml.versionsBackup
                        pom.xml.next
                        release.properties
                        dependency-reduced-pom.xml
                        buildNumber.properties
                        """;
    Files.writeString(root.resolve(".gitignore"), content);
  }

  private void createMainClass(Path root, ProjectRequestDTO req) throws IOException {
    String pkg = req.getGroupId();
    String cls = req.getArtifactId();
    Path javaDir = root.resolve("src/main/java/" + pkg.replace(".", "/") + "/" + cls.toLowerCase());
    Path file = javaDir.resolve(cls + ".java");

    String content =
        String.format(
            """
                            package %s;

                            /**
                             * %s
                             */
                            public class %s {
                                public static void main(String[] args) {
                                    System.out.println("Hello, %s!");
                                }
                            }
                            """,
            pkg,
            req.getDescription() != null ? req.getDescription() : "Generated Maven Project",
            cls,
            cls);

    Files.writeString(file, content);
  }

  private void updatePomFile(Path root, ProjectRequestDTO req) throws IOException {
    Path pomPath = root.resolve("pom.xml");
    if (!Files.exists(pomPath)) return;

    String content =
        Files.readString(pomPath)
            .replace(
                "<groupId>com.example</groupId>", "<groupId>" + req.getGroupId() + "</groupId>")
            .replace(
                "<artifactId>new-project</artifactId>",
                "<artifactId>" + req.getArtifactId() + "</artifactId>")
            .replace(
                "<maven.compiler.release>25</maven.compiler.release>",
                "<maven.compiler.release>" + req.getJavaVersion() + "</maven.compiler.release>");

    Files.writeString(pomPath, content);
  }
}
