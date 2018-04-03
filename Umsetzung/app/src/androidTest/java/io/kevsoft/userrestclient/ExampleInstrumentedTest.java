package io.kevsoft.userrestclient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.MutableBoolean;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.kevsoft.userrestclient", appContext.getPackageName());
    }

    @Test
    public void loginAndRegister() throws InterruptedException {
        final Object smallLock = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable task = () -> {
            String username = "TestUser_" + UUID.randomUUID().toString();
            String email = "TestEmail_" + UUID.randomUUID().toString() + "@example.com";
            String password = "TestPassword_" + UUID.randomUUID().toString();

            // Register
            RESTExecutor.ExecuteRegisterRequest(username, email, password, () -> {
                // First try login with invalid password:
                RESTExecutor.ExecuteLoginRequest(username, "InvalidPW", () -> {
                    fail("User logged in with invalid PW");
                    synchronized (smallLock) {
                        smallLock.notifyAll();
                    }
                }, errorMsg -> {
                    assertEquals("Wrong error message", "Invalid password, try again!", errorMsg);
                    // Now with correct password
                    RESTExecutor.ExecuteLoginRequest(username, password, () -> {
                        synchronized (smallLock) {
                            smallLock.notifyAll();
                        }
                    }, errorText -> {
                        fail("Error on login execution: " + errorText);
                        synchronized (smallLock) {
                            smallLock.notifyAll();
                        }
                    });
                });


            }, errorText -> {
                fail("Error on register execution: " + errorText);
                synchronized (smallLock) {
                    smallLock.notifyAll();
                }
            });
        };
        handler.post(task);

        synchronized (smallLock) {
            smallLock.wait();
        }
    }


    @Test
    public void tryLoginInvalidUser() throws InterruptedException {
        final Object smallLock = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable task = () -> {
            String username = "TestUser_" + UUID.randomUUID().toString();
            String password = "TestPassword_" + UUID.randomUUID().toString();

            // Register
            RESTExecutor.ExecuteLoginRequest(username, password, () -> {
                fail("User shouldn't exist");
                synchronized (smallLock) {
                    smallLock.notifyAll();
                }
            }, errorText -> {
                assertEquals("Wrong error message", "User does not exist!", errorText);
                synchronized (smallLock) {
                    smallLock.notifyAll();
                }
            });

        };
        handler.post(task);

        synchronized (smallLock) {
            smallLock.wait();
        }
    }



}
