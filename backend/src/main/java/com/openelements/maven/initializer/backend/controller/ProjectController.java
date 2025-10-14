package com.openelements.maven.initializer.backend.controller;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import com.openelements.maven.initializer.backend.service.ProjectGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectGeneratorService projectGeneratorService;

  public ProjectController(ProjectGeneratorService projectGeneratorService) {
    this.projectGeneratorService = projectGeneratorService;
  }

  @PostMapping("/generate")
  public ResponseEntity<byte[]> generateProject(@Valid @RequestBody ProjectRequestDTO request) {
    byte[] zipBytes = projectGeneratorService.generateProjectZip(request);
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + request.getArtifactId() + ".zip\"")
        .body(zipBytes);
  }
}
