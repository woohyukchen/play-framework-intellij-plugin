package com.intellij.frameworks.play;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPlainText;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.IncorrectOperationException;

public class PlayPlainTextManipulatorTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.addFileToProject("play/mvc/Controller.java", "package play.mvc; public class Controller {}");
  }

  public void testRejectsNonPlayPlainTextFile() {
    PsiFile file = myFixture.addFileToProject("notes.txt", "alpha");
    PsiPlainText plainText = (PsiPlainText)file.getFirstChild();

    assertThrows(IncorrectOperationException.class,
                 () -> ElementManipulators.handleContentChange(plainText, new TextRange(0, 5), "beta"));
  }
}
