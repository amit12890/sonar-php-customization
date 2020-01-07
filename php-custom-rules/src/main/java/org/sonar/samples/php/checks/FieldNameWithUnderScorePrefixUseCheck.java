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
import org.sonar.plugins.php.api.tree.declaration.VariableDeclarationTree;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.List;
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
  key = FieldNameWithUnderScorePrefixUseCheck.KEY,
  priority = Priority.MAJOR,
  name = "Property name should not be prefixed with an underscore to indicate visibility.",
  tags = {"convention"},
// Description can either be given in this annotation or through HTML name <ruleKey>.html located in package src/resources/org/sonar/l10n/php/rules/<repositoryKey>
 description = "<p>Property name should not be prefixed with an underscore to indicate visibility</p>"
  )
public class FieldNameWithUnderScorePrefixUseCheck extends PHPSubscriptionCheck {

  public static final String KEY = "S4";
  private static final String MESSAGE = "Property name \"%s\" should not be prefixed with an underscore to indicate visibility";

  private static final Set<String> VISIBILITIES = ImmutableSet.of(
          PHPKeyword.PROTECTED.getValue());


  /*@Override
  public void visitVariableDeclaration(VariableDeclarationTree tree) {
    super.visitVariableDeclaration(tree);
    System.out.println("Class tree: " + tree);
  }*/


  private void checkVariable(VariableDeclarationTree member, @Nullable String className) {
    if (!member.equalToken().text().isEmpty()) {
      String memberName = member.identifier().text();
      if(memberName.startsWith("$_")){
        String message = String.format(MESSAGE, "variable", memberName);
        context().newIssue(this, member.identifier(), message);
      }

    }
  }

  @Override
  public List<Kind> nodesToVisit() {
    return ImmutableList.of(Kind.CLASS_PROPERTY_DECLARATION);
  }

  @Override
  public void visitNode(Tree tree) {
    //super.visitNode(tree);
    ClassPropertyDeclarationTree property = (ClassPropertyDeclarationTree) tree;
    if(property.hasModifiers()){
      //System.out.println("Inside");
      for (VariableDeclarationTree variableDeclarationTree : property.declarations()) {
        String propertyName = variableDeclarationTree.identifier().text();
        //System.out.println("Property name: " + propertyName);
        if(propertyName.startsWith("$_")){
          String message = String.format(MESSAGE, propertyName, propertyName);
          //context().newFileIssue(this, message)
          context().newIssue(this, variableDeclarationTree.identifier(), message);
        }
      }
    }

  }

}
