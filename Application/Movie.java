package Application;

public class Movie {
    public String getName() {
        return name;
    }

    public Movie setName(String name) {
        this.name = name;
        return this;
    }

    public long getRating() {
        return rating;
    }

    public Movie setRating(long rating) {
        this.rating = rating;
        return this;
    }

    public Movie(String name, long rating) {
        this.name = name;
        this.rating = rating;
    }

    private String name;

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", rating=" + rating +
                '}';
    }

    private long rating;
}
