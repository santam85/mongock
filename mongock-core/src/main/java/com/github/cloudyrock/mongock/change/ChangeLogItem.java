package com.github.cloudyrock.mongock.change;

import java.util.List;

public class ChangeLogItem {

  private final Class<?> type;

  private final String order;

  private final List<ChangeSetItem> changeSetElements;

  public ChangeLogItem(Class<?> type, String order, List<ChangeSetItem> changeSetElements) {
    this.type = type;
    this.order = order;
    this.changeSetElements = changeSetElements;
  }


  public Class<?> getType() {
    return type;
  }

  public String getOrder() {
    return order;
  }

  public List<ChangeSetItem> getChangeSetElements() {
    return changeSetElements;
  }
}
