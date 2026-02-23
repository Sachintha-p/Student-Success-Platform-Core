package com.sliit.studentplatform.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated API response wrapper for list endpoints.
 *
 * @param <T> element type in the page
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean last;
  private boolean first;

  /**
   * Convenience factory that wraps a Spring Data {@link Page}.
   *
   * @param page the page returned by a repository call
   * @param <T>  element type
   * @return a populated {@link PagedResponse}
   */
  public static <T> PagedResponse<T> of(Page<T> page) {
    return PagedResponse.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }
}
