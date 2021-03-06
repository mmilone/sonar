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
package org.sonar.core.technicaldebt;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sonar.api.rule.RuleKey;

import javax.annotation.CheckForNull;

import java.util.List;

public class DefaultRequirement {

  public static final String FUNCTION_CONSTANT = "constant_resource";
  public static final String FUNCTION_LINEAR = "linear";
  public static final String FUNCTION_LINEAR_WITH_OFFSET = "linear_offset";
  public static final String FUNCTION_LINEAR_WITH_THRESHOLD = "linear_threshold";

  public static final List<String> FUNCTIONS = ImmutableList.of(FUNCTION_CONSTANT, FUNCTION_LINEAR, FUNCTION_LINEAR_WITH_OFFSET, FUNCTION_LINEAR_WITH_THRESHOLD);

  private RuleKey ruleKey;
  private DefaultCharacteristic rootCharacteristic;
  private DefaultCharacteristic characteristic;

  private String function;
  private WorkUnit factor;
  private WorkUnit offset;

  public RuleKey ruleKey() {
    return ruleKey;
  }

  public DefaultRequirement setRuleKey(RuleKey ruleKey) {
    this.ruleKey = ruleKey;
    return this;
  }

  public DefaultCharacteristic rootCharacteristic() {
    return rootCharacteristic;
  }

  public DefaultRequirement setRootCharacteristic(DefaultCharacteristic rootCharacteristic) {
    this.rootCharacteristic = rootCharacteristic;
    return this;
  }

  public DefaultCharacteristic characteristic() {
    return characteristic;
  }

  public DefaultRequirement setCharacteristic(DefaultCharacteristic characteristic) {
    this.characteristic = characteristic;
    return this;
  }

  public DefaultRequirement addProperty(DefaultRequirementProperty property) {
    if (property.key().equals(DefaultRequirementProperty.PROPERTY_REMEDIATION_FUNCTION)) {
      String textValue = property.textValue();
      if (!FUNCTIONS.contains(textValue)) {
        throw new IllegalArgumentException("Function is not valid. Should be one of : "+ FUNCTIONS);
      }
      this.function = property.textValue();
    } else if (property.key().equals(DefaultRequirementProperty.PROPERTY_REMEDIATION_FACTOR)) {
      this.factor = WorkUnit.create(property.value(), property.textValue());
    } else if (property.key().equals(DefaultRequirementProperty.PROPERTY_OFFSET)) {
      this.offset = WorkUnit.create(property.value(), property.textValue());
    } else {
      throw new IllegalArgumentException("Property key is not found");
    }
    return this;
  }

  public String function() {
    return function;
  }

  public WorkUnit factor() {
    return factor;
  }

  @CheckForNull
  public WorkUnit offset() {
    return offset;
  }

  public boolean isConstant(){
    return FUNCTION_CONSTANT.equals(function);
  }

  public boolean isLinear(){
    return FUNCTION_LINEAR.equals(function);
  }

  public boolean isLinearWithThreshold(){
    return FUNCTION_LINEAR_WITH_THRESHOLD.equals(function);
  }

  public boolean isLinearWithOffset(){
    return FUNCTION_LINEAR_WITH_OFFSET.equals(function);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
