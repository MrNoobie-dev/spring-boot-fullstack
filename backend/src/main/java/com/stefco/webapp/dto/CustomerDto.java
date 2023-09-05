package com.stefco.webapp.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CustomerDto {

    private final String name;
    private final String email;
    private final Integer age;
}
