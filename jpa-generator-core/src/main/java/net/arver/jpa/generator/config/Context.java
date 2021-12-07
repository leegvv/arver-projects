package net.arver.jpa.generator.config;

import java.util.ArrayList;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class Context extends PropertyHolder{

    private String id;

    private JDBCConnectionConfiguration jdbcConnectionConfiguration;

    private ConnectionFactoryConfiguration connectionFactoryConfiguration;

    private JavaTypeResolverConfiguration javaTypeResolverConfiguration;

    private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;

    private JavaClientGeneratorConfiguration javaClientGeneratorConfiguration;

    private final ArrayList<TableConfiguration> tableConfigurations;

    private final ModelType defaultModelType;

    private String beginningDelimiter = "\"";

    private String endingDelimiter = "\"";

    private CommentGeneratorConfiguration commentGeneratorConfiguration;

    private CommentGenerator commentGenerator;

    private PluginAggregator pluginAggregator;

    private final List<PluginConfiguration> pluginConfigurations;

    private String targetRuntime;

    private String introspectedColumnImpl;

    private Boolean autoDelimitKeywords;

    private JavaFormatter javaFormatter;

    private XmlFormatter xmlFormatter;

    public Context(final ModelType defaultModelType) {
        super();

        if (defaultModelType == null) {
            this.defaultModelType = ModelType.CONDITIONAL;
        } else {
            this.defaultModelType = defaultModelType;
        }

        tableConfigurations = new ArrayList<>();
        pluginConfigurations = new ArrayList<>();
    }



}
