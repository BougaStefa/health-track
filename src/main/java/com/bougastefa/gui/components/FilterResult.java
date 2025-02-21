package com.bougastefa.gui.components;

import java.util.List;
import java.util.function.Function;

// Utility class, helps with applying filters to a list of entities.
public class FilterResult<T> {
  private final List<T> items;

  public FilterResult(List<T> items) {
    this.items = items;
  }

  public FilterResult<T> filter(String fieldValue, Function<T, String> getter) {
    if (fieldValue == null || fieldValue.isEmpty()) {
      return this;
    }

    List<T> filtered =
        items.stream()
            .filter(
                item -> {
                  String value = getter.apply(item);
                  return value != null && value.toLowerCase().contains(fieldValue.toLowerCase());
                })
            .toList();

    return new FilterResult<>(filtered);
  }

  public List<T> getResults() {
    return items;
  }
}
