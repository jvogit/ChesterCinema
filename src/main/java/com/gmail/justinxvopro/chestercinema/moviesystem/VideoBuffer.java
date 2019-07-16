package com.gmail.justinxvopro.chestercinema.moviesystem;

import java.util.Optional;

import com.gmail.justinxvopro.chestercinema.Loggable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class VideoBuffer implements Loggable{
    @Setter
    @Getter
    private int FRAME_DURATION = MoviePlayer.SEEK_DURATION_IN_SECONDS;
    private String[] BUFFER;
    
    public void setBuffer(String[] buffer) {
	this.BUFFER = buffer;
    }
    
    public int size() {
	return BUFFER.length;
    }
    
    public void add(int index, String url) {
	logger().info("{} add {}", index, url);
	BUFFER[index] = url;
    }
    
    public Optional<String> get(int index) {
	logger().info(index + " from buffer " + BUFFER[index]);
	return index < BUFFER.length ? Optional.ofNullable(BUFFER[index]) : Optional.ofNullable(null);
    }
    
    public boolean allFilled() {
	for(String s : BUFFER)
	    if(s == null) return false;
	
	return true;
    }
    
    public static class SavedVideoBuffer {
	@Getter
	@Setter
	private int frame_duration;
	@Getter
	@Setter
	private String[] buffer;
	
	public static SavedVideoBuffer toImmutableSave(VideoBuffer buffer) {
	    var sb = new SavedVideoBuffer();
	    sb.frame_duration = buffer.FRAME_DURATION;
	    sb.buffer = buffer.BUFFER;
	    
	    return sb;
	}
	
	public static VideoBuffer fromSave(SavedVideoBuffer sb) {
	    VideoBuffer buffer = new VideoBuffer();
	    buffer.FRAME_DURATION = sb.frame_duration;
	    buffer.BUFFER = sb.buffer;
	    
	    return buffer;
	}
    }
}
