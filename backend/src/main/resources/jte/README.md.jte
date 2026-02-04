@import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO
@param ProjectRequestDTO data
# ${data.getName()}

## Prerequisites

*   **Java SDK**: Version ${data.getJavaVersion()} or higher
@if(!data.isIncludeMavenWrapper())
*   **Apache Maven™**: Version 3.9.x or higher
@endif

## Build Instructions

This project uses Apache Maven™ for dependency management and building.

To build the project and run tests, use the following command:
@if(data.isIncludeMavenWrapper())
On Windows:

```shell
.\mvnw.cmd verify
```

On Mac/Linux:

```shell
./mvnw verify
```
@else
```shell
mvn verify
```
@endif
@if(data.isIncludeCheckstyle() || data.isIncludeSpotless())
## Code Formatting Plugins
@endif
@if(data.isIncludeSpotless())
### Spotless Maven Plugin

This project includes the [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven).
Please configure the plugin according to your code style preferences.
See the [Spotless documentation](https://github.com/diffplug/spotless/tree/main/plugin-maven) for configuration options.
@endif
@if(data.isIncludeCheckstyle())
### Maven Checkstyle Plugin

This project includes the [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/).
Please configure the plugin according to your coding standards.
See the [Maven Checkstyle Plugin documentation](https://maven.apache.org/plugins/maven-checkstyle-plugin/) for configuration options.
@endif

