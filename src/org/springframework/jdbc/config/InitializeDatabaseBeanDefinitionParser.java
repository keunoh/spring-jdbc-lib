package org.springframework.jdbc.config;

import org.w3c.dom.Element;

public class InitializeDatabaseBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializer.class);
        builder.addPropertyReference("dataSource", element.getAttribute("data-source"));
        builder.addPropertyValue("enabled", element.getAttribute("enabled"));
        DatabasePopulatorConfigUtils.setDatabasePopulator(element, builder);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
        return builder.getBeanDefinition();
    }

    @Override
    protected boolean shouldGenerateId() { return true; }
}
