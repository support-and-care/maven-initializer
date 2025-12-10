package com.openelements.maven.initializer.backend.service;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceTemplateEngineTest {

    @Test
    void testincludeMvnWrapper() throws IOException {
        ResourceTemplateEngine engine = new ResourceTemplateEngine();
        ProjectRequestDTO data=  new ProjectRequestDTO("groupid", "artifactId", "Version", "description", "25", "my-project");
        Path outputFilePath = Path.of("target/jteTest/includeMvnWrapper.md");
        engine.createReadmeFile(data, outputFilePath);

        assertThat(outputFilePath.toFile().exists()).isTrue();
        assertThat(outputFilePath.toFile()).hasSameTextualContentAs(new File("src/test/resources/jte/includedMvnWrapper.md"));

    }

}