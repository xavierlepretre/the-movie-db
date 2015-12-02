package com.github.xavierlepretre.tmdb.sync;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TmdbAuthenticatorTest
{
    private TmdbAuthenticator authenticator;

    @Before
    public void setUp() throws Exception
    {
        authenticator = new TmdbAuthenticator(InstrumentationRegistry.getContext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void editProperties_isDummy() throws Exception
    {
        authenticator.editProperties(mock(AccountAuthenticatorResponse.class), "");
    }

    @Test public void addAccount_isDummy() throws Exception
    {
        assertThat(authenticator.addAccount(
                mock(AccountAuthenticatorResponse.class),
                "",
                "",
                new String[0],
                new Bundle()))
                .isNull();
    }

    @Test public void confirmCredentials_isDummy() throws Exception
    {
        assertThat(authenticator.confirmCredentials(
                mock(AccountAuthenticatorResponse.class),
                null,
                new Bundle()))
                .isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAuthToken_isDummy() throws Exception
    {
        authenticator.getAuthToken(mock(AccountAuthenticatorResponse.class),
                mock(Account.class),
                "",
                new Bundle());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAuthTokenLabel_isDummy() throws Exception
    {
        authenticator.getAuthTokenLabel("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updateCredentials_isDummy() throws Exception
    {
        authenticator.updateCredentials(mock(AccountAuthenticatorResponse.class),
                mock(Account.class),
                "",
                new Bundle());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void hasFeatures_isDummy() throws Exception
    {
        authenticator.hasFeatures(mock(AccountAuthenticatorResponse.class),
                mock(Account.class),
                new String[0]);
    }
}