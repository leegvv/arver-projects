package net.arver.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * 生成mapper接口通用父接口插件.
 * @author li gu
 * @version 1.0.0.0
 **/
public class BaseMapperInterfacePlugin extends PluginAdapter {

    private static final String DEFAULT_MAPPER_SUPER_CLASS = ".BaseGeneratedMapper";
    private static final FullyQualifiedJavaType PARAM_ANNOTATION_TYPE =
            new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
    private static final FullyQualifiedJavaType LIST_TYPE = FullyQualifiedJavaType.getNewListInstance();
    private static final FullyQualifiedJavaType SERIALIZABLE_TYPE = new FullyQualifiedJavaType("java.io.Serializable");

    private List<Method> methods = new ArrayList<>();

    private ShellCallback shellCallback = null;

    public BaseMapperInterfacePlugin () {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(final IntrospectedTable introspectedTable) {
        final boolean hasPk = introspectedTable.hasPrimaryKeyColumns();
        final JavaFormatter javaFormatter = context.getJavaFormatter();
        final String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        final String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        final String javaFileEncoding = context.getProperty("javaFileEncoding");
        final Interface mapperInterface = new Interface(targetPackage + DEFAULT_MAPPER_SUPER_CLASS);

        final List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<>();
        if (stringHasValue(targetPackage)) {
            mapperInterface.addImportedType(PARAM_ANNOTATION_TYPE);
            mapperInterface.addImportedType(LIST_TYPE);
            mapperInterface.addImportedType(SERIALIZABLE_TYPE);

            mapperInterface.setVisibility(JavaVisibility.PUBLIC);
            mapperInterface.addJavaDocLine("/**");
            mapperInterface.addJavaDocLine(" * " + "DAO公共基类，由MybatisGenerator自动生成请勿修改.");
            mapperInterface.addJavaDocLine(" * " + "@param <Entity> The Model Class 这里是泛型不是Model类");
            mapperInterface.addJavaDocLine(" * " + "@param <PK> The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类");
            if (isUserExample()) {
                mapperInterface.addJavaDocLine(" * " + "@param <Example> The Example Class");
            }
            mapperInterface.addJavaDocLine(" */");
            final FullyQualifiedJavaType baseMapperInterfaceJavaType = mapperInterface.getType();
            baseMapperInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("Entity"));
            baseMapperInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("PK extends Serializable"));
            if (isUserExample()) {
                baseMapperInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("Example"));
            }
            if (!this.methods.isEmpty()) {
                for (final Method method : methods) {
                    mapperInterface.addMethod(method);
                }
            }
            final List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();
            for (final GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
                final CompilationUnit compilationUnit = generatedJavaFile.getCompilationUnit();
                final FullyQualifiedJavaType type = compilationUnit.getType();
                final String shortName = type.getShortName();
                if (shortName.endsWith("DAO")) {

                }
            }

            try {
                final GeneratedJavaFile mapperJavaFile = new GeneratedJavaFile(mapperInterface, targetProject, javaFileEncoding, javaFormatter);
                final File mapperDir = shellCallback.getDirectory(targetProject, targetPackage);
                final File mapperFile = new File(mapperDir, mapperJavaFile.getFileName());
                if (!mapperFile.exists()) {
                    mapperJavaFiles.add(mapperJavaFile);
                }
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }
        this.methods.clear();

        return mapperJavaFiles;
    }

    @Override
    public boolean clientGenerated(final Interface interfaze, final IntrospectedTable introspectedTable) {
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * " + interfaze.getType().getShortName() + "继承基类");
        interfaze.addJavaDocLine(" */");

        final String mapperSuperClass = interfaze.getType().getPackageName() + DEFAULT_MAPPER_SUPER_CLASS;
        final FullyQualifiedJavaType mapperSuperType = new FullyQualifiedJavaType(mapperSuperClass);
        final String targetPackage = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        // final String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        final String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        final FullyQualifiedJavaType baseModelJavaType = new FullyQualifiedJavaType(targetPackage + "." + domainObjectName);
        mapperSuperType.addTypeArgument(baseModelJavaType);
        FullyQualifiedJavaType primaryKeyTypeJavaType = null;
        if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
            primaryKeyTypeJavaType = new FullyQualifiedJavaType(targetPackage + "." + domainObjectName + "key");
        } else if (introspectedTable.hasPrimaryKeyColumns()) {
            primaryKeyTypeJavaType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        } else {
            primaryKeyTypeJavaType = baseModelJavaType;
        }
        mapperSuperType.addTypeArgument(primaryKeyTypeJavaType);
        interfaze.addImportedType(primaryKeyTypeJavaType);
        if (isUserExample()) {
            final String exampleType = introspectedTable.getExampleType();
            final FullyQualifiedJavaType exampleTypeJavaType = new FullyQualifiedJavaType(exampleType);
            mapperSuperType.addTypeArgument(exampleTypeJavaType);
            interfaze.addImportedType(exampleTypeJavaType);
        }
        interfaze.addImportedType(baseModelJavaType);
        interfaze.addImportedType(mapperSuperType);
        interfaze.addSuperInterface(mapperSuperType);
        return true;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptExampleParam(method);
        }
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptExampleParam(method);
        }
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptPrimaryKeyParam(method);
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptEntityParam(method);
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptEntityParam(method);
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptExampleParam(method);
            method.setReturnType(new FullyQualifiedJavaType("List<Entity>"));
        }
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptExampleParam(method);
            method.setReturnType(new FullyQualifiedJavaType("List<Entity>"));
        }
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptPrimaryKeyParam(method);
        method.setReturnType(new FullyQualifiedJavaType("Entity"));
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptEntityAndExampleParam(method);
        }
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptEntityAndExampleParam(method);
        }
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        if (isUserExample()) {
            interceptEntityAndExampleParam(method);
        }
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptEntityParam(method);
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptEntityParam(method);
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(final Method method, final Interface interfaze, final IntrospectedTable introspectedTable) {
        interceptEntityParam(method);
        return false;
    }

    private boolean isUserExample() {
        return "true".equals(getProperties().getProperty("useExample"));
    }

    private void interceptExampleParam(final Method method) {
        if (isUserExample()) {
            method.getParameters().clear();
            method.addParameter(new Parameter(new FullyQualifiedJavaType("Example"), "example"));
            methods.add(method);
        }
    }

    private void interceptPrimaryKeyParam(final Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("PK"), "id"));
        methods.add(method);
    }

    private void interceptEntityParam(final Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Entity"), "entity"));
        methods.add(method);
    }

    private void interceptEntityAndExampleParam(final Method method) {
        if (isUserExample()) {
            final List<Parameter> parameters = method.getParameters();
            if (parameters.size() == 1) {
                interceptExampleParam(method);
            } else {
                method.getParameters().clear();
                final Parameter parameter1 = new Parameter(new FullyQualifiedJavaType("Entity"), "entity");
                parameter1.addAnnotation("@Param(\"entity\")");
                method.addParameter(parameter1);

                final Parameter parameter2 = new Parameter(new FullyQualifiedJavaType("Example"), "example");
                parameter2.addAnnotation("@Param(\"example\")");
                method.addParameter(parameter2);
                methods.add(method);
            }
        }
    }
}
