package org.springframework.jdbc.core;

import org.springframework.jdbc.myannotation.Nullable;

public class ArgumentTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    @Nullable
    private final Object[] args;

    @Nullable
    private final int[] argTypes;

    public ArgumentTypePreparedStatementSetter(@Nullable Object[] args, @Nullable int[] argTypes) {
        if ((args != null && argTypes == null) || (args == null && argTypes != null) ||
                (args != null && args.length != argTypes.length)) {
            throw new
        }
    }
}
