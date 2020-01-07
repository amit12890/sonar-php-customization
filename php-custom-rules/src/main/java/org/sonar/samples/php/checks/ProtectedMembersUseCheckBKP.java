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

import com.google.common.collect.ImmutableSet;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.php.api.PHPKeyword;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.ClassDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.ClassMemberTree;
import org.sonar.plugins.php.api.tree.declaration.ClassTree;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.VariableDeclarationTree;
import org.sonar.plugins.php.api.tree.expression.AnonymousClassTree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nullable;

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
  key = ProtectedMembersUseCheckBKP.KEY,
  priority = Priority.MAJOR,
  name = "Protected class member should not be used.",
  tags = {"convention"},
// Description can either be given in this annotation or through HTML name <ruleKey>.html located in package src/resources/org/sonar/l10n/php/rules/<repositoryKey>
 description = "<p>The use of protected class member is discouraged</p>"
  )
public class ProtectedMembersUseCheckBKP extends PHPVisitorCheck {

  public static final String KEY = "S3";
  private static final String MESSAGE = "Remove the protected visibility of this %s \"%s\".";

  private static final Set<String> VISIBILITIES = ImmutableSet.of(
          PHPKeyword.PROTECTED.getValue());

  @Override
  public void visitClassDeclaration(ClassDeclarationTree tree) {
    super.visitClassDeclaration(tree);

    if (tree.is(Kind.CLASS_DECLARATION)) {
      visitClass(tree, tree.name().text());
    }
  }

  @Override
  public void visitAnonymousClass(AnonymousClassTree tree) {
    super.visitAnonymousClass(tree);

    visitClass(tree, null);
  }

  private void visitClass(ClassTree classTree, @Nullable String name) {
    for (ClassMemberTree member : classTree.members()) {

      if (member.is(Kind.METHOD_DECLARATION)) {
        checkMethod((MethodDeclarationTree) member, name);
      }else if(member.is(Kind.VARIABLE_DECLARATION)){
        checkVariable((VariableDeclarationTree)member, name);
      }
    }
  }

  private void checkVariable(VariableDeclarationTree member, @Nullable String className) {
    if (hasVariableVisibilityModifier(member)) {
      String memberName = member.identifier().text();
      String message = String.format(MESSAGE, "variable", memberName);
      context().newIssue(this, member.identifier(), message);
    }
  }

  private void checkMethod(MethodDeclarationTree method, @Nullable String className) {
    if (hasVisibilityModifier(method)) {
      String methodName = method.name().text();
      String message = String.format(MESSAGE, getMethodKind(methodName, className), methodName);
      context().newIssue(this, method.name(), message);
    }
  }

  private static boolean hasVisibilityModifier(MethodDeclarationTree method) {
    for (SyntaxToken modifier : method.modifiers()) {
      if (VISIBILITIES.contains(modifier.text().toLowerCase(Locale.ENGLISH))) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasVariableVisibilityModifier(VariableDeclarationTree member) {
    if (VISIBILITIES.contains(member.equalToken().text().toLowerCase(Locale.ENGLISH))) {
      return true;
    }
    return false;
  }

  private static String getMethodKind(String methodName, @Nullable String className) {
    if ("__construct".equalsIgnoreCase(methodName) || methodName.equalsIgnoreCase(className)) {
      return "constructor";

    } else if ("__destruct".equalsIgnoreCase(methodName)) {
      return "destructor";

    } else {
      return "method";
    }
  }

}
