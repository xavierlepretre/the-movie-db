package com.github.xavierlepretre.tmdb.net;

import com.squareup.okhttp.Interceptor.Chain;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RateLimitingInterceptorTest
{
    private Request.Builder fakeRequestBuilder;
    private Response.Builder fakeResponseBuilder;
    private Chain mockedChain;

    @Before
    public void setUp() throws Exception
    {
        fakeRequestBuilder = new Request.Builder().url("http://yahoo.com");
        fakeResponseBuilder = new Response.Builder()
                .code(1)
                .protocol(Protocol.HTTP_1_1);
        mockedChain = mock(Chain.class);
    }

    @Test
    public void whenNoRemaining_waitsTheSecondsToResetDate() throws Exception
    {
        Request request = fakeRequestBuilder.build();
        Response response = fakeResponseBuilder
                .request(request).build();
        when(mockedChain.request()).thenReturn(request);
        when(mockedChain.proceed(any(Request.class))).thenReturn(response);

        Calendar resetDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        resetDate.add(Calendar.SECOND, 3);
        RateLimitingInterceptor interceptor = new RateLimitingInterceptor(
                null,
                new AtomicInteger(0),
                new AtomicLong(resetDate.getTimeInMillis() / 1000));

        long before = System.currentTimeMillis();
        interceptor.intercept(mockedChain);
        long after = System.currentTimeMillis();
        assertThat(after - before).isGreaterThan(1000);
    }

    @Test
    public void whenNoHeader_doesNotUpdateRemainingFields() throws Exception
    {
        Request request = fakeRequestBuilder.build();
        Response response = fakeResponseBuilder
                .request(request).build();
        when(mockedChain.request()).thenReturn(request);
        when(mockedChain.proceed(any(Request.class))).thenReturn(response);

        Logger logger = mock(Logger.class);
        AtomicInteger remaining = new AtomicInteger(10);
        AtomicLong remainingReset = new AtomicLong(12345);
        new RateLimitingInterceptor(
                logger,
                remaining,
                remainingReset)
                .intercept(mockedChain);

        verify(logger).log(eq(Level.SEVERE), anyString(), any(NumberFormatException.class));
        assertThat(remaining.get()).isEqualTo(10);
        assertThat(remainingReset.get()).isEqualTo(12345);
    }

    @Test
    public void whenReceiveHeaders_updatesTheRemainingFields() throws Exception
    {
        Calendar resetDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        resetDate.add(Calendar.SECOND, 7);
        String headerReset = String.valueOf(resetDate.getTimeInMillis() / 1000);

        Request request = fakeRequestBuilder.build();
        Response response = fakeResponseBuilder
                .header(TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING, "20")
                .header(
                        TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING_RESET,
                        headerReset)
                .request(request).build();
        when(mockedChain.request()).thenReturn(request);
        when(mockedChain.proceed(any(Request.class))).thenReturn(response);

        AtomicInteger remaining = new AtomicInteger();
        AtomicLong remainingReset = new AtomicLong();
        new RateLimitingInterceptor(
                null,
                remaining,
                remainingReset)
                .intercept(mockedChain);

        assertThat(remaining.get()).isEqualTo(20);
        assertThat(remainingReset.get()).isEqualTo(resetDate.getTimeInMillis() / 1000);
    }

    @Test
    public void whenReceiveMalformedHeaders_doesNotUpdate() throws Exception
    {
        Request request = fakeRequestBuilder.build();
        Response response = fakeResponseBuilder
                .header(TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING, "fake result")
                .header(
                        TmdbConstants.RESPONSE_HEADER_REQUEST_REMAINING_RESET,
                        "fake result")
                .request(request).build();
        when(mockedChain.request()).thenReturn(request);
        when(mockedChain.proceed(any(Request.class))).thenReturn(response);

        Logger logger = mock(Logger.class);
        AtomicInteger remaining = new AtomicInteger(1);
        AtomicLong remainingReset = new AtomicLong(1);
        new RateLimitingInterceptor(
                logger,
                remaining,
                remainingReset).intercept(mockedChain);

        verify(logger).log(eq(Level.SEVERE), anyString(), any(NumberFormatException.class));
        assertThat(remaining.get()).isEqualTo(1);
        assertThat(remainingReset.get()).isEqualTo(1);
    }
}
