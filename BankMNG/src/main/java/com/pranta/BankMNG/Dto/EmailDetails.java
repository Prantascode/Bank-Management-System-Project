package com.pranta.BankMNG.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDetails {
    private String recipient;
    private String massageBody;
    private String subject;
    private String attachment;
}
