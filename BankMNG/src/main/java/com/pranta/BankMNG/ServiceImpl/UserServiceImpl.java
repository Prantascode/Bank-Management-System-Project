package com.pranta.BankMNG.ServiceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.BankMNG.Dto.AccountInfo;
import com.pranta.BankMNG.Dto.BankResponse;
import com.pranta.BankMNG.Dto.CreditDebitRequest;
import com.pranta.BankMNG.Dto.EmailDetails;
import com.pranta.BankMNG.Dto.EnquiryRequest;
import com.pranta.BankMNG.Dto.UserRequest;
import com.pranta.BankMNG.Entity.User;
import com.pranta.BankMNG.Reporsitory.UserReporsitory;
import com.pranta.BankMNG.Utilis.AccountUtils;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserReporsitory userReporsitory;
    @Autowired
    EmailService emailService;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
       //creating an account - saving a new user into database s

       //check if user already has an account
       if (userReporsitory.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
       }
       User newUser = User.builder()
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .otherName(userRequest.getOtherName())
            .gender(userRequest.getGender())
            .address(userRequest.getAddress())
            .stateOrigin(userRequest.getStateOrigin())
            .accountNumber(AccountUtils.generateAccountNumber())
            .accountBalance(BigDecimal.ZERO)
            .email(userRequest.getEmail())
            .phonenumber(userRequest.getPhonenumber())
            .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
            .status("ACTIVE")
            .build();
            
        User savedUser = userReporsitory.save(newUser);
        //Send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
            .recipient(savedUser.getEmail())
            .subject("ACCOUNT CREATION")
            .massageBody("Congratulation! Your Account Has Been Suceessfully Created.\nYour Account Details : \n"+
            "Account Name : "+savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName()+"\nAccount Number : "+savedUser.getAccountNumber())
            .build();
        emailService.sendEmailAlert(emailDetails);
            return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_SUCCESS)
                .responseCode(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                    .accountBalance(savedUser.getAccountBalance())
                    .accountNumber(savedUser.getAccountNumber())
                    .accountName(savedUser.getFirstName())
                    .build())
                .build();

          
                // balance enquiry, name enquiry, credit,debit,transfer  
   
                
        }
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // check if the provided account number exists in the db
        boolean isAccountExist = userReporsitory.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userReporsitory.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                            .accountBalance(foundUser.getAccountBalance())
                            .accountNumber(foundUser.getAccountNumber())
                            .accountName(foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName())

                            .build())
                .build();
    }
    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userReporsitory.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userReporsitory.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName();

    }
    //Credit Service
    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
       //checking if the account exist
       boolean isAccountExist = userReporsitory.existsByAccountNumber(request.getAccountNumber());
       if (!isAccountExist) {
        return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
        .accountInfo(null)
        .build();
       }
       User usertoCredit = userReporsitory.findByAccountNumber(request.getAccountNumber());
       usertoCredit.setAccountBalance(usertoCredit.getAccountBalance().add(request.getAmount()));
       userReporsitory.save(usertoCredit);

       return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(usertoCredit.getFirstName()+" "+usertoCredit.getLastName()+" "+usertoCredit.getOtherName())
                            .accountBalance(usertoCredit.getAccountBalance())
                            .accountNumber(usertoCredit.getAccountNumber())
                            .build())
                    .build();
        //Debit Service
        
    }
    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exist
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userReporsitory.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
         return BankResponse.builder()
         .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
         .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
         .accountInfo(null)
         .build();         
        } 
        User userToDebit = userReporsitory.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAccount = request.getAmount().toBigInteger();

        if(availableBalance.intValue() < debitAccount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else{
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userReporsitory.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                                .accountNumber(userToDebit.getAccountNumber())
                                .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName()+" "+userToDebit.getOtherName())
                                .accountBalance(userToDebit.getAccountBalance())

                                .build())
                    .build();
        }
    
    }
}
