package ru.netology.cloudstorage.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @NotNull
    String fileName;

    @NotNull
    Integer size;
}
