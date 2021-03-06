/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.plugins.core.issue.ignore;

import com.google.common.collect.ImmutableList;
import org.sonar.api.CoreProperties;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyFieldDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class IgnoreIssuesConfiguration {

  public static final String SUB_CATEGORY_IGNORE_ISSUES = "issues";

  public static final String EXCLUSION_KEY_PREFIX = "sonar.issue.ignore";
  public static final String INCLUSION_KEY_PREFIX = "sonar.issue.enforce";

  public static final String MULTICRITERIA_SUFFIX = ".multicriteria";
  public static final String PATTERNS_MULTICRITERIA_EXCLUSION_KEY = EXCLUSION_KEY_PREFIX + MULTICRITERIA_SUFFIX;
  public static final String PATTERNS_MULTICRITERIA_INCLUSION_KEY = INCLUSION_KEY_PREFIX + MULTICRITERIA_SUFFIX;
  public static final String RESOURCE_KEY = "resourceKey";
  private static final String PROPERTY_FILE_PATH_PATTERN = "File Path Pattern";
  public static final String RULE_KEY = "ruleKey";
  private static final String PROPERTY_RULE_KEY_PATTERN = "Rule Key Pattern";

  public static final String BLOCK_SUFFIX = ".block";
  public static final String PATTERNS_BLOCK_KEY = EXCLUSION_KEY_PREFIX + BLOCK_SUFFIX;
  public static final String BEGIN_BLOCK_REGEXP = "beginBlockRegexp";
  public static final String END_BLOCK_REGEXP = "endBlockRegexp";

  public static final String ALLFILE_SUFFIX = ".allfile";
  public static final String PATTERNS_ALLFILE_KEY = EXCLUSION_KEY_PREFIX + ALLFILE_SUFFIX;
  public static final String FILE_REGEXP = "fileRegexp";

  private IgnoreIssuesConfiguration() {
    // static configuration declaration only
  }

  static final int LARGE_SIZE = 40;
  static final int SMALL_SIZE = 10;

  public static List<PropertyDefinition> getPropertyDefinitions() {
    return ImmutableList.of(
      PropertyDefinition.builder(PATTERNS_MULTICRITERIA_EXCLUSION_KEY)
        .category(CoreProperties.CATEGORY_EXCLUSIONS)
        .subCategory(SUB_CATEGORY_IGNORE_ISSUES)
        .name("Multi-criteria Exclusion Patterns")
        .description("Patterns used to identify which issues are ignored.")
        .onQualifiers(Qualifiers.PROJECT)
        .index(3)
        .fields(
          PropertyFieldDefinition.build(RULE_KEY)
            .name(PROPERTY_RULE_KEY_PATTERN)
            .description("Pattern used to match rules which should be ignored.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build(),
          PropertyFieldDefinition.build(RESOURCE_KEY)
            .name(PROPERTY_FILE_PATH_PATTERN)
            .description("Pattern used to match files which should be ignored.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build())
        .build(),
      PropertyDefinition.builder(PATTERNS_BLOCK_KEY)
        .category(CoreProperties.CATEGORY_EXCLUSIONS)
        .subCategory(SUB_CATEGORY_IGNORE_ISSUES)
        .name("Block Exclusion Patterns")
        .description("Patterns used to identify blocks in which issues are ignored.")
        .onQualifiers(Qualifiers.PROJECT)
        .index(2)
        .fields(
          PropertyFieldDefinition.build(BEGIN_BLOCK_REGEXP)
            .name("Regular Expression for Start of Block")
            .description("If this regular expression is found in a file, then following lines are ignored until end of block.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build(),
          PropertyFieldDefinition.build(END_BLOCK_REGEXP)
            .name("Regular Expression for End of Block")
            .description("If specified, this regular expression is used to determine the end of code blocks to ignore. If not, then block ends at the end of file.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build())
        .build(),
      PropertyDefinition.builder(PATTERNS_ALLFILE_KEY)
        .category(CoreProperties.CATEGORY_EXCLUSIONS)
        .subCategory(SUB_CATEGORY_IGNORE_ISSUES)
        .name("File Exclusion Patterns")
        .description("Patterns used to identify files in which issues are ignored.")
        .onQualifiers(Qualifiers.PROJECT)
        .index(1)
        .fields(
          PropertyFieldDefinition.build(FILE_REGEXP)
            .name("Regular Expression")
            .description("If this regular expression is found in a file, then the whole file is ignored.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build())
        .build(),
      PropertyDefinition.builder(PATTERNS_MULTICRITERIA_INCLUSION_KEY)
        .category(CoreProperties.CATEGORY_EXCLUSIONS)
        .subCategory(SUB_CATEGORY_IGNORE_ISSUES)
        .name("Multi-criteria Inclusion Patterns")
        .description("Patterns used to identify which issues should be enforced on selected resources.")
        .onQualifiers(Qualifiers.PROJECT)
        .index(4)
        .fields(
          PropertyFieldDefinition.build(RULE_KEY)
            .name(PROPERTY_RULE_KEY_PATTERN)
            .description("Pattern used to match rules which should be enforced.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build(),
          PropertyFieldDefinition.build(RESOURCE_KEY)
            .name(PROPERTY_FILE_PATH_PATTERN)
            .description("Pattern used to match files on which issues should be enforced.")
            .type(PropertyType.STRING)
            .indicativeSize(LARGE_SIZE)
            .build())
        .build());
  }
}
