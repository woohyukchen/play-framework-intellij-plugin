package com.intellij.frameworks.play;

import com.intellij.play.language.PlayLanguage;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class PlayLanguageSubstitutorTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.addFileToProject("play/mvc/Controller.java", "package play.mvc; public class Controller {}");
  }

  public void testHtmlOutsideViewsKeepsOriginalLanguage() {
    var file = myFixture.addFileToProject("web/index.html", "<html/>");

    assertNotSame(PlayLanguage.INSTANCE, file.getLanguage());
  }

  public void testHtmlUnderViewsUsesPlayLanguage() {
    var file = myFixture.addFileToProject("app/views/Application/index.html", "<html/>");

    assertSame(PlayLanguage.INSTANCE, file.getLanguage());
  }
}
