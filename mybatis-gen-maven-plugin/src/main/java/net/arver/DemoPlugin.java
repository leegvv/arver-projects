package net.arver;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class DemoPlugin extends PluginAdapter {

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(final IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
    }
}
