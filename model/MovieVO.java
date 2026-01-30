package model;

/**
 * 영화 정보
 * JComboBox 표시를 위해 toString은 title로 반환
 */
public class MovieVO {
    private int movieId;
    private String title;
    private String posterPath;
    
    private boolean active =true;

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    
    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}
    

		@Override
    public String toString() { 
    	
    	return active ? title : (title + "(비활성)" ); 
    	}
}
