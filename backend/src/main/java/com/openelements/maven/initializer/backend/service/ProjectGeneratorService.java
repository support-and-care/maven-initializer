package com.openelements.maven.initializer.backend.service;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorService.class);
  private final CommandExecutorService commandExecutor;
  private final ProjectStructureService structureService;
  private final ProjectZipperService zipperService;
  private final FileUtilsService fileUtils;

  public ProjectGeneratorService(
      CommandExecutorService commandExecutor,
      ProjectStructureService structureService,
      ProjectZipperService zipperService,
      FileUtilsService fileUtils) {
    this.commandExecutor = commandExecutor;
    this.structureService = structureService;
    this.zipperService = zipperService;
    this.fileUtils = fileUtils;
  }

  public String generateProject(ProjectRequestDTO request) {
    logger.info("Starting project generation for: {}", request);

    String projectDirName = request.getArtifactId();
    Path targetDir = Paths.get("target").toAbsolutePath().resolve(projectDirName);

    try {
      // Step 1: Create directories
      Files.createDirectories(targetDir.getParent());
      Path tempDir = targetDir.getParent().resolve("temp_" + projectDirName);
      fileUtils.recreateDirectory(tempDir);

      // Step 2: Run NewProject
      commandExecutor.runNewProject(
          tempDir, request.getGroupId(), request.getArtifactId(), request.getVersion());

      // Step 3: Locate generated project
      Path projectRoot = fileUtils.locateGeneratedProject(tempDir, request.getArtifactId());

      // Step 4: Move to final destination
      fileUtils.moveDirectory(projectRoot, targetDir);

      // Step 5: Add managed dependencies and plugins
      commandExecutor.addManagedDependency(targetDir, "commons-io:commons-io:2.15.1");
      commandExecutor.addManagedPlugins(targetDir);

      // Step 6: Create project structure
      structureService.createStructure(targetDir, request);

      logger.info("✅ Project generated successfully at: {}", targetDir);
      return targetDir.toString();

    } catch (Exception e) {
      logger.error("❌ Project generation failed", e);
      throw new RuntimeException("Failed to generate project: " + e.getMessage(), e);
    }
  }

  public byte[] generateProjectZip(ProjectRequestDTO request) {
    String projectPath = generateProject(request);
    try {
      return zipperService.createProjectZip(projectPath);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create ZIP: " + e.getMessage(), e);
    }
  }
}
