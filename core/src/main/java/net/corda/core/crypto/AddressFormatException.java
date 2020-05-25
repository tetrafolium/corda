package net.corda.core.crypto;

public class AddressFormatException extends IllegalArgumentException {
    public AddressFormatException() {
        super();
    }

    public AddressFormatException(final String message) {
        super(message);
    }
}
