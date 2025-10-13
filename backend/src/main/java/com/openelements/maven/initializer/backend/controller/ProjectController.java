package com.openelements.maven.initializer.backend.controller;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import com.openelements.maven.initializer.backend.service.ProjectGeneratorService;
import com.openelements.maven.initializer.backend.service.ProjectZipperService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

  private final ProjectGeneratorService projectGeneratorService;
  private final ProjectZipperService projectZipperService;

  public ProjectController(
      ProjectGeneratorService projectGeneratorService, ProjectZipperService projectZipperService) {
    this.projectGeneratorService = projectGeneratorService;
    this.projectZipperService = projectZipperService;
  }

  @PostMapping(value = "/generate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Resource> generateProject(@Valid @RequestBody ProjectRequestDTO request) {
    logger.info("Received project generation request: {}", request);

    try {
      // Generate the project
      String projectPath = projectGeneratorService.generateProject(request);

      // Zip the project
      byte[] zipBytes = projectZipperService.createProjectZip(projectPath);
      ByteArrayResource resource = new ByteArrayResource(zipBytes);

      // Prepare response
      String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1);
      HttpHeaders headers = new HttpHeaders();
      headers.add(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectName + ".zip\"");
      headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

      logger.info("Successfully generated and packaged project: {}", projectName);
      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(zipBytes.length)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);

    } catch (Exception e) {
      logger.error("Failed to generate project", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    response.put("success", false);
    response.put("message", "Validation failed");
    response.put("errors", errors);

    logger.warn("Validation failed for project generation request: {}", errors);
    return ResponseEntity.badRequest().body(response);
  }
}
