package it.nicolabrogelli.imedici.models;

/**
 * Created by Nicola on 08/06/2016.
 */
public class Itineraries {

    private int itineraryId;
    private String itineraryName;
    private String itineraryDescription;
    private String itineraryImage;
    private String itineraryMarker;
    private Double itineraryStratingLatitude;
    private Double itineraryStartingLongitude;
    private Double itineraryDestinationLatitude;
    private Double itineraryDestinationLongitude;


    public int getItineraryId() {
        return itineraryId;
    }

    public String getItineraryName() {
        return itineraryName;
    }

    public String getItineraryDescription() {
        return itineraryDescription;
    }

    public String getItineraryImage() {
        return itineraryImage;
    }

    public String getItineraryMarker() {
        return itineraryMarker;
    }

    public Double getItineraryStratingLatitude() {
        return itineraryStratingLatitude;
    }

    public Double getItineraryStartingLongitude() {
        return itineraryStartingLongitude;
    }

    public Double getItineraryDestinationLatitude() {
        return itineraryDestinationLatitude;
    }

    public Double getItineraryDestinationLongitude() {
        return itineraryDestinationLongitude;
    }



    @Override
    public String toString() {
        return "Itineraries{" +
                "itineraryId=" + itineraryId +
                ", itineraryName='" + itineraryName + '\'' +
                ", itineraryDescription='" + itineraryDescription + '\'' +
                ", itineraryImage='" + itineraryImage + '\'' +
                ", itineraryMarker='" + itineraryMarker + '\'' +
                ", itineraryStratingLatitude='" + itineraryStratingLatitude + '\'' +
                ", itineraryStartingLongitude='" + itineraryStartingLongitude + '\'' +
                ", itineraryDestinationLatitude='" + itineraryDestinationLatitude + '\'' +
                ", itineraryDestinationLongitude='" + itineraryDestinationLongitude + '\'' +
                '}';
    }

    public static class Builder {

        private int itineraryId;
        private String itineraryName;
        private String itineraryDescription;
        private String itineraryImage;
        private String itineraryMarker;
        private Double itineraryStratingLatitude;
        private Double itineraryStartingLongitude;
        private Double itineraryDestinationLatitude;
        private Double itineraryDestinationLongitude;

        public Builder setItineraryId(int itineraryId) {
            this.itineraryId = itineraryId;
            return this;
        }

        public Builder setItineraryName(String itineraryName) {
            this.itineraryName = itineraryName;
            return this;
        }

        public Builder setItineraryDescription(String itineraryDescription) {
            this.itineraryDescription = itineraryDescription;
            return this;
        }

        public Builder setItineraryImage(String itineraryImage) {
            this.itineraryImage = itineraryImage;
            return this;
        }

        public Builder setItineraryMarker(String itineraryMarker) {
            this.itineraryMarker = itineraryMarker;
            return this;
        }

        public Builder setItineraryStratingLatitude(Double itineraryStratingLatitude) {
            this.itineraryStratingLatitude = itineraryStratingLatitude;
            return this;
        }

        public Builder setItineraryStartingLongitude(Double itineraryStartingLongitude) {
            this.itineraryStartingLongitude = itineraryStartingLongitude;
            return this;
        }

        public Builder setItineraryDestinationLatitude(Double itineraryDestinationLatitude) {
            this.itineraryDestinationLatitude = itineraryDestinationLatitude;
            return this;
        }

        public Builder setItineraryDestinationLongitude(Double itineraryDestinationLongitude) {
            this.itineraryDestinationLongitude = itineraryDestinationLongitude;
            return this;
        }



        public Itineraries build() {
            return new Itineraries(this);
        }
    }

    public Itineraries() {}

    public Itineraries(Builder builder) {
        itineraryId                     = builder.itineraryId;
        itineraryName                   = builder.itineraryName;
        itineraryDescription            = builder.itineraryDescription;
        itineraryImage                  = builder.itineraryImage;
        itineraryMarker                 = builder.itineraryMarker;
        itineraryStratingLatitude       = builder.itineraryStratingLatitude;
        itineraryStartingLongitude      = builder.itineraryStartingLongitude;
        itineraryDestinationLatitude    = builder.itineraryDestinationLatitude;
        itineraryDestinationLongitude   = builder.itineraryDestinationLongitude;

    }

}
