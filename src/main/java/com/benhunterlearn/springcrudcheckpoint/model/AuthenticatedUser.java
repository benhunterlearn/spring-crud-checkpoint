package com.benhunterlearn.springcrudcheckpoint.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticatedUser {
    private boolean authenticated;
    private User user;
}
