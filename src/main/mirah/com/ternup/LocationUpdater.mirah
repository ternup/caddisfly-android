import android.locatoin.LocationListener
import android.location.Location
import com.ternup.CaddisflyReader

class LocationUpdater < LocationListener
    def initialize(act:CaddisflyReader)
        @activity = act
    end

    def onLocationChanged(location:Location)
        @activity.updateLocation(location)
    end
end
