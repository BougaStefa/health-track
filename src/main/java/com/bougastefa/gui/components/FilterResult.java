package com.bougastefa.gui.components;

import java.util.List;
import java.util.function.Function;

/**
 * Utility class that facilitates the application of filters to collections of entities.
 * This class implements a fluent interface pattern that allows for chaining multiple filter
 * operations together, making the code more readable and maintainable.
 * It's designed to work with any type of entity object by using Java generics.
 *
 * @param <T> The type of entities being filtered (e.g., Patient, Doctor, Prescription)
 */
public class FilterResult<T> {
  /**
   * The current list of items after any filters have been applied.
   * This list is immutable to prevent unintended modifications.
   */
  private final List<T> items;

  /**
   * Constructs a new FilterResult containing the provided list of items.
   * This is typically used to initialize the filtering process with an unfiltered list.
   *
   * @param items The initial collection of items to be filtered
   */
  public FilterResult(List<T> items) {
    this.items = items;
  }

  /**
   * Applies a string-based filter to the current list of items.
   * This method filters items based on whether a specific string field of each item
   * contains the provided filter value (case-insensitive). If the filter value is
   * null or empty, no filtering is performed and the current list is returned unchanged.
   *
   * @param fieldValue The value to filter by (search term); filtering is skipped if null or empty
   * @param getter A function that extracts the string field to compare against from each item
   * @return A new FilterResult containing only the items that match the filter criteria
   */
  public FilterResult<T> filter(String fieldValue, Function<T, String> getter) {
    // Skip filtering if the filter value is null or empty
    if (fieldValue == null || fieldValue.isEmpty()) {
      return this;
    }

    // Apply the filter using Java streams
    List<T> filtered =
        items.stream()
            .filter(
                item -> {
                  String value = getter.apply(item);
                  // Case-insensitive contains check, with null safety
                  return value != null && value.toLowerCase().contains(fieldValue.toLowerCase());
                })
            .toList();

    // Return a new FilterResult with the filtered items
    return new FilterResult<>(filtered);
  }

  /**
   * Returns the final list of items after all filters have been applied.
   * This method is typically called at the end of a chain of filter operations
   * to retrieve the filtered collection for display or further processing.
   *
   * @return The filtered list of items
   */
  public List<T> getResults() {
    return items;
  }
}
