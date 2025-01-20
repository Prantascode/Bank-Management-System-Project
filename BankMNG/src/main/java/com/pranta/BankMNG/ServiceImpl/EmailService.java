package com.pranta.BankMNG.ServiceImpl;

import com.pranta.BankMNG.Dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    
}
