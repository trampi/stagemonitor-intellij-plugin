import org.stagemonitor.configuration.ConfigurationOptionProvider;
import org.stagemonitor.configuration.ConfigurationOption;

public class SomeClass extends ConfigurationOptionProvider {

	private static final String ITEM_DETAIL = "Item Detail";

	private ConfigurationOption<Boolean> useMarkdownProcessorForArticleTexts = ConfigurationOption.booleanOption()
			.key("article.text.useMarkdownProcessor")
			.dynamic(true)
			.label("Use Markdown Processor")
			.description("When set to true, a markdown processor is used to convert the markdown in "
					+ " an article text to HTML."
					+ " something" + ("some " + " thing") + ("something"))
			.defaultValue(false)
			.configurationCategory(ITEM_DETAIL)
			.tags("markdown")
			.build();

}
