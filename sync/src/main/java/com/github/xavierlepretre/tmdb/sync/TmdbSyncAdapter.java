package com.github.xavierlepretre.tmdb.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.sync.R;
import com.github.xavierlepretre.tmdb.sync.movie.GenreSyncAdapter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TmdbSyncAdapter extends AbstractThreadedSyncAdapter
{
    private static final String TAG = TmdbSyncAdapter.class.getSimpleName();

    @NonNull private final GenreSyncAdapter genreSyncAdapter;
    @NonNull private final Logger logger;

    public TmdbSyncAdapter(@NonNull Context context,
                           boolean autoInitialize,
                           @NonNull GenreSyncAdapter genreSyncAdapter,
                           @NonNull Logger logger)
    {
        super(context, autoInitialize);
        this.genreSyncAdapter = genreSyncAdapter;
        this.logger = logger;
    }

    @Override public void onPerformSync(
            Account account,
            @NonNull Bundle extras,
            String authority,
            @NonNull ContentProviderClient provider,
            @NonNull SyncResult syncResult)
    {
        try
        {
            genreSyncAdapter.onPerformSync(account, extras, authority, provider, syncResult);
        }
        catch (IOException | RemoteException e)
        {
            logger.log(Level.SEVERE, TAG + ", failed to sync Genres", e);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(@NonNull Context context, @NonNull Bundle extras)
    {
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                getSyncAccount(context),
                context.getString(R.string.content_authority),
                extras);
    }

    public static void initializeSyncAdapter(
            @NonNull Context context)
    {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    @Nullable public static Account getSyncAccount(@NonNull Context context)
    {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null)
        {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
            {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(context, newAccount);
        }
        return newAccount;
    }

    private static void onAccountCreated(
            @NonNull Context context,
            @NonNull Account newAccount)
    {
    }
}
