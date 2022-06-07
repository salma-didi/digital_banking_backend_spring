package com.example.digital_banking.Exceptions;

public class CustomerNotFound extends Exception{ //exception non surveillé (pas nécessaire d'jouter à la fonction try ... catch ou thorws)

    public CustomerNotFound(String message){
        super(message);
    }
}
