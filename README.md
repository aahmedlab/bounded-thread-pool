# Bounded Thread Pool

[![](https://jitpack.io/v/abdol-ahmed/bounded-thread-pool.svg)](https://jitpack.io/#abdol-ahmed/bounded-thread-pool)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java 17+](https://img.shields.io/badge/Java-17+-green.svg)](https://openjdk.org/)
[![Tests](https://img.shields.io/badge/Tests-31%20Passing-brightgreen.svg)](https://github.com/abdol-ahmed/bounded-thread-pool/actions)

A lightweight, thread-safe bounded thread pool implementation in Java with support for various rejection policies and graceful shutdown semantics.

## Features

- **Bounded Capacity**: Configurable maximum queue size prevents memory exhaustion
- **Multiple Rejection Policies**: BLOCK, ABORT, DISCARD, DISCARD_OLDEST, CALLER_RUNS
- **Graceful Shutdown**: `shutdown()` completes queued tasks without interruption
- **Immediate Shutdown**: `shutdownNow()` interrupts workers and returns unexecuted tasks
- **Thread Safety**: Fully thread-safe implementation using `ReentrantLock` and `Condition`
- **Deadlock-Free**: BLOCK policy handled without holding pool lock
- **Factory Methods**: Convenient factory methods for common configurations
- **Clean API**: Minimal public surface with intuitive boolean state methods

## Requirements

- JDK 17+
- Gradle 9.2.0+ (project uses Gradle 9.2.0)

## Quick Start - Test the Library

### Simple Main Example

Create a `Main.java` file to test the library:

```java
import io.github.abdol_ahmed.btp.BoundedThreadPool;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create a thread pool with 4 workers and queue capacity of 10
        BoundedThreadPool pool = BoundedThreadPool.createFixed(4);
        
        System.out.println("Pool created with " + pool.getPoolSize() + " threads");
        System.out.println("Queue capacity: " + pool.getQueueRemainingCapacity());
        
        // Submit some tasks
        int taskCount = 10;
        CountDownLatch done = new CountDownLatch(taskCount);
        
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            pool.submit(() -> {
                System.out.println("Task " + taskId + " running in " + 
                    Thread.currentThread().getName());
                // Simulate some work
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                done.countDown();
            });
        }
        
        // Wait for all tasks to complete
        if (done.await(5, TimeUnit.SECONDS)) {
            System.out.println("All tasks completed!");
        } else {
            System.out.println("Tasks did not complete in time");
        }
        
        // Shutdown the pool
        pool.shutdown();
        if (pool.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Pool shutdown successfully");
        } else {
            System.out.println("Pool did not shutdown in time");
        }
    }
}
```

### Compile and Run

#### Using javac (without build tool):

```bash
# Download the JAR from JitPack
wget https://jitpack.io/com/github/abdol-ahmed/bounded-thread-pool/1.0.0/bounded-thread-pool-1.0.0.jar

# Compile
javac -cp "bounded-thread-pool-1.0.0.jar" Main.java

# Run
java -cp ".:bounded-thread-pool-1.0.0.jar" Main
```

#### Using Gradle:

Create `build.gradle`:
```gradle
plugins {
    id 'java'
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'io.github.abdol_ahmed.btp:bounded-thread-pool:1.0.0'
}

task runMain(type: JavaExec) {
    mainClass = 'Main'
    classpath = sourceSets.main.runtimeClasspath
}
```

#### Using Gradle with Kotlin DSL (build.gradle.kts):

Create `build.gradle.kts`:
```kotlin
plugins {
    java
    application
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.abdol_ahmed.btp:bounded-thread-pool:1.0.0")
}

application {
    mainClass.set("Main")
}

// Custom task to run the main example
tasks.register<JavaExec>("runMain") {
    group = "application"
    description = "Run the Main example"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("Main")
}
```

Run with:
```bash
# Using Groovy DSL
./gradlew runMain

# Using Kotlin DSL
./gradlew runMain
# or
./gradlew run
```

#### Using Maven:

Create `pom.xml`:
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>test-btp</artifactId>
    <version>1.0</version>
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>io.github.abdol-ahmed.btp</groupId>
            <artifactId>bounded-thread-pool</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

Run with:
```bash
mvn compile exec:java -Dexec.mainClass="Main"
```

### Expected Output

```
Pool created with 4 threads
Queue capacity: 8
Task 0 running in btp-worker-0
Task 1 running in btp-worker-1
Task 2 running in btp-worker-2
Task 3 running in btp-worker-3
Task 4 running in btp-worker-0
Task 5 running in btp-worker-1
Task 6 running in btp-worker-2
Task 7 running in btp-worker-3
Task 8 running in btp-worker-0
Task 9 running in btp-worker-1
All tasks completed!
Pool shutdown successfully
```

## Installation

### JitPack

Add JitPack repository to your build file:

#### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'io.github.abdol_ahmed.btp:bounded-thread-pool:1.0.0'
}
```

#### Gradle Kotlin DSL (build.gradle.kts)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.abdol_ahmed.btp:bounded-thread-pool:1.0.0")
}
```

#### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>io.github.abdol_ahmed.btp</groupId>
    <artifactId>bounded-thread-pool</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage Examples

### Factory Methods (Recommended)

```java
// Create a pool with default BLOCK policy
BoundedThreadPool pool = BoundedThreadPool.create(4, 100);

// Create a fixed-size pool with CALLER_RUNS policy
BoundedThreadPool pool = BoundedThreadPool.createFixed(8);

// Create a pool optimized for CPU-bound tasks
BoundedThreadPool pool = BoundedThreadPool.createCpuBound();

// Create a pool optimized for I/O-bound tasks
BoundedThreadPool pool = BoundedThreadPool.createIoBound();
```

### Basic Usage

```java
// Submit tasks
pool.submit(() -> {
    System.out.println("Task executed in: " + Thread.currentThread().getName());
});

// Graceful shutdown
pool.shutdown();
pool.awaitTermination(5, TimeUnit.SECONDS);
```

### Rejection Policies

```java
// BLOCK: Wait for space in queue (may block indefinitely)
BoundedThreadPool pool1 = new BoundedThreadPool(2, 10, RejectionPolicy.BLOCK);

// ABORT: Throw RejectedExecutionException if queue is full
BoundedThreadPool pool2 = new BoundedThreadPool(2, 10, RejectionPolicy.ABORT);

// DISCARD: Silently discard task if queue is full
BoundedThreadPool pool3 = new BoundedThreadPool(2, 10, RejectionPolicy.DISCARD);

// DISCARD_OLDEST: Remove oldest task and add new one if queue is full
BoundedThreadPool pool4 = new BoundedThreadPool(2, 10, RejectionPolicy.DISCARD_OLDEST);

// CALLER_RUNS: Execute task in caller thread if queue is full
BoundedThreadPool pool5 = new BoundedThreadPool(2, 10, RejectionPolicy.CALLER_RUNS);
```

### Shutdown Patterns

```java
// Graceful shutdown - completes all queued tasks
pool.shutdown();
if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
    // Optional: force shutdown if graceful takes too long
    List<Runnable> remaining = pool.shutdownNow();
    System.out.println("Unexecuted tasks: " + remaining.size());
}

// Immediate shutdown - interrupts workers
List<Runnable> unexecuted = pool.shutdownNow();
```

### Monitoring

```java
// Check pool state
System.out.println("Pool size: " + pool.getPoolSize());
System.out.println("Queue size: " + pool.getQueueSize());
System.out.println("Queue remaining capacity: " + pool.getQueueRemainingCapacity());
System.out.println("Is queue full? " + pool.isQueueFull());
System.out.println("Is running? " + pool.isRunning());
System.out.println("Is shutdown? " + pool.isShutdown());
System.out.println("Is terminated? " + pool.isTerminated());
```

## Build and Test

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean and test
./gradlew clean test
```

## Architecture

### Core Components

- **`BoundedThreadPool`**: Main thread pool implementation
- **`BoundedBlockingQueue`**: Internal bounded queue (package-private)
- **`PoolState`**: Internal pool state enum (package-private)
- **`RejectionPolicy`**: Enum defining task rejection strategies

### Thread Pool States

1. **RUNNING**: Accepts new tasks and executes them
2. **SHUTDOWN**: Rejects new tasks, completes queued tasks gracefully
3. **STOP**: Rejects new tasks, interrupts workers, doesn't start new tasks
4. **TERMINATED**: All workers have terminated

## Implementation Details

### Deadlock Prevention

The BLOCK rejection policy is handled outside the pool lock to prevent deadlocks:

```java
if (rejectionPolicy == RejectionPolicy.BLOCK) {
    if (poolState != PoolState.RUNNING) {
        throw new RejectedExecutionException("Pool is shut down");
    }
    blockingQueue.put(task); // May block, but doesn't hold poolLock
    return;
}
```

### Graceful Shutdown Mechanism

1. `shutdown()` sets state to SHUTDOWN and closes the queue
2. Workers detect queue closure via `take()` returning null
3. Workers finish current tasks and exit naturally

### Immediate Shutdown Behavior

Important: Tasks already taken by workers before they observe the STOP state may be dropped and won't be included in the returned list. This is documented behavior due to the inherent race between draining the queue and workers taking tasks.

## Testing

The project includes comprehensive tests covering:

- Basic task execution
- Thread pool sizing
- Rejection policy behavior
- Shutdown and termination
- Task accounting during shutdown
- Edge cases and error conditions
- Stress testing

Run tests with: `./gradlew test`

## Design Decisions

1. **Clean Public API**: Only essential methods exposed, implementation details hidden
2. **Factory Methods**: Convenient constructors for common use cases
3. **Boolean State Methods**: Intuitive state checking without exposing internal enum
4. **Volatile Pool State**: `poolState` is volatile for visibility without requiring lock acquisition
5. **Separate Locks**: Pool and queue use separate locks to minimize contention
6. **Daemon Workers**: Worker threads are daemon to prevent JVM hangs

## Limitations

1. **Task Accounting**: During `shutdownNow()`, tasks taken by workers before observing STOP may be dropped (documented behavior).

2. **No Dynamic Resizing**: Thread pool size is fixed after construction.

3. **No Priority Queue**: Tasks are executed in FIFO order only.

## Versioning

This project follows [Semantic Versioning](https://semver.org/).

- **MAJOR**: Incompatible API changes
- **MINOR**: New functionality in a backward compatible manner
- **PATCH**: Backward compatible bug fixes

Current version: **1.0.0**

## For Developers - Publishing New Versions

### Prerequisites
- Git installed and configured
- GitHub access with push permissions
- Personal Access Token (if using HTTPS)

### Publishing Workflow

1. **Make Your Changes**
   ```bash
   # Make your code changes
   git add .
   git commit -m "feat: Your feature description"
   ```

2. **Run Tests**
   ```bash
   ./gradlew test
   # Ensure all tests pass before publishing
   ```

3. **Tag New Version**
   ```bash
   # Create a semantic version tag
   git tag v1.0.1  # For patch version
   git tag v1.1.0  # For minor version
   git tag v2.0.0  # For major version
   ```

4. **Push to GitHub**
   ```bash
   # Push commits and tag
   git push origin main
   git push origin v1.0.1
   ```

5. **JitPack Build**
   - JitPack automatically detects the new tag
   - Go to [JitPack](https://jitpack.io) to monitor the build
   - Wait for green status (usually 2-5 minutes)

6. **Release is Live!**
   - Your library is now available at the new version

### Version Automation

The project uses the Palantir git-version plugin:
- **With tags**: Version resolves to tag (e.g., `v1.0.1`)
- **Without tags**: Version shows commit hash (e.g., `abc1234.dirty`)

### Authentication Issues

If you get "Permission denied" errors:

1. **Using Personal Access Token**:
   ```bash
   # Create token at https://github.com/settings/tokens
   git remote set-url origin https://YOUR_TOKEN@github.com/abdol-ahmed/bounded-thread-pool.git
   git push origin main v1.0.1
   ```

2. **Using SSH** (recommended for multiple accounts):
   ```bash
   git remote set-url origin git@github.com:abdol-ahmed/bounded-thread-pool.git
   git push origin main v1.0.1
   ```

### Quick Publishing Script

Create `scripts/publish.sh`:
```bash
#!/bin/bash
set -e

VERSION=$1
if [ -z "$VERSION" ]; then
    echo "Usage: ./scripts/publish.sh 1.0.1"
    exit 1
fi

echo "Running tests..."
./gradlew test

echo "Creating tag v$VERSION..."
git tag v$VERSION

echo "Pushing to GitHub..."
git push origin main
git push origin v$VERSION

echo "Published v$VERSION! Check JitPack for build status."
```

Usage:
```bash
./scripts/publish.sh 1.0.1
```

## Migration Guide

### From v1.0.0 to v1.x.x

No breaking changes expected. Minor versions will add new features while maintaining backward compatibility.

### Breaking Changes (v2.0.0)

When major version changes occur:
1. Check the CHANGELOG for breaking changes
2. Update your dependency version
3. Update code that uses removed APIs
4. Run tests with new version

## Usage Examples

### Web Server Example

```java
import io.github.abdol_ahmed.btp.BoundedThreadPool;

public class WebServer {
    private final BoundedThreadPool requestPool;
    
    public WebServer() {
        // Optimize for I/O-bound requests
        this.requestPool = BoundedThreadPool.createIoBound();
    }
    
    public void handleRequest(Request request) {
        try {
            requestPool.submit(() -> {
                processRequest(request);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Server shutdown", e);
        }
    }
    
    public void shutdown() {
        requestPool.shutdown();
        try {
            if (!requestPool.awaitTermination(30, TimeUnit.SECONDS)) {
                requestPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### Batch Processing Example

```java
import io.github.abdol_ahmed.btp.BoundedThreadPool;
import io.github.abdol_ahmed.btp.RejectionPolicy;

public class BatchProcessor {
    public void processBatch(List<Item> items) {
        // Use ABORT policy to fail fast if queue is full
        BoundedThreadPool pool = new BoundedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            100,
            RejectionPolicy.ABORT
        );
        
        try {
            for (Item item : items) {
                pool.submit(() -> item.process());
            }
        } finally {
            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

### Rate Limiting Example

```java
import io.github.abdol_ahmed.btp.BoundedThreadPool;

public class RateLimitedService {
    private final BoundedThreadPool pool;
    
    public RateLimitedService() {
        // Small pool with small queue for rate limiting
        this.pool = new BoundedThreadPool(2, 10, RejectionPolicy.BLOCK);
    }
    
    public void submitTask(Runnable task) throws InterruptedException {
        // Will block if too many tasks are queued
        pool.submit(task);
    }
}
```

## Best Practices

1. **Choose the Right Policy**:
   - Use `BLOCK` for rate limiting
   - Use `ABORT` for fail-fast scenarios
   - Use `CALLER_RUNS` for responsive UIs
   - Use `DISCARD` for lossy data pipelines

2. **Always Shutdown**:
   ```java
   try (BoundedThreadPool pool = BoundedThreadPool.createFixed(4)) {
       // Use pool
   } // Automatically calls shutdown()
   ```

3. **Monitor Pool State**:
   ```java
   if (pool.isQueueFull()) {
       // Handle backpressure
       logger.warn("Queue is full, consider throttling");
   }
   ```

4. **Handle Interruptions**:
   ```java
   try {
       pool.submit(task);
   } catch (InterruptedException e) {
       Thread.currentThread().interrupt();
       // Handle interruption gracefully
   }
   ```

## Troubleshooting

### Common Issues

1. **"Could not complete execution for Gradle Test Executor"**
   - Reduce task counts in tests
   - Ensure `shutdownNow()` is used in tearDown
   - Check for resource leaks

2. **Permission Denied on Push**
   - Use Personal Access Token
   - Check GitHub permissions
   - Consider SSH authentication

3. **JitPack Build Fails**
   - Check build logs on JitPack
   - Ensure `gradle.properties` exists
   - Verify all dependencies are public

### Getting Help

- Check [USAGE.md](USAGE.md) for detailed examples
- Review test cases for usage patterns
- Open an issue on GitHub for bugs

## License

Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

### Project Maintainer

- **Abdullah Ahmed** (abdol-ahmed)
- Email: abdol.ahmed@gmail.com

### Build Requirements

- **Gradle**: 9.2.0
- **Java**: 17+
- **Test Framework**: JUnit 5.10.0
