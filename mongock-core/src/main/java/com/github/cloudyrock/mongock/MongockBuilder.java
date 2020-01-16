package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;

import java.util.Map;
import java.util.Optional;

public class MongockBuilder extends MongockBuilderBase<MongockBuilder, Mongock> {

  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  public MongockBuilder(com.mongodb.client.MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected MongockBuilder returnInstance() {
    return this;
  }

  @Override
  protected Mongock createMongockInstance() {
    return new Mongock(changeEntryRepository, createChangeService(), lockChecker, throwExceptionIfCannotObtainLock, enabled);
  }

  @Override
  protected ChangeLogService createChangeServiceInstance() {
    return new ChangeLogService();
  }



}
