package org.miller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NodeEquation {

  private int id;
  private String equation;
  private String status;
}
