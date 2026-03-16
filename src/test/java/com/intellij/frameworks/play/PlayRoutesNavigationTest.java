package com.intellij.frameworks.play;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class PlayRoutesNavigationTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.addFileToProject("play/mvc/Controller.java", "package play.mvc; public class Controller {}");
    myFixture.addFileToProject("controllers/Application.java",
                               "package controllers; public class Application extends play.mvc.Controller { public static void index() {} }");
  }

  public void testRouteControllerAndActionReferences() {
    myFixture.configureByText("routes", "GET / controllers.Application.index");

    PsiReference controllerReference = myFixture.getFile().findReferenceAt(myFixture.getFile().getText().indexOf("Application"));
    assertNotNull(controllerReference);
    PsiElement controllerTarget = controllerReference.resolve();
    assertNotNull(controllerTarget);
    assertTrue(controllerTarget instanceof PsiClass);
    assertEquals("Application", ((PsiClass)controllerTarget).getName());

    PsiReference actionReference = myFixture.getFile().findReferenceAt(myFixture.getFile().getText().indexOf("index"));
    assertNotNull(actionReference);
    PsiElement actionTarget = actionReference.resolve();
    assertNotNull(actionTarget);
    assertTrue(actionTarget instanceof PsiMethod);
    assertEquals("index", ((PsiMethod)actionTarget).getName());
  }
}
