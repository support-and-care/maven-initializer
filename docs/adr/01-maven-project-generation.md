# ADR 001 — Project Generation for Apache Maven™

- **Status:** accepted
- **Decision makers:** Support and Care Dev Team
- **Date:** 2025-10-07
- **Related issues:** [GitHub #14](https://github.com/support-and-care/maven-initializer/issues/14), [GitHub #6](https://github.com/support-and-care/maven-initializer/issues/6)

## Context and Problem Description

Initializer for Apache Maven™ should help to generate a ready-to-use project structure for Apache Maven™. The core of the application is to generate the necessary project files.

In the past, others already solved this problem. Therefore, there is a chance to reuse their solution. We want to evaluate an existing solution and compare it with a custom solution based on a template engine.

## Considered Options

1. Reuse Apache Maven™ Archetype components
2. Maveniverse Toolbox
3. Custom solution based on a template engine

## Decision

**Maveniverse Toolbox**

We chose the Java-DSL approach, which is straightforward to use programmatically. The Toolbox already provides methods such as `addDependencies` that help to implement our requirements easily. It is implemented by an Apache Maven™ maintainer and is an active project.

### Positive Consequences

- Toolbox is a library from the Apache Maven™ community.
- The Java DSL simplifies programmatic configuration.
- It is actively maintained.

## Pros and Cons of the Options

### Reuse Apache Maven™ Archetype Components

Apache Maven™ has its own mechanism for generating new projects, implemented as an Apache Maven™ plugin. This plugin has common components responsible for the generation part. Integrating these components is tricky because they depend on Apache Maven™ Core infrastructure. We would still need custom implementation to match our requirements (e.g. adding dependencies).

- **Good:** Many ready-to-use templates exist.
- **Good:** Generator implementation exists.
- **Bad:** Custom adjustment for our requirements would be high.
- **Bad:** Dependency hell.
- **Bad:** Integration in other components is not easy (e.g. field-injection).

### Maveniverse Toolbox

Toolbox started as a showcase for [MIMA Resolver](https://github.com/maveniverse/mima), but provides a ready-to-use Java DSL for POM generation. This DSL is demonstrated in its own Apache Maven™ plugin and CLI tool, and can be used as a library.

- **Good:** DSL-based.
- **Good:** Good integration.
- **Good:** Good abstraction of the Apache Maven™ infrastructure.

### Custom solution based on a template engine

We could start from scratch using a template engine that integrates well with Spring Boot.

- **Good:** Full control.
- **Bad:** Everything would have to be implemented from scratch.
- **Bad:** Increased maintenance.

## Links

- [PoC with shared Maveniverse Toolbox](https://github.com/sparsick/embedded-maven-shared-toolbox)
- [PoC with Maven Archetype](https://github.com/sparsick/embedded-maven-archetype)
- [List of template engines](https://www.baeldung.com/spring-template-engines) with good Spring integration
- [Result of the Kickoff Meeting for Apache Maven™ initializer](https://github.com/support-and-care/maven-initializer/issues/3#issuecomment-3324185347)
