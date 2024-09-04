package com.tranhuy105.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageInstructionDTO {
    private Integer id;
    private String name;
    private String instruction;
    private boolean main;
}
