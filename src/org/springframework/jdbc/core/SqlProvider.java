package org.springframework.jdbc.core;


import org.springframework.jdbc.myannotation.Nullable;

public interface SqlProvider {

    @Nullable
    String getSql();
}
