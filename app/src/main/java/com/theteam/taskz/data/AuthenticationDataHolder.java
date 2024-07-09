package com.theteam.taskz.data;

import com.theteam.taskz.enums.AccountType;

public class AuthenticationDataHolder {
    public static String firstName,lastName,email,password, dob;
    public static AccountType selecAccountType;

    public static void clear(){
        firstName = null;
        lastName = null;
        selecAccountType = null;
        email = null;
        password = null;
        dob = null;
    }
}
