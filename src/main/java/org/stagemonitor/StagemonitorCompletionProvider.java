package org.stagemonitor;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParenthesizedExpression;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.ProcessingContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StagemonitorCompletionProvider extends CompletionProvider<CompletionParameters> {

	private static final Logger LOG = Logger.getInstance(StagemonitorCompletionProvider.class.getName());
	private static final List<String> configurationOptionCanonicalTypes = Arrays.asList(
			"org.stagemonitor.configuration.ConfigurationOption",
			"ConfigurationOption" // for tests
	);

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		try {
			if (parameters.getPosition().getContainingFile().getName().equals("stagemonitor.properties")) {
				for (PsiClass psiClass : getAllConfigurationClasses()) {
					searchConfigurationOptions(psiClass);
				}
			}
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	List<ConfigurationOptionDescription> searchConfigurationOptions(PsiClass psiClass) {
		List<ConfigurationOptionDescription> optionDescriptionList = new ArrayList<>();
		for (PsiField psiField : psiClass.getAllFields()) {
			// be sure to use source (if available)
			psiField = (PsiField) psiField.getNavigationElement();

			PsiExpression initializer = psiField.getInitializer();
			if (configurationOptionCanonicalTypes.contains(canonicalTypeWithoutGenerics(psiField)) && initializer != null) {
				ConfigurationOptionDescriptionResolver configurationOptionDescriptionResolver = new ConfigurationOptionDescriptionResolver();
				configurationOptionDescriptionResolver.visitElement(psiField);
				optionDescriptionList.add(configurationOptionDescriptionResolver.result);
			}
		}
		return optionDescriptionList;
	}

	private Collection<PsiClass> getAllConfigurationClasses() {
		Project project = ProjectManager.getInstance().getOpenProjects()[0];
		GlobalSearchScope scope = new EverythingGlobalScope(project);
		PsiClass configurationBaseClass = JavaPsiFacade.getInstance(project).findClass("org.stagemonitor.configuration.ConfigurationOptionProvider", scope);

		Collection<PsiClass> result = new ArrayList<>();
		if (configurationBaseClass != null) {
			// we seem to have stagemonitor enabled
			result.addAll(ClassInheritorsSearch.search(configurationBaseClass, scope, true).findAll());
		}

		return result;
	}

	private static String canonicalTypeWithoutGenerics(PsiField field) {
		String canonicalText = field.getType().getCanonicalText();
		int indexOfOpeningBracket = canonicalText.indexOf('<');
		return indexOfOpeningBracket == -1 ? canonicalText : canonicalText.substring(0, indexOfOpeningBracket);
	}

	static class ConfigurationOptionDescriptionResolver extends JavaRecursiveElementVisitor {

		private final ConfigurationOptionDescription result = new ConfigurationOptionDescription();

		@Override
		public void visitMethodCallExpression(PsiMethodCallExpression expression) {
			super.visitMethodCallExpression(expression);
			PsiElement possibleIdentifier = expression.getFirstChild().getLastChild();
			if (possibleIdentifier != null && possibleIdentifier instanceof PsiIdentifier) {
				PsiIdentifier identifier = ((PsiIdentifier) possibleIdentifier);


				if (identifier.getText().equals("description")) {
					PsiExpression argumentExpression = expression.getArgumentList().getExpressions()[0];
					result.description = reduceExpression(argumentExpression);
				} else if (identifier.getText().equals("key")) {
					PsiExpression argumentExpression = expression.getArgumentList().getExpressions()[0];
					result.key = reduceExpression(argumentExpression);
				}

			} else {
				LOG.warn("unknown expression type encountered: " + possibleIdentifier);
			}

		}

		public ConfigurationOptionDescription getResult() {
			return result;
		}

		private String reduceExpression(PsiExpression expression) {
			if (expression instanceof PsiPolyadicExpression) {
				StringBuilder reducedExpression = new StringBuilder();
				PsiPolyadicExpression polyadicExpression = (PsiPolyadicExpression) expression;
				for (PsiExpression operand : polyadicExpression.getOperands()) {
					reducedExpression.append(reduceExpression(operand));
				}
				return reducedExpression.toString();
			} else if (expression instanceof PsiLiteralExpression) {
				return String.valueOf(((PsiLiteralExpression) expression).getValue());
			} else if (expression instanceof PsiParenthesizedExpression) {
				return reduceExpression(((PsiParenthesizedExpression) expression).getExpression());
			} else {
				LOG.warn("unknown type encountered while reducing expression: " + expression.getType());
				return "";
			}
		}

	}

	public static class ConfigurationOptionDescription {
		private String key;
		private String description;

		public String getKey() {
			return key;
		}

		public String getDescription() {
			return description;
		}
	}


}
