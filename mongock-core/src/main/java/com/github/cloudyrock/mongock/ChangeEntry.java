package com.github.cloudyrock.mongock;

import org.bson.Document;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
class ChangeEntry {
  static final String KEY_CHANGEID = "changeId";
  static final String KEY_AUTHOR = "author";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_CHANGELOGCLASS = "changeLogClass";
  private static final String KEY_CHANGESETMETHOD = "changeSetMethod";
  private static final String KEY_EXECUTION_MILLIS = "executionMillis";

  private final String changeId;
  private final String author;
  private final Date timestamp;
  private final String changeLogClass;
  private final String changeSetMethodName;
  private long startMillis = -1L;
  private long executionMillis = -1L;

  public ChangeEntry(String changeId, String author, Date timestamp, String changeLogClass, String changeSetMethodName) {
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.changeLogClass = changeLogClass;
    this.changeSetMethodName = changeSetMethodName;
  }


  Document buildFullDBObject() {
    Document entry = new Document();

    entry.append(KEY_CHANGEID, this.changeId)
        .append(KEY_AUTHOR, this.author)
        .append(KEY_TIMESTAMP, this.timestamp)
        .append(KEY_CHANGELOGCLASS, this.changeLogClass)
        .append(KEY_CHANGESETMETHOD, this.changeSetMethodName)
        .append(KEY_EXECUTION_MILLIS, this.executionMillis);

    return entry;
  }

  String getChangeId() {
    return this.changeId;
  }

  String getAuthor() {
    return this.author;
  }

  Date getTimestamp() {
    return this.timestamp;
  }

  String getChangeLogClass() {
    return this.changeLogClass;
  }

  String getChangeSetMethodName() {
    return this.changeSetMethodName;
  }

  public void startTracking() {
    this.startMillis = System.currentTimeMillis();
  }

  public void stopTracking() {
    this.executionMillis = this.startMillis > 0 ? System.currentTimeMillis() - this.startMillis : -1;
  }

  public long getExecutionMillis() {
    return executionMillis;
  }

  @Override
  public String toString() {

    return String.format(
        "Mongock change[%s] for method[%s.%s] at %s during %d milliseconds by %s",
        changeId,
        changeLogClass,
        changeSetMethodName,
        timestamp,
        executionMillis,
        author);
  }
}
