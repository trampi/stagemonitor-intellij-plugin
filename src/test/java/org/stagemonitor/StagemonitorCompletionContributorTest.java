package org.stagemonitor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import org.junit.Test;

import java.util.List;

public class StagemonitorCompletionContributorTest extends LightCodeInsightFixtureTestCase {

	protected String getTestDataPath() {
		return "testData/";
	}

	public void testSomething() {
		myFixture.configureByFile("/SomeClass.java");
		PsiFile file = myFixture.getFile();

		PsiClass configurationClass = (PsiClass)file.getChildren()[2];
		StagemonitorCompletionProvider stagemonitorCompletionProvider = new StagemonitorCompletionProvider();
		List<StagemonitorCompletionProvider.ConfigurationOptionDescription> optionDescriptionList = stagemonitorCompletionProvider.searchConfigurationOptions(configurationClass);
		assertSize(1, optionDescriptionList);
		assertEquals("When set to true, a markdown processor is used to convert the markdown in  an article text to HTML. somethingsome  thingsomething", optionDescriptionList.get(0).getDescription());
		assertEquals("article.text.useMarkdownProcessor", optionDescriptionList.get(0).getKey());

	}


}
