package it.nicolabrogelli.imedici.models;

/**
 * Created by Nicola on 06/05/2016.
 */
public class Categories {

    int categoryId;
    String categoryName;
    String categoryMarker;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryMarker() {
        return categoryMarker;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryMarker='" + categoryMarker + '\'' +
                '}';
    }

    public static class Builder {
        int categoryId;
        String categoryName;
        String categoryMarker;

        public Builder setCategoryId(int categoryId) {
            this.categoryId = categoryId;
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

        public Categories biuld() {
            return new Categories(this);
        }
    }

    public Categories() {}

    public Categories(Builder builder) {
        categoryId = builder.categoryId;
        categoryName = builder.categoryName;
        categoryMarker = builder.categoryMarker;
    }

}
