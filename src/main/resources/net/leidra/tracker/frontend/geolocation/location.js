/**
 * Created by andyphillips404 on 6/9/15.
 */

window.net_leidra_tracker_frontend_geolocation_Location = function() {

    var self = this;

    this.location = function() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function (position) {
                    var latitude = position.coords.latitude;
                    var longitude = position.coords.longitude;
                    var accuracy = position.coords.accuracy;
                    self.onLocationFound(latitude, longitude, accuracy);
                },
                function (error) {
                    self.onLocationError(error.code);
                }
            );
        }
        else {
            self.onLocationNotSupported();
        }
    };



};