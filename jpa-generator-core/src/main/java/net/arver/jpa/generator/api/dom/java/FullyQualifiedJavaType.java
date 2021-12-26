package net.arver.jpa.generator.api.dom.java;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class FullyQualifiedJavaType implements Comparable<FullyQualifiedJavaType>{

    private static final String JAVA_LANG = "java.lang";

    private static FullyQualifiedJavaType intInstance = null;

    private static FullyQualifiedJavaType stringInstance = null;

    private static FullyQualifiedJavaType booleanPrimitiveInstance = null;

    private static FullyQualifiedJavaType objectInstance = null;

    private static FullyQualifiedJavaType dateInstance = null;

    private static FullyQualifiedJavaType criteriaInstance = null;

    private static FullyQualifiedJavaType generatedCriteriaInstance = null;

    /** The short name without any generic arguments. */
    private String baseShortName;

    /** The fully qualified name without any generic arguments. */
    private String baseQualifiedName;

    private boolean explicitlyImported;

    private String packageName;

    private boolean primitive;

    private boolean isArray;

    private PrimitiveTypeWrapper primitiveTypeWrapper;

    private final List<FullyQualifiedJavaType> typeArguments;

    // the following three values are used for dealing with wildcard types
    private boolean wildcardType;

    private boolean boundedWildcard;

    private boolean extendsBoundedWildcard;

    public FullyQualifiedJavaType(final String fullTypeSpecification) {
        super();
        typeArguments = new ArrayList<>();
        parse(fullTypeSpecification);
    }

    public String getFullyQualifiedName() {
        final StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    @Override
    public int compareTo(final FullyQualifiedJavaType o) {
        return 0;
    }

    private void parse(final String fullTypeSpecification) {
        String spec = fullTypeSpecification.trim();
        if (spec.startsWith("?")) {
            wildcardType = true;
            spec = spec.substring(1).trim();
            if (spec.startsWith("extends ")) {
                boundedWildcard = true;
                extendsBoundedWildcard = true;
                spec = spec.substring(8);
            } else if (spec.startsWith("super ")) {
                boundedWildcard = true;
                extendsBoundedWildcard = true;
                spec = spec.substring(6);
            } else{
                boundedWildcard = false;
            }
            parse(spec);
        } else {

        }
    }


}
