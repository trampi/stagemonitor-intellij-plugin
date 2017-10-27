package org.stagemonitor;

import com.intellij.lang.properties.psi.impl.PropertyKeyImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;

import org.jetbrains.annotations.NotNull;

class StagemonitorPropertyReferenceProvider extends PsiReferenceProvider {

	private final StagemonitorCompletionProvider stagemonitorCompletionProvider = new StagemonitorCompletionProvider();

	@NotNull
	@Override
	public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
		if (element instanceof PropertyKeyImpl) {
			final String key = element.getText();
			return stagemonitorCompletionProvider.getAllConfigurationOptions()
					.stream()
					.filter(c -> c.getKey().equals(key))
					.findFirst()
					.map(configurationOptionDescription -> new PsiReference[]{configurationOptionDescription.getMethodReference()})
					.orElse(PsiReference.EMPTY_ARRAY);
		} else {
			return PsiReference.EMPTY_ARRAY;
		}
	}



}
