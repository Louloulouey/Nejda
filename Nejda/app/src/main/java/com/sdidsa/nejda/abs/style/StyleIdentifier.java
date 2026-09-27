package com.sdidsa.nejda.abs.style;

import androidx.annotation.Nullable;

public record StyleIdentifier(boolean dark, String accent) {

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StyleIdentifier other) {
            return dark == other.dark && accent.equalsIgnoreCase(other.accent);
        }
        return false;
    }
}
