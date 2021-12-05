package net.arver.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * JavaTypeResolverJsr310Impl.
 * @author leegvv
 */
public class JavaTypeResolverJsr310Impl extends JavaTypeResolverDefaultImpl {

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(final IntrospectedColumn column, final FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;
        switch (column.getJdbcType()) {
            case Types.BIT:
                answer = calculateBitReplacement(column, defaultType);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                answer = calculateBigDecimalReplacement(column, defaultType);
                break;
            case Types.DATE:
                answer = new FullyQualifiedJavaType(LocalDate.class.getName());
                break;
            case Types.TIME:
                answer = new FullyQualifiedJavaType(LocalTime.class.getName());
                break;
            case Types.TIMESTAMP:
                answer = new FullyQualifiedJavaType(LocalDateTime.class.getName());
                break;
            case Types.TINYINT:
                answer = new FullyQualifiedJavaType(Integer.class.getName());
                break;
            case Types.SMALLINT:
                answer = new FullyQualifiedJavaType(Integer.class.getName());
                break;
            default:
                break;
        }
        return answer;
    }
}
