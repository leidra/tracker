package net.leidra.tracker.vaadin.geolocation;

/**
 * Created by andyphillips404 on 6/9/15.
 */
public enum  LocationError {
    /**
     * Requesting location information failed for an unknown reason.
     */
    UNKNOWN_ERROR(0),

    /**
     * The location acquisition process failed because the application origin
     * does not have permission to use the Geolocation API.
     */
    PERMISSION_DENIED(1),

    /**
     * The position of the device could not be determined. For instance, one or
     * more of the location providers used in the location acquisition process
     * reported an internal error that caused the process to fail entirely.
     */
    POSITION_UNAVAILABLE(2),

    /**
     * The length of time specified by the timeout property has elapsed before
     * the implementation could successfully acquire a new Position object.
     */
    TIMEOUT(3);

    private final int errorCode;

    private LocationError(int errorCode) {
        this.errorCode = errorCode;
    }

    static LocationError getErrorForCode(int errorCode) {
        for (LocationError error : LocationError.values()) {
            if (error.errorCode == errorCode) {
                return error;
            }
        }
        return UNKNOWN_ERROR;
    }
}
