package com.opinion.authentication;

import com.google.common.base.Optional;
import com.opinion.models.UserDetails;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Created by hritupon on 9/18/2015.
 */
public class OpinionAuthenticator  implements Authenticator<BasicCredentials, UserDetails> {
    @Override
    public Optional<UserDetails> authenticate(BasicCredentials credentials)
            throws AuthenticationException {
        String userName=credentials.getUsername();
        //based on this userName check for the password from the
        //server
        if ("opinion".equals(credentials.getPassword())) {
            return Optional.of(new UserDetails());
        } else {
            return Optional.absent();
        }
    }
}