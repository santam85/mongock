package com.github.cloudyrock.mongock;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.cloudyrock.mongock.StringUtils.hasText;
import static java.util.Arrays.asList;

import com.github.cloudyrock.mongock.change.ChangeLogItem;
import com.github.cloudyrock.mongock.change.ChangeSetItem;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

//TODO: this can become a Util class, no a service: static methods and name is confusing
/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
class ChangeLogService {

  private String changeLogsBasePackage;

  private ArtifactVersion startVersion = new DefaultArtifactVersion("0");

  private ArtifactVersion endVersion = new DefaultArtifactVersion(String.valueOf(Integer.MAX_VALUE));

  ChangeLogService() {
  }

  /**
   * <p>Indicates the package to scan changeLogs</p>
   *
   * @param changeLogsBasePackage path of the package
   */
  //Implementation note: This has been added, replacing constructor, to be able to inject this service as dependency
  void setChangeLogsBasePackage(String changeLogsBasePackage) {
    this.changeLogsBasePackage = changeLogsBasePackage;
  }

  /**
   * <p>
   * Indicates the changeLogs end systemVersion
   * </p>
   *
   * @param endVersion
   *          systemVersion to upgrading upgrading with (lower than this systemVersion)
   */
  // Implementation note: This has been added, replacing constructor, to be
  // able to inject this service as dependency
  void setEndVersion(String endVersion) {
    this.endVersion = new DefaultArtifactVersion(endVersion);
  }

  /**
   * <p>
   * Indicates the changeLogs start systemVersion
   * </p>
   *
   * @param startVersion
   *          systemVersion to start upgrading from (greater equals this systemVersion)
   */
  // Implementation note: This has been added, replacing constructor, to be
  // able to inject this service as dependency
  void setStartVersion(String startVersion) {
    this.startVersion = new DefaultArtifactVersion(startVersion);
  }

  @SuppressWarnings("unchecked")
  List<Class<?>> fetchChangeLogsSorted() {
    Reflections reflections = new Reflections(changeLogsBasePackage);
    List<Class<?>> changeLogs = new ArrayList<>(reflections.getTypesAnnotatedWith(ChangeLog.class)); // TODO remove dependency, do own method
    changeLogs.sort(new ChangeLogComparator());
    return changeLogs;
  }


  public List<ChangeLogItem> fetchChangeLogs() {
    return fetchChangeLogsSorted()
        .stream()
        .map(this::buildChangeLogObject)
        .collect(Collectors.toList());
  }

  private ChangeLogItem buildChangeLogObject(Class<?> type) {
    try {
      Object instance = type.getConstructor().newInstance();
      return new ChangeLogItem(type, instance, type.getAnnotation(ChangeLog.class).order(), fetchChangeSetFromClass(type));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private List<ChangeSetItem> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetsSorted(type)
        .stream()
        .map(method-> {
          ChangeSet ann = method.getAnnotation(ChangeSet.class);
          return new ChangeSetItem(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), method);
        })
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  List<Method> fetchChangeSetsSorted(final Class<?> type) throws MongockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator());
    return changeSets;
  }

  @Deprecated
  ChangeEntry createChangeEntry(String executionId, Method changesetMethod, Map<String, Object> metadata) {
    if (changesetMethod.isAnnotationPresent(ChangeSet.class)) {
      ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);

      return new ChangeEntryMongo(
          executionId,
          annotation.id(),
          annotation.author(),
          new Date(),
          changesetMethod.getDeclaringClass().getName(),
          changesetMethod.getName(),
          metadata);
    } else {
      return null;
    }
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws MongockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changeSetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (method.isAnnotationPresent(ChangeSet.class)) {
        String id = method.getAnnotation(ChangeSet.class).id();
        if (changeSetIds.contains(id)) {
          throw new MongockException(String.format("Duplicated changeset id found: '%s'", id));
        }
        changeSetIds.add(id);
        if(isChangeSetWithinSystemVersionRange(method.getAnnotation(ChangeSet.class))) {
          changeSetMethods.add(method);
        }
      }
    }
    return changeSetMethods;
  }

  //todo Create a SystemVersionChecker
  private boolean isChangeSetWithinSystemVersionRange(ChangeSet changeSetAnn) {
    boolean isWithinVersion = false;
    String versionString = changeSetAnn.systemVersion();
    ArtifactVersion version = new DefaultArtifactVersion(versionString);
    if (version.compareTo(startVersion) >= 0 && version.compareTo(endVersion) < 0) {
      isWithinVersion = true;
    }
    return isWithinVersion;
  }

  private static class ChangeLogComparator implements Comparator<Class<?>>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;

    @Override
    public int compare(Class<?> o1, Class<?> o2) {
      ChangeLog c1 = o1.getAnnotation(ChangeLog.class);
      ChangeLog c2 = o2.getAnnotation(ChangeLog.class);

      String val1 = !(hasText(c1.order())) ? o1.getCanonicalName() : c1.order();
      String val2 = !(hasText(c2.order())) ? o2.getCanonicalName() : c2.order();

      if (val1 == null && val2 == null) {
        return 0;
      } else if (val1 == null) {
        return -1;
      } else if (val2 == null) {
        return 1;
      }

      return val1.compareTo(val2);
    }
  }

  private static class ChangeSetComparator implements Comparator<Method>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;

    @Override
    public int compare(Method o1, Method o2) {
      ChangeSet c1 = o1.getAnnotation(ChangeSet.class);
      ChangeSet c2 = o2.getAnnotation(ChangeSet.class);
      return c1.order().compareTo(c2.order());
    }
  }

}
