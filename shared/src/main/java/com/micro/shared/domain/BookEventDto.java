package com.micro.shared.domain;

import java.util.List;
import java.util.UUID;

import com.micro.shared.models.DtoBase;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookEventDto extends DtoBase {
    private String title;
    private String description;
    private List<UUID> authors;
}
