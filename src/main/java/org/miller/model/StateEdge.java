package org.miller.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class StateEdge {

  private String label;
  private String value;

  @Override
  public String toString() {
    return label + ", " + value;
  }
}
