package com.pranta.BankMNG.ServiceImpl;

import com.pranta.BankMNG.Dto.BankResponse;
import com.pranta.BankMNG.Dto.CreditDebitRequest;
import com.pranta.BankMNG.Dto.EnquiryRequest;
import com.pranta.BankMNG.Dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
