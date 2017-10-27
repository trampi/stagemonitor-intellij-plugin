package org.stagemonitor;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.properties.psi.Property;
import com.intellij.psi.PsiElement;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

public class StagemonitorDocumentationProvider extends AbstractDocumentationProvider {

	private final StagemonitorCompletionProvider stagemonitorCompletionProvider = new StagemonitorCompletionProvider();

	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return null;
	}

	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if (element instanceof Property) {
			final String key = ((Property) element).getKey();
			return stagemonitorCompletionProvider.getAllConfigurationOptions()
					.stream()
					.filter(c -> c.getKey().equals(key))
					.findFirst()
					.map(configurationOption
							-> ObjectUtils.firstNonNull(configurationOption.getDescription(), configurationOption.getLabel()))
					.orElse(null);
		} else {
			return null;
		}
	}

}
