package org.springframework.jdbc.myannotation;

import java.lang.annotation.Annotation;

public interface TypeQualifierValidator<A extends Annotation> {
    public @Nonnull
    When forConstantValue(@Nonnull A annotation, Object value);
}
