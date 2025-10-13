package com.openelements.maven.initializer.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectZipperService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectZipperService.class);

  public byte[] createProjectZip(String projectPath) throws IOException {
    Path projectDir = Paths.get(projectPath);
    if (!Files.exists(projectDir)) {
      throw new IOException("Project directory does not exist: " + projectPath);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
      Files.walk(projectDir)
          .filter(Files::isRegularFile)
          .forEach(
              path -> {
                try {
                  Path rel = projectDir.getParent().relativize(path);
                  zos.putNextEntry(new ZipEntry(rel.toString()));
                  Files.copy(path, zos);
                  zos.closeEntry();
                } catch (IOException e) {
                  logger.error("Failed to add file to ZIP: {}", path, e);
                }
              });
    }

    logger.info("Created ZIP for project: {} ({} bytes)", projectPath, baos.size());
    return baos.toByteArray();
  }
}
