package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto extends UserShortDto {
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @Builder(builderMethodName = "childBuilder")
    public UserDto(Long id, @NotNull String name, String email) {
        super(id, name);
        this.email = email;
    }
}
