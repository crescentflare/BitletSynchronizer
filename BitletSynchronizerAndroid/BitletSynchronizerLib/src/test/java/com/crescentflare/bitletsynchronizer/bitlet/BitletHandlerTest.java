package com.crescentflare.bitletsynchronizer.bitlet;

import org.junit.Assert;
import org.junit.Test;

/**
 * Bitlet test: handler
 */
public class BitletHandlerTest
{
    // ---
    // Loading test
    // ---

    @Test
    public void testLoad() throws Exception
    {
        new BitletHandlerSample("Test").load(new BitletResultObserver<>(new BitletResultObserver.SimpleCompletionListener<String>()
        {
            @Override
            public void onFinish(String value, Throwable exception)
            {
                Assert.assertEquals(value, "Test");
            }
        }));
    }


    // ---
    // Helper class
    // ---

    private static class BitletHandlerSample implements BitletHandler<String>
    {
        private String generateValue;

        public BitletHandlerSample(String generateValue)
        {
            this.generateValue = generateValue;
        }

        @Override
        public void load(BitletObserver<String> observer)
        {
            observer.setBitlet(generateValue);
            observer.finish();
        }
    }
}
