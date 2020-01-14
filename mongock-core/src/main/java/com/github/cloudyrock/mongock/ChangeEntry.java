package com.github.cloudyrock.mongock;

import org.bson.Document;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public abstract class ChangeEntry implements Entry {


  private final String executionId;
  private final String changeId;
  private final String author;
  private final Date timestamp;
  private final String changeLogClass;
  private final String changeSetMethodName;
  private final Object metadata;
  private long executionMillis = -1;

  public ChangeEntry(String executionId, String changeId, String author, Date timestamp, String changeLogClass, String changeSetMethodName, Object metadata) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.changeLogClass = changeLogClass;
    this.changeSetMethodName = changeSetMethodName;
    this.metadata = metadata;
  }


  public String getExecutionId() {
    return executionId;
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getAuthor() {
    return this.author;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public String getChangeLogClass() {
    return this.changeLogClass;
  }

  public String getChangeSetMethodName() {
    return this.changeSetMethodName;
  }

  public long getExecutionMillis() {
    return executionMillis;
  }

  public Object getMetadata() {
    return metadata;
  }

  public void setExecutionMillis(long executionMillis) {
    this.executionMillis = executionMillis;
  }


  @Override
  public String toString() {

    return String.format(
        "Mongock change[%s] for method[%s.%s] in execution[%s] at %s for [%d]ms by %s",
        changeId,
        changeLogClass,
        changeSetMethodName,
        executionId,
        timestamp,
        executionMillis,
        author);
  }
}
