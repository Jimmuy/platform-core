package com.jimmy.interfaces;

/**
 * @author: jimmy
 * @description TODO
 * @Modification History:
 * <p>
 * Date         Author      Version     Description
 * -----------------------------------------------------------------
 * 2017/3/29     jimmy       v1.0.0        create
 **/

public interface IAccountManager {

    String getToken();

    String getCompanyId();

    String getUserId();

    void login(String username, String password);

    void logout();

    boolean isUserLogin();
}
