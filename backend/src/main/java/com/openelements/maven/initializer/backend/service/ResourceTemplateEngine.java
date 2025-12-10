package com.openelements.maven.initializer.backend.service;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import gg.jte.resolve.ResourceCodeResolver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class ResourceTemplateEngine {

    public void createReadmeFile(ProjectRequestDTO data, Path filePath) throws IOException {
        CodeResolver codeResolver = new ResourceCodeResolver("jte", getClass().getClassLoader()) ;
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Plain);

        FileOutput output= new FileOutput(filePath, Charset.forName("UTF-8"));
        templateEngine.render("README.md.jte", data, output);
        output.close();

    }
}
