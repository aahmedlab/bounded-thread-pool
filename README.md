# Bounded Thread Pool

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
- Gradle 7.0+

## Installation

### JitPack

Add JitPack repository to your build file:

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

## License

Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
