package com.openelements.maven.initializer.backend.service;

import eu.maveniverse.maven.toolbox.plugin.hello.AddManagedDependency;
import eu.maveniverse.maven.toolbox.plugin.hello.AddManagedPlugin;
import eu.maveniverse.maven.toolbox.plugin.hello.NewProject;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import picocli.CommandLine;

@Service
public class CommandExecutorService {

  private static final Logger logger = LoggerFactory.getLogger(CommandExecutorService.class);

  public void runNewProject(Path cwd, String groupId, String artifactId, String version) {
    String gav = "%s:%s:%s".formatted(groupId, artifactId, version);
    int exitCode = executeCommand(new NewProject(), cwd, "--force", gav);
    if (exitCode != 0) throw new RuntimeException("NewProject failed with code " + exitCode);
  }

  public void addManagedDependency(Path cwd, String dependency) {
    int exitCode = executeCommand(new AddManagedDependency(), cwd, dependency);
    if (exitCode != 0) throw new RuntimeException("AddManagedDependency failed");
  }

  public void addManagedPlugins(Path cwd) {
    String[] plugins = {
      "org.apache.maven.plugins:maven-compiler-plugin:3.14.0",
      "org.apache.maven.plugins:maven-resources-plugin:3.3.1",
      "org.apache.maven.plugins:maven-surefire-plugin:3.5.4",
      "org.apache.maven.plugins:maven-jar-plugin:3.4.2",
      "org.apache.maven.plugins:maven-install-plugin:3.1.4",
      "org.apache.maven.plugins:maven-deploy-plugin:3.1.4"
    };

    for (String plugin : plugins) {
      int exitCode = executeCommand(new AddManagedPlugin(), cwd, plugin);
      if (exitCode != 0) throw new RuntimeException("AddManagedPlugin failed for " + plugin);
    }
  }

  private int executeCommand(Object command, Path cwd, String... args) {
    try {
      command.getClass().getMethod("setCwd", Path.class).invoke(command, cwd);
    } catch (Exception ignored) {
    }
    int exitCode = new CommandLine(command).execute(args);
    logger.debug(
        "Executed {} with args {} -> exit {}", command.getClass().getSimpleName(), args, exitCode);
    return exitCode;
  }
}
