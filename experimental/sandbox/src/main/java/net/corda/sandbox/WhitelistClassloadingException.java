package net.corda.sandbox;

/**
 *
 */
public class WhitelistClassloadingException extends Exception {

    public WhitelistClassloadingException() {
        super();
    }

    public WhitelistClassloadingException(final String message) {
        super(message);
    }

    public WhitelistClassloadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WhitelistClassloadingException(final Throwable cause) {
        super(cause);
    }

    protected WhitelistClassloadingException(final String message, final Throwable cause,
                                             final boolean enableSuppression,
                                             final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
