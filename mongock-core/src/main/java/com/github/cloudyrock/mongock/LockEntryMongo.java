package com.github.cloudyrock.mongock;

import org.bson.Document;

import java.util.Date;

public class LockEntryMongo extends LockEntry<Document> {

  static final String KEY_FIELD = "key";
  static final String STATUS_FIELD = "status";
  static final String OWNER_FIELD = "owner";
  static final String EXPIRES_AT_FIELD = "expiresAt";

  public LockEntryMongo(String key, String status, String owner, Date expiresAt) {
    super(key, status, owner, expiresAt);
  }

  @Override
  public Document getItemForDB() {
    Document entry = new Document();
    entry.append(KEY_FIELD, this.getKey())
        .append(STATUS_FIELD, this.getStatus())
        .append(OWNER_FIELD, this.getOwner())
        .append(EXPIRES_AT_FIELD, this.getExpiresAt());
    return entry;
  }

}
