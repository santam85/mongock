package com.github.cloudyrock.mongock;

import org.bson.Document;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class ChangeEntryMongo extends ChangeEntry<Document> {
  static final String KEY_EXECUTION_ID = "executionId";
  static final String KEY_CHANGE_ID = "changeId";
  static final String KEY_AUTHOR = "author";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_CHANGE_LOG_CLASS = "changeLogClass";
  private static final String KEY_CHANGE_SET_METHOD = "changeSetMethod";
  private static final String KEY_EXECUTION_MILLIS = "executionMillis";
  private static final String KEY_METADATA = "metadata";


  public ChangeEntryMongo(String executionId, String changeId, String author, Date timestamp, String changeLogClass, String changeSetMethodName, Object metadata) {
    super(executionId, changeId, author, timestamp, changeLogClass, changeSetMethodName, metadata);
  }

  @Override
  public Document getItemForDB() {
    return new Document()
        .append(KEY_EXECUTION_ID, this.getExecutionId())
        .append(KEY_CHANGE_ID, this.getChangeId())
        .append(KEY_AUTHOR, this.getAuthor())
        .append(KEY_TIMESTAMP, this.getTimestamp())
        .append(KEY_CHANGE_LOG_CLASS, this.getChangeLogClass())
        .append(KEY_CHANGE_SET_METHOD, this.getChangeSetMethodName())
        .append(KEY_EXECUTION_MILLIS, this.getExecutionMillis())
        .append(KEY_METADATA, this.getMetadata());
  }

}
