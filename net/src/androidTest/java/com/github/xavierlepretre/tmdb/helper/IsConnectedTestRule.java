package com.github.xavierlepretre.tmdb.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;

import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class IsConnectedTestRule implements TestRule
{
    @Override public Statement apply(final Statement base, Description description)
    {
        return new Statement()
        {
            @Override public void evaluate() throws Throwable
            {
                ConnectivityManager cm = (ConnectivityManager) InstrumentationRegistry
                        .getContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                Assume.assumeTrue(activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting());

                base.evaluate();
            }
        };
    }
}
