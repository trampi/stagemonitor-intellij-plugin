package org.stagemonitor.intellij;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.properties.parsing.PropertiesTokenTypes;
import com.intellij.patterns.PlatformPatterns;

public class StagemonitorCompletionContributor extends CompletionContributor {

	public StagemonitorCompletionContributor() {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(PropertiesTokenTypes.KEY_CHARACTERS), new StagemonitorCompletionProvider());
	}


}
