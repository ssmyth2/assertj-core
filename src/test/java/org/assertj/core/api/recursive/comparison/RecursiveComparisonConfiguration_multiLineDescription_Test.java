/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2018 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.presentation.StandardRepresentation.STANDARD_REPRESENTATION;
import static org.assertj.core.util.Lists.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

public class RecursiveComparisonConfiguration_multiLineDescription_Test {

  private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

  @BeforeEach
  public void setup() {
    recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
  }

  @Test
  public void should_show_that_null_fields_are_ignored() {
    // WHEN
    recursiveComparisonConfiguration.setIgnoreAllActualNullFields(true);
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    assertThat(multiLineDescription).contains(format("- all actual null fields were ignored in the comparison%n"));
  }

  @Test
  public void should_show_that_some_given_fields_are_ignored() {
    // WHEN
    recursiveComparisonConfiguration.ignoreFields("foo", "bar", "foo.bar");
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    assertThat(multiLineDescription).contains(format("- the following fields were ignored in the comparison: foo, bar, foo.bar%n"));
  }

  @Test
  public void should_show_the_regexes_used_to_ignore_fields() {
    // WHEN
    recursiveComparisonConfiguration.ignoreFieldsByRegexes("foo", "bar", "foo.bar");
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    assertThat(multiLineDescription).contains(format("- the fields matching the following regexes were ignored in the comparison: foo, bar, foo.bar%n"));
  }

  @Test
  public void should_show_the_ignored_overridden_equals_methods_regexes() {
    // WHEN
    recursiveComparisonConfiguration.ignoreOverriddenEqualsByRegexes("foo", "bar", "foo.bar");
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    // @format:off
    assertThat(multiLineDescription).contains(format(
               "- overridden equals methods were used in the comparison, except for:%n" +
               "--- the types matching the following regexes: foo, bar, foo.bar%n"));
    // @format:on
  }

  @Test
  public void should_show_the_ignored_overridden_equals_methods_types() {
    // WHEN
    recursiveComparisonConfiguration.ignoreOverriddenEqualsForTypes(String.class, Multimap.class);
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    // @format:off
    assertThat(multiLineDescription).contains(format(
               "- overridden equals methods were used in the comparison, except for:%n" +
               "--- the following types: java.lang.String, com.google.common.collect.Multimap%n"));
    // @format:on
  }

  @Test
  public void should_show_the_ignored_overridden_equals_methods_fields() {
    // WHEN
    recursiveComparisonConfiguration.ignoreOverriddenEqualsForFields("foo", "baz", "foo.baz");
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    // @format:off
    assertThat(multiLineDescription).contains(format(
               "- overridden equals methods were used in the comparison, except for:%n" +
               "--- the following fields: foo, baz, foo.baz%n"));
    // @format:on
  }

  @Test
  public void should_show_a_complete_multiline_description() {
    // WHEN
    recursiveComparisonConfiguration.setIgnoreAllActualNullFields(true);
    recursiveComparisonConfiguration.ignoreFields("foo", "bar", "foo.bar");
    recursiveComparisonConfiguration.ignoreFieldsByRegexes("f.*", ".ba.", "..b%sr..");
    recursiveComparisonConfiguration.ignoreOverriddenEqualsByRegexes(".*oo", ".ar", "oo.ba");
    recursiveComparisonConfiguration.ignoreOverriddenEqualsForTypes(String.class, Multimap.class);
    recursiveComparisonConfiguration.ignoreOverriddenEqualsForFields("foo", "baz", "foo.baz");
    String multiLineDescription = recursiveComparisonConfiguration.multiLineDescription(STANDARD_REPRESENTATION);
    // THEN
    // @format:off
    assertThat(multiLineDescription).isEqualTo(format("- all actual null fields were ignored in the comparison%n" +
                                                      "- the following fields were ignored in the comparison: foo, bar, foo.bar%n" +
                                                      "- the fields matching the following regexes were ignored in the comparison: f.*, .ba., ..b%%sr..%n"+
                                                      "- overridden equals methods were used in the comparison, except for:%n" +
                                                      "--- the following fields: foo, baz, foo.baz%n" +
                                                      "--- the following types: java.lang.String, com.google.common.collect.Multimap%n" +
                                                      "--- the types matching the following regexes: .*oo, .ar, oo.ba%n"));
    // @format:on
  }

  @Test
  public void should_build_multiline_description_containing_percent() {
    // GIVEN
    ComparisonDifference com = new ComparisonDifference(list("a", "b"), "foo%", "%bar%%", "%additional %information%");

    // THEN
    assertThat(com.multiLineDescription()).isEqualTo(format("field/property 'a.b' differ:%n" +
                                                            "- actual value   : \"foo%%\"%n" +
                                                            "- expected value : \"%%bar%%%%\"%n" +
                                                            "%%additional %%information%%"));
  }

}
