package org.concurrency;

class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println("Exception handled in thread " + t.getName() + ": " + e.getMessage());
        e.printStackTrace();
    }
}
