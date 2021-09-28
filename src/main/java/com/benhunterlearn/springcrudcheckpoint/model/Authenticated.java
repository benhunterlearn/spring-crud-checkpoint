package com.benhunterlearn.springcrudcheckpoint.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authenticated {
    private boolean authenticated;
    private User user;

//    @JsonValue
//    public String getJsonString() {
//        ObjectMapper mapper = new ObjectMapper();
//
//    }
}
