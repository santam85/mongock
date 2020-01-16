package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.executor.MigrationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mongock runner
 *
 * @since 26/07/2014
 */
public class Mongock {
  private static final Logger logger = LoggerFactory.getLogger(Mongock.class);
  private final MigrationExecutor executor;
  protected final LockChecker lockChecker;
  private final boolean throwExceptionIfCannotObtainLock;
  private final boolean enabled;

  private Map<String, Object> metadata;
  private Map<Class, Object> dependencies = new HashMap<>();


  protected Mongock(
      ChangeEntryRepository changeEntryRepository,
      ChangeLogService changeLogService,
      LockChecker lockChecker,
      boolean throwExceptionIfCannotObtainLock,
      boolean enabled) {
    this.lockChecker = lockChecker;
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    this.enabled = enabled;
    executor = new MigrationExecutor(changeLogService, changeEntryRepository, this::getDependency, metadata);

  }

  /**
   * @return true if an execution is in progress, in any process.
   */
  public boolean isExecutionInProgress() {
    return lockChecker.isLockHeld();
  }

  /**
   * @return true if Mongock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  public void execute() {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
    } else {

      try {
        lockChecker.acquireLockDefault();
        executor.executeMigration();
      } catch (LockCheckException lockEx) {

        if (throwExceptionIfCannotObtainLock) {
          logger.error(lockEx.getMessage());//only message as the exception is propagated
          throw new MongockException(lockEx.getMessage());
        } else {
          logger.warn(lockEx.getMessage());
          logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION");
        }

      } finally {
        lockChecker.releaseLockDefault();//we do it anyway, it's idempotent
        logger.info("Mongock has finished his job.");
      }
    }
  }

  protected Optional<Object> getDependency(Class type) {
    return this.dependencies.entrySet().stream()
        .filter(entrySet -> type.isAssignableFrom(entrySet.getKey()))
        .map(Map.Entry::getValue)
        .findFirst();
  }


  //todo delegate to executor
  void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  //todo delegate this to executor
  <T> void addChangeSetDependency(Class<T> type, T dependency) {
    this.dependencies.put(type, dependency);
  }

  void addChangeSetDependency(Object dependency) {
    this.dependencies.put(dependency.getClass(), dependency);
  }


}
