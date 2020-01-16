package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.resources.EnvironmentMock;
import com.github.cloudyrock.mongock.test.changelogs.AnotherMongockTestResource;
import com.github.cloudyrock.mongock.test.profiles.def.UnProfiledChangeLog;
import com.github.cloudyrock.mongock.test.profiles.dev.ProfiledDevChangeLog;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SpringMongockProfileTest extends SpringMongockTestBase {
  private static final int CHANGELOG_COUNT = 11;

  @Test
  public void shouldRunDevProfileAndNonAnnotated() {
    // given
    changeService.setEnvironment(new EnvironmentMock("dev", "test"));
    changeService.setChangeLogsBasePackage(ProfiledDevChangeLog.class.getPackage().getName());
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long change1 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev1")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);  //  no-@Profile  should not match

    long change2 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev4")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);  //  @Profile("dev")  should not match

    long change3 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev3")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(0, change3);  //  @Profile("default")  should not match
  }

  @Test
  public void shouldRunUnprofiledChangeLog() {
    // given
    changeService.setChangeLogsBasePackage(UnProfiledChangeLog.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("test"));
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long change1 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev1")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);

    long change2 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev2")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);

    long change3 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev3")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change3);  //  @Profile("dev")  should not match

    long change4 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev4")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(0, change4);  //  @Profile("pro")  should not match

    long change5 = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME)
        .count(new Document()
            .append(ChangeEntryMongo.KEY_CHANGE_ID, "Pdev5")
            .append(ChangeEntryMongo.KEY_AUTHOR, "testuser"));
    assertEquals(1, change5);  //  @Profile("!pro")  should match
  }

  @Test
  public void shouldNotRunAnyChangeSet() {
    // given
    changeService.setChangeLogsBasePackage(ProfiledDevChangeLog.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("no-profile"));
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(0, changes);
  }

  @Test
  public void shouldNotRunChangeLog_IfProfileAdded_WhenProfileNotMatchingChangeLog() {
    // given
    changeService.setChangeLogsBasePackage(ProfiledDevChangeLog.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("pro"));
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(0, changes);
  }

  @Test
  public void shouldRunChangeSetsWhenNoEnv() {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", null);
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test
  public void shouldRunChangeSetsWhenEmptyEnv() {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock());
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test
  public void shouldRunAllChangeSets() throws Exception {
    // given
    changeService.setChangeLogsBasePackage(AnotherMongockTestResource.class.getPackage().getName());
    TestUtils.setField(runner, "springEnvironment", new EnvironmentMock("dev"));
    when(changeEntryRepository.isNewChange(any(ChangeEntryMongo.class))).thenReturn(true);

    // when
    runner.execute();

    // then
    long changes = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

}
