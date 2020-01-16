package com.github.cloudyrock.mongock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Closeable;

public class SpringMongock extends Mongock implements InitializingBean {

  SpringMongock(ChangeEntryRepository changeEntryRepository,
                SpringChangeLogService changeService,
                LockChecker lockChecker,
                boolean throwExceptionIfCannotObtainLock,
                boolean enabled,
                Environment environment,
                MongoTemplate mongoTemplate) {
    super(changeEntryRepository, changeService, lockChecker, throwExceptionIfCannotObtainLock, enabled);
    addChangeSetDependency(Environment.class, environment);
    addChangeSetDependency(MongoTemplate.class, mongoTemplate);
  }

  /**
   * For Spring users: executing mongock after bean is created in the Spring context
   */
  @Override
  public void afterPropertiesSet() {
    execute();
  }
}
