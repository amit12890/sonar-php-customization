/*
 * SonarQube PHP Custom Rules Example
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.samples.php.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.php.api.PHPKeyword;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.ClassPropertyDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Example of implementation of a check by extending {@link PHPVisitorCheck}.
 * PHPVisitorCheck provides methods to visit nodes of the Abstract Syntax Tree
 * that represents the source code.
 * <p>
 * Those methods can be overridden to process information
 * related to node and issue can be created via the context that can be
 * accessed through {@link PHPVisitorCheck#context()}.
 */
@Rule(
  key = ProtectedMembersUseCheck.KEY,
  priority = Priority.MAJOR,
  name = "Protected class member should not be used.",
  tags = {"convention"},
// Description can either be given in this annotation or through HTML name <ruleKey>.html located in package src/resources/org/sonar/l10n/php/rules/<repositoryKey>
 description = "<p>The use of protected class member is discouraged</p>"
  )
public class ProtectedMembersUseCheck extends PHPSubscriptionCheck {

  public static final String KEY = "S3";
  private static final String MESSAGE = "Use of protected class members is discouraged.";

  private static final Set<String> VISIBILITIES = ImmutableSet.of(
          PHPKeyword.PROTECTED.getValue());

  @Override
  public List<Kind> nodesToVisit() {
    return ImmutableList.of(Kind.CLASS_PROPERTY_DECLARATION, Kind.METHOD_DECLARATION);
  }

  @Override
  public void visitNode(Tree tree) {

    if(tree.getKind().equals(Kind.CLASS_PROPERTY_DECLARATION)){
      ClassPropertyDeclarationTree property = (ClassPropertyDeclarationTree) tree;
      if(hasProtectedVisibilityModifier(property)){
        System.out.println("Class property:"+ property);
        String message = String.format(MESSAGE, property);
        //context().newFileIssue(this, message)
        context().newIssue(this, property, MESSAGE);
      }

    }else{
      MethodDeclarationTree method = (MethodDeclarationTree) tree;
      if(hasProtectedVisibilityModifier(method)){
        System.out.println("Method:"+ method);
        String message = String.format(MESSAGE, method);
        //context().newFileIssue(this, message)
        context().newIssue(this, method, MESSAGE);
      }

    }

  }


  private static boolean hasProtectedVisibilityModifier(MethodDeclarationTree method) {
    for (SyntaxToken modifier : method.modifiers()) {
      if (method.modifiers().size() == 1 && VISIBILITIES.contains(modifier.text().toLowerCase(Locale.ENGLISH))) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasProtectedVisibilityModifier(ClassPropertyDeclarationTree property) {
    for (SyntaxToken modifier : property.modifierTokens()) {
      if (VISIBILITIES.contains(modifier.text().toLowerCase(Locale.ENGLISH))) {
        return true;
      }
    }
    return false;
  }
}
