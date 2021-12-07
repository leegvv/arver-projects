package net.arver.mybatis.maven.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Properties;

/**
 * CommentGenerator.
 * @author leegvv
 */
public class CommentGenerator extends DefaultCommentGenerator {

    private boolean addRemarkComments = false;
    private static final String EXAMPLE_SUFFIX = "Example";
    private static final String MAPPER_SUFFIX = "Mapper";
    private static final String API_MODEL_PROPERTY_FULL_CLASS_NAME = "io.swagger.annotations.ApiModelProperty";
    private boolean addSwaggerComments = false;

    @Override
    public void addConfigurationProperties(final Properties properties) {
        super.addConfigurationProperties(properties);
        this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
        this.addSwaggerComments = StringUtility.isTrue(properties.getProperty("addSwaggerComments"));
    }

    @Override
    public void addFieldComment(final Field field, final IntrospectedTable introspectedTable, final IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            field.addJavaDocLine("/**");
            final StringBuilder sb = new StringBuilder();
            sb.append(" * ");
            sb.append(remarks);
            sb.append(".");
            field.addJavaDocLine(sb.toString());
            field.addJavaDocLine(" */");
        }
        //给model的字段添加swagger注解
        if (addSwaggerComments) {
            if (remarks.contains("\"")) {
                remarks = remarks.replace("\"", "'");
            }
            field.addJavaDocLine("@ApiModelProperty(value = \"" + remarks + "\")");
        }
    }

    @Override
    public void addModelClassComment(final TopLevelClass topLevelClass, final IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getRemarks() + ".");
        topLevelClass.addJavaDocLine(" * @author " + System.getProperty("user.name"));
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(final InnerClass innerClass, final IntrospectedTable introspectedTable, final boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addJavaFileComment(final CompilationUnit compilationUnit) {
        super.addJavaFileComment(compilationUnit);
        if (!compilationUnit.getType().getFullyQualifiedName().contains(MAPPER_SUFFIX)
                && !compilationUnit.getType().getFullyQualifiedName().contains(EXAMPLE_SUFFIX) && addSwaggerComments) {
            compilationUnit.addImportedType(new FullyQualifiedJavaType(API_MODEL_PROPERTY_FULL_CLASS_NAME));
        }
    }

}
