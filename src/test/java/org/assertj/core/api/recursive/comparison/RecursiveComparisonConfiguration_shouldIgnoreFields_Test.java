package org.assertj.core.api.recursive.comparison;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RecursiveComparisonConfiguration_shouldIgnoreFields_Test {

  private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

  @BeforeEach
  public void setup() {
    recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
  }

  @Test
  public void should_register_fields_path_to_ignore_without_duplicates() {
    // GIVEN
    recursiveComparisonConfiguration.ignoreFields("foo", "bar", "foo.bar", "bar");
    // WHEN
    Set<FieldLocation> fields = recursiveComparisonConfiguration.getIgnoredFields();
    // THEN
    assertThat(fields).containsExactlyInAnyOrder(new FieldLocation("foo"),
                                                 new FieldLocation("bar"),
                                                 new FieldLocation("foo.bar"));
  }

  @ParameterizedTest(name = "{0} should be ignored")
  @MethodSource("ignoringNullFieldsSource")
  public void should_ignore_actual_null_fields(DualKey dualKey) {
    // GIVEN
    recursiveComparisonConfiguration.setIgnoreAllActualNullFields(true);
    // WHEN
    boolean ignored = recursiveComparisonConfiguration.shouldIgnore(dualKey);
    // THEN
    assertThat(ignored).as("%s should be ignored", dualKey).isTrue();
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> ignoringNullFieldsSource() {
    return Stream.of(Arguments.of(dualKey(null, "John")),
                     Arguments.of(dualKey(null, 123)),
                     Arguments.of(dualKey(null, (Object) null)),
                     Arguments.of(dualKey(null, new Date())));

  }

  @ParameterizedTest(name = "{0} should be ignored with these ignored fields {1}")
  @MethodSource("ignoringSpecifiedFieldsSource")
  public void should_ignore_specified_fields(DualKey dualKey, List<String> ignoredFields) {
    // GIVEN
    recursiveComparisonConfiguration.ignoreFields(ignoredFields.toArray(new String[0]));
    // WHEN
    boolean ignored = recursiveComparisonConfiguration.shouldIgnore(dualKey);
    // THEN
    assertThat(ignored).as("%s should be ignored with these ignored fields %s", dualKey, ignoredFields).isTrue();
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> ignoringSpecifiedFieldsSource() {
    return Stream.of(Arguments.of(dualKeyWithPath("name"), list("name")),
                     Arguments.of(dualKeyWithPath("name"), list("foo", "name", "foo")),
                     Arguments.of(dualKeyWithPath("name", "first"), list("name.first")),
                     Arguments.of(dualKeyWithPath("father", "name", "first"),
                                  list("father", "name.first", "father.name.first")));

  }

  @ParameterizedTest(name = "{0} should be ignored with these regexes {1}")
  @MethodSource("ignoringRegexSpecifiedFieldsSource")
  public void should_ignore_fields_specified_with_regex(DualKey dualKey, List<String> regexes) {
    // GIVEN
    recursiveComparisonConfiguration.ignoreFieldsByRegexes(regexes.toArray(new String[0]));
    // WHEN
    boolean ignored = recursiveComparisonConfiguration.shouldIgnore(dualKey);
    // THEN
    assertThat(ignored).as("%s should be ignored with these regexes %s", dualKey, regexes).isTrue();
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> ignoringRegexSpecifiedFieldsSource() {
    return Stream.of(Arguments.of(dualKeyWithPath("name"), list(".*name")),
                     Arguments.of(dualKeyWithPath("name"), list("foo", "n.m.", "foo")),
                     Arguments.of(dualKeyWithPath("name", "first"), list("name\\.first")),
                     Arguments.of(dualKeyWithPath("name", "first"), list(".*first")),
                     Arguments.of(dualKeyWithPath("name", "first"), list("name.*")),
                     Arguments.of(dualKeyWithPath("father", "name", "first"),
                                  list("father", "name.first", "father\\.name\\.first")));

  }

  private static DualKey dualKeyWithPath(String... pathElements) {
    return new DualKey(list(pathElements), new Object(), new Object());
  }

  private static DualKey dualKey(Object key1, Object key2) {
    return new DualKey(randomPath(), key1, key2);
  }

  private static List<String> randomPath() {
    return list(RandomStringUtils.random(RandomUtils.nextInt(0, 10)));
  }

}
