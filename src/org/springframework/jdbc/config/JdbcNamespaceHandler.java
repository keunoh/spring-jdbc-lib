package org.springframework.jdbc.config;

public class JdbcNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("embedded-database", new EmbeddedDatabaseBeanDefinitionParser());
        registerBeanDefinitionParser("initialize-database", new InitializeDatabaseBeanDefinitionParser());
    }
}
