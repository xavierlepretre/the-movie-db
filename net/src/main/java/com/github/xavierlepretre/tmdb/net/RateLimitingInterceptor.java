package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The API limits your number of requests.
 * http://docs.themoviedb.apiary.io/introduction/request-rate-limiting
 */
public class RateLimitingInterceptor implements Interceptor
{
    @Nullable private final Logger logger;
    @NonNull private final AtomicInteger remainingRequests;
    // Contains the date in seconds.
    @NonNull private final AtomicLong resetDate;

    public RateLimitingInterceptor()
    {
        this(null, new AtomicInteger(Integer.MAX_VALUE), new AtomicLong(0));
    }

    public RateLimitingInterceptor(
            @Nullable Logger logger,
            @NonNull AtomicInteger remainingRequests,
            @NonNull AtomicLong resetDate)
    {
        this.logger = logger;
        this.remainingRequests = remainingRequests;
        this.resetDate = resetDate;
    }

    @Override public Response intercept(@NonNull Chain chain) throws IOException
    {
        Request request = chain.request();
        if (remainingRequests.get() == 0)
        {
            long toWait = resetDate.get() * 1000 - Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
            if (toWait > 0)
            {
                try
                {
                    Thread.sleep(toWait);
                }
                catch (InterruptedException e)
                {
                    if (logger != null)
                    {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        }
        Response response = chain.proceed(request);
        try
        {
            remainingRequests.set(Integer.parseInt(
                    response.header(TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING)));
            // We do not care about the reset date if we could not read the remaining requests.
            resetDate.set(Long.parseLong(
                    response.header(TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING_RESET)));
        }
        catch (NumberFormatException e)
        {
            if (logger != null)
            {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return response;
    }

    public int getRemainingRequests()
    {
        return remainingRequests.get();
    }

    @NonNull public Date getResetDate()
    {
        return new Date(resetDate.get() * 1000);
    }
}
