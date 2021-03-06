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
package org.sonar.core.technicaldebt.functions;

import com.google.common.collect.Maps;
import org.sonar.api.BatchComponent;
import org.sonar.api.issue.Issue;
import org.sonar.api.rules.Violation;
import org.sonar.core.technicaldebt.TechnicalDebtRequirement;

import java.util.Collection;
import java.util.Map;

public class Functions implements BatchComponent {

  private final Map<String, Function> functionsByKey = Maps.newHashMap();

  public Functions(final Function[] functions) {
    for (Function function : functions) {
      functionsByKey.put(function.getKey(), function);
    }
  }

  Function getFunction(String key) {
    return functionsByKey.get(key);
  }

  public Function getFunction(TechnicalDebtRequirement requirement) {
    return getFunction(requirement.getRemediationFunction());
  }

  public double costInHours(TechnicalDebtRequirement requirement, Collection<Violation> violations) {
    return getFunction(requirement).costInHours(requirement, violations);
  }

  public long costInMinutes(TechnicalDebtRequirement requirement, Issue issue) {
    return getFunction(requirement).costInMinutes(requirement, issue);
  }

}
