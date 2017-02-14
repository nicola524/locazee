package it.nicolabrogelli.imedici.models;

/**
 * Created by Nicola on 06/05/2016.
 */
public class Locations {

    private int locationId;
    private int categoryId;
    private int charactersId;
    private String locationeName;
    private String locationAddress;
    private String locationDescription;
    private String locationImage;
    private Double locationLatitude;
    private Double locationLongitude;
    private String locationPhone;
    private String locationWebSite;
    private String locationWebPage;
    private String categoryName;
    private String categoryMarker;


    public int getLocationId() {
        return locationId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getCharactersId() {
        return charactersId;
    }

    public String getLocationeName() {
        return locationeName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public String getLocationPhone() {
        return locationPhone;
    }

    public String getLocationWebSite() {
        return locationWebSite;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryMarker() {
        return categoryMarker;
    }

    public String getLocationWebPage() {
        return locationWebPage;
    }


    @Override
    public String toString() {
        return "Locations{" +
                "locationId=" + locationId +
                ", categoryId=" + categoryId +
                ", charactersId=" + charactersId +
                ", locationeName='" + locationeName + '\'' +
                ", locationAddress='" + locationAddress + '\'' +
                ", locationDescription='" + locationDescription + '\'' +
                ", locationImage='" + locationImage + '\'' +
                ", locationLatitude='" + locationLatitude + '\'' +
                ", locationLongitude='" + locationLongitude + '\'' +
                ", locationPhone='" + locationPhone + '\'' +
                ", locationWebSite='" + locationWebSite + '\'' +
                ", locationWebPage='" + locationWebPage + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryMarker='" + categoryMarker + '\'' +
                '}';
    }

    public static class Builder {

        int locationId;
        int categoryId;
        int charactersId;
        String locationeName;
        String locationAddress;
        String locationDescription;
        String locationImage;
        Double locationLatitude;
        Double locationLongitude;
        String locationPhone;
        String locationWebSite;
        String locationWebPage;
        String categoryName;
        String categoryMarker;

        public Builder setLocationId(int locationId) {
            this.locationId = locationId;
            return this;
        }

        public Builder setCategoryId(int categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder setCharactersId(int charactersId) {
            this.charactersId = charactersId;
            return this;
        }

        public Builder setLocationeName(String locationeName) {
            this.locationeName = locationeName;
            return this;
        }

        public Builder setLocationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
            return this;
        }

        public Builder setLocationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
            return this;
        }

        public Builder setLocationImage(String locationImage) {
            this.locationImage = locationImage;
            return this;
        }

        public Builder setLocationLatitude(Double locationLatitude) {
            this.locationLatitude = locationLatitude;
            return this;
        }

        public Builder setLocationLongitude(Double locationLongitude) {
            this.locationLongitude = locationLongitude;
            return this;
        }

        public Builder setLocationPhone(String locationPhone) {
            this.locationPhone = locationPhone;
            return this;
        }

        public Builder setLocationWebSite(String locationWebSite) {
            this.locationWebSite = locationWebSite;
            return this;
        }

        public Builder setCategoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Builder setCategoryMarker(String categoryMarker) {
            this.categoryMarker = categoryMarker;
            return this;
        }

        public Builder setLocationWebPage(String locationWebPage) {
            this.locationWebPage = locationWebPage;
            return this;
        }

        public Locations bild() {
            return new Locations(this);
        }
    }

    public Locations(){}

    public Locations(Builder builder) {

        locationId = builder.locationId;
        categoryId = builder.categoryId;
        charactersId = builder.charactersId;
        locationeName = builder.locationeName;
        locationAddress = builder.locationAddress;
        locationDescription = builder.locationDescription;
        locationImage = builder.locationImage;
        locationLatitude = builder.locationLatitude;
        locationLongitude = builder.locationLongitude;
        locationPhone = builder.locationPhone;
        locationWebSite = builder.locationWebSite;
        locationWebPage = builder.locationWebPage;
        categoryMarker = builder.categoryMarker;
        categoryName = builder.categoryName;
    }


}
