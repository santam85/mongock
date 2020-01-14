package com.github.cloudyrock.mongock;

/**
 * @since 27/07/2014
 */
public interface ChangeEntryRepository<ENTRY extends ChangeEntry> extends Repository {

  boolean isNewChange(ENTRY changeEntry) throws MongockException;

  void save(ENTRY changeEntry) throws MongockException;


}
