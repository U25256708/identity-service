package org.latinflavor.identity.shared.pagination;

public record SortSpec(String property, Direction direction) {

    public enum Direction {
        ASC,
        DESC
    }
}
