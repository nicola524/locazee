package it.nicolabrogelli.imedici.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicola on 20/06/2016.
 */
public class WayPoints implements Parcelable {

    private int wayPointID;
    private String wayPointName;
    private String wayPointDescription;
    private String wayPointAddress;
    private String wayPointMarker;
    private Double wayPointLatitude;
    private Double wayPointLongitude;

    public int getWayPointID() {
        return wayPointID;
    }

    public String getWayPointName() {
        return wayPointName;
    }

    public String getWayPointDescription() {
        return wayPointDescription;
    }

    public String getWayPointMarker() {
        return wayPointMarker;
    }

    public Double getWayPointLatitude() {
        return wayPointLatitude;
    }

    public Double getWayPointLongitude() {
        return wayPointLongitude;
    }

    public String getWayPointAddress() {
        return wayPointAddress;
    }

    @Override
    public String toString() {
        return "WayPoints{" +
                "wayPointID=" + wayPointID +
                ", wayPointName='" + wayPointName + '\'' +
                ", wayPointDescription='" + wayPointDescription + '\'' +
                ", wayPointAddress='" + wayPointAddress + '\'' +
                ", wayPointMarker='" + wayPointMarker + '\'' +
                ", wayPointLatitude='" + wayPointLatitude + '\'' +
                ", wayPointLongitude='" + wayPointLongitude + '\'' +
                '}';
    }

    public static class Builder {
        private int wayPointID;
        private String wayPointName;
        private String wayPointDescription;
        private String wayPointAddress;
        private String wayPointMarker;
        private Double wayPointLatitude;
        private Double wayPointLongitude;

        public Builder setWayPointID(int wayPointID) {
            this.wayPointID = wayPointID;
            return this;
        }

        public Builder setWayPointName(String wayPointName) {
            this.wayPointName = wayPointName;
            return this;
        }

        public Builder setWayPointAddress(String wayPointAddress) {
            this.wayPointAddress = wayPointAddress;
            return this;
        }

        public Builder setWayPointDescription(String wayPointDescription) {
            this.wayPointDescription = wayPointDescription;
            return this;
        }

        public Builder setWayPointMarker(String wayPointMarker) {
            this.wayPointMarker = wayPointMarker;
            return this;
        }

        public Builder setWayPointLatitude(Double wayPointLatitude) {
            this.wayPointLatitude = wayPointLatitude;
            return this;
        }

        public Builder setWayPointLongitude(Double wayPointLongitude) {
            this.wayPointLongitude = wayPointLongitude;
            return this;
        }

        public WayPoints build() {
            return new WayPoints(this);
        }
    }

    public WayPoints() {}

    public WayPoints(Builder builder) {
        this.wayPointID = builder.wayPointID;
        this.wayPointName = builder.wayPointName;
        this.wayPointAddress = builder.wayPointAddress;
        this.wayPointDescription = builder.wayPointDescription;
        this.wayPointMarker = builder.wayPointMarker;
        this.wayPointLatitude = builder.wayPointLatitude;
        this.wayPointLongitude = builder.wayPointLongitude;
    }


    protected WayPoints(Parcel in) {
        wayPointID = in.readInt();
        wayPointName = in.readString();
        wayPointDescription = in.readString();
        wayPointAddress = in.readString();
        wayPointMarker = in.readString();
        wayPointLatitude = in.readDouble();
        wayPointLongitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(wayPointID);
        dest.writeString(wayPointName);
        dest.writeString(wayPointDescription);
        dest.writeString(wayPointAddress);
        dest.writeString(wayPointMarker);
        dest.writeDouble(wayPointLatitude);
        dest.writeDouble(wayPointLongitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WayPoints> CREATOR = new Parcelable.Creator<WayPoints>() {
        @Override
        public WayPoints createFromParcel(Parcel in) {
            return new WayPoints(in);
        }

        @Override
        public WayPoints[] newArray(int size) {
            return new WayPoints[size];
        }
    };
}