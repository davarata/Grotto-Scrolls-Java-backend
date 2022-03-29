package toit.du.tee.grsc.rest;

import java.time.LocalDateTime;

public record Content(Long id, String title, String content, LocalDateTime created, LocalDateTime modified, LocalDateTime accessed) {
	
	public Content(String title, String content, LocalDateTime created, LocalDateTime modified, LocalDateTime accessed) {
		this(null, title, content, created, modified, accessed);
	}
	
}
