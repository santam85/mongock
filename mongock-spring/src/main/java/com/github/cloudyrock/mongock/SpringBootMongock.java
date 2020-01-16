package com.github.cloudyrock.mongock;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Closeable;
import java.util.Optional;

public class SpringBootMongock extends Mongock implements ApplicationRunner {

  private final ApplicationContext springContext;

  SpringBootMongock(ChangeEntryRepository changeEntryRepository,
                    SpringChangeLogService changeService,
                    LockChecker lockChecker,
                    ApplicationContext springContext,
                    MongoTemplate mongoTemplate) {
    super(changeEntryRepository, changeService, lockChecker);
    this.springContext = springContext;
    addChangeSetDependency(MongoTemplate.class, mongoTemplate);
  }

  /**
   * @see ApplicationRunner#run(ApplicationArguments)
   * @see Mongock#execute()
   */
  @Override
  public void run(ApplicationArguments args) {
    execute();
  }

  @Override
  protected Optional<Object> getDependency(Class dependencyType) {
    Optional<Object> dependencyFromParent = super.getDependency(dependencyType);
    if(dependencyFromParent.isPresent()) {
      return dependencyFromParent;
    } else if (springContext != null){
      return Optional.of(springContext.getBean(dependencyType));
    } else {
      return Optional.empty();
    }
  }
}
