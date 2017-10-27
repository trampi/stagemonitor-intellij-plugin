package org.stagemonitor.intellij;

import com.intellij.lang.properties.parsing.PropertiesTokenTypes;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

import org.jetbrains.annotations.NotNull;

public class StagemonitorPropertyReferenceContributor extends PsiReferenceContributor {

	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(
				PlatformPatterns.psiElement(PropertiesTokenTypes.KEY_CHARACTERS),
				new StagemonitorPropertyReferenceProvider()
		);
	}

}
