package com.github.cloudyrock.mongock;

import org.bson.Document;

public interface Entry {

  Document buildFullDBObject();
}
