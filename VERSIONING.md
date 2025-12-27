# Version Management

This project follows [Semantic Versioning](https://semver.org/) (MAJOR.MINOR.PATCH).

## Version Format
- **MAJOR**: Incompatible API changes
- **MINOR**: New functionality in a backward compatible manner
- **PATCH**: Backward compatible bug fixes

## Current Version: 1.0.0

### Version History

#### 1.0.0 (Current)
- Initial release
- Core BoundedThreadPool implementation
- BoundedBlockingQueue with configurable capacity
- Five rejection policies: BLOCK, ABORT, DISCARD, DISCARD_OLDEST, CALLER_RUNS
- Graceful shutdown (shutdown()) and immediate shutdown (shutdownNow())
- Factory methods for common configurations
- Comprehensive test suite

### Release Process

1. Update version in `build.gradle.kts`
2. Update `CHANGELOG.md`
3. Commit changes with tag: `git tag -a v1.0.0 -m "Release version 1.0.0"`
4. Push to GitHub: `git push origin v1.0.0`
5. JitPack will automatically build and publish the release

### Using Different Versions

#### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'io.github.abdol-ahmed.btp:bounded-thread-pool:1.0.0'
}
```

#### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>io.github.abdol-ahmed.btp</groupId>
    <artifactId>bounded-thread-pool</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Version Compatibility

- **1.x.x**: Stable API with backward compatibility
- Breaking changes will increment MAJOR version
- All MINOR versions will be backward compatible
- PATCH versions for bug fixes only

### Branching Strategy

- `main`: Stable releases
- `develop`: Next development version
- `feature/*`: New features
- `hotfix/*`: Critical bug fixes
